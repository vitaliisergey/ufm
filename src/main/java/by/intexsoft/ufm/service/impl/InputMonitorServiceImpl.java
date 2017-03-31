package by.intexsoft.ufm.service.impl;

import by.intexsoft.ufm.service.InputMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Pattern;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Service
@DependsOn("ClientFileService")
public class InputMonitorServiceImpl implements InputMonitorService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(InputMonitorServiceImpl.class);
	private static final String INBOX_FILENAME_PATTERN = "\\d+\\.json";
	@Value("${inbox.folder.name:data/inbox}")
	private String inputDir;
	private WatchService watcher;
	private Map<WatchKey, Path> keys;
	private boolean trace = false;

	@PostConstruct
	private void init() throws IOException
	{
		LOGGER.info("Register monitor for folder: {}", inputDir);
		this.watcher = FileSystems.getDefault().newWatchService();
		this.keys = new HashMap<>();
		register(Paths.get(inputDir));
		this.trace = true;
	}

	@Async
	@Override
	public void start(Queue<String> clientIds)
	{
		LOGGER.info("Start monitor for folder: {}", inputDir);
		processExistingClients(clientIds);
		processEvents(clientIds);
	}

	@SuppressWarnings("unchecked")
	static private <T> WatchEvent<T> cast(WatchEvent<?> event)
	{
		return (WatchEvent<T>) event;
	}

	private void register(Path dir) throws IOException
	{
		WatchKey key = dir.register(watcher, ENTRY_CREATE);
		if (trace)
		{
			Path prev = keys.get(key);
			if (prev == null)
			{
				LOGGER.info(String.format("Register: %s\n", dir));
			}
			else
			{
				if (!dir.equals(prev))
				{
					LOGGER.info(String.format("Update: %s -> %s\n", prev, dir));
				}
			}
		}
		keys.put(key, dir);
	}

	private void processExistingClients(Queue<String> names)
	{
		Path inboxDir = Paths.get(inputDir);
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(inboxDir))
		{
			for (Path client : dirStream)
			{
				addName(names, client);
			}
		}
		catch (IOException e)
		{
			LOGGER.error("Cant get list files ", e);
		}
	}

	private void addName(Queue<String> names, Path client)
	{
		String fileName = client.getFileName().toString();
		if (Pattern.matches(INBOX_FILENAME_PATTERN, fileName))
		{
			LOGGER.info("Add client:" + client.getFileName().toString());
			names.add(fileName);
		}
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	private void processEvents(Queue<String> names)
	{
		for (; ; )
		{
			WatchKey key;
			try
			{
				key = watcher.take();
			}
			catch (InterruptedException x)
			{
				return;
			}
			Path dir = keys.get(key);
			if (dir == null)
			{
				LOGGER.error("WatchKey not recognized!!");
				continue;
			}
			for (WatchEvent<?> event : key.pollEvents())
			{
				WatchEvent.Kind kind = event.kind();
				// TBD - provide example of how OVERFLOW event is handled
				if (kind == OVERFLOW)
				{
					continue;
				}
				// Context for directory entry event is the file name of entry
				WatchEvent<Path> ev = cast(event);
				Path name = ev.context();
				addName(names, name);
			}
			// reset key and remove from set if directory no longer accessible
			boolean valid = key.reset();
			if (!valid)
			{
				keys.remove(key);
				// all directories are inaccessible
				if (keys.isEmpty())
				{
					break;
				}
			}
		}
	}
}
