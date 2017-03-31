package by.intexsoft.ufm.service.impl;

import by.intexsoft.ufm.model.Client;
import by.intexsoft.ufm.service.ClientFileService;
import by.intexsoft.ufm.service.ClientProcessorService;
import by.intexsoft.ufm.service.KieProcessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * Base implements {@link ClientProcessorService}
 */
@Service
@DependsOn("ClientFileService")
public class ClientProcessorServiceImpl implements ClientProcessorService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientProcessorServiceImpl.class);
	private static final String JSON_EXTENSION = ".json";
	private final ObjectMapper objectMapper;
	private final KieProcessorService kieProcessorService;
	private final ClientFileService clientFileService;
	@Value("${max.client.side:100}")
	private long maxClientSide;
	@Value("${inbox.folder.name:data/inbox}")
	private String inboxFolder;

	/**
	 * Constructor for inject needed dependencies
	 */
	@Autowired
	public ClientProcessorServiceImpl(ObjectMapper objectMapper, KieProcessorService kieProcessorService,
		ClientFileService clientFileService)
	{
		this.objectMapper = objectMapper;
		this.kieProcessorService = kieProcessorService;
		this.clientFileService = clientFileService;
	}

	@Override
	public void start(BlockingQueue<String> names)
	{
		Set<Client> clients = new HashSet<>();
		for (; ; )
		{
			oneStart(names, clients);
		}
	}

	@Override
	public void oneStart(BlockingQueue<String> names, Set<Client> clients)
	{
		try
		{
			String name = names.take();
			Client file = readFile(name);
			if (file != null)
			{
				LOGGER.info("Add client to hash: {}", name);
				clients.add(file);
			}
			if (names.isEmpty() || clients.size() == maxClientSide)
			{
				generate(clients);
			}
		}
		catch (InterruptedException e)
		{
			LOGGER.error("Cant process client data.", e);
		}
	}

	private void generate(Set<Client> clients)
	{
		try
		{
			kieProcessorService.generate(clients);
		}
		catch (Exception e)
		{
			LOGGER.error("Error: ", e);
		}
		finally
		{
			clientFileService.deleteInput(clients);
			clients.clear();
		}
	}

	private Client readFile(String name)
	{
		Client client;
		try
		{
			Path file = Paths.get(inboxFolder, name);
			client = objectMapper.readValue(file.toFile(), Client.class);
			if (!(client.getClientId() + JSON_EXTENSION).equals(name))
			{
				LOGGER.warn("Name {} does not match clientId {}", name, client.getClientId());
				client = null;
			}
		}
		catch (IOException e)
		{
			client = null;
			LOGGER.error("{} is not valid client json", name, e);
		}
		return client;
	}
}
