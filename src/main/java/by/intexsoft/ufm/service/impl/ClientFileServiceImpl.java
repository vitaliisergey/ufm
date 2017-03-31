package by.intexsoft.ufm.service.impl;

import by.intexsoft.ufm.model.Client;
import by.intexsoft.ufm.service.ClientFileService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Set;

/**
 * Base implements {@link ClientFileService}
 */
@Service("ClientFileService")
public class ClientFileServiceImpl implements ClientFileService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientFileServiceImpl.class);
	@Value("${inbox.filename.template:{0}.json}")
	private String fileNameTemplate;
	@Value("${inbox.folder.name:inbox}")
	private String inboxFolder;
	@Value("${outbox.folder.name:outbox}")
	private String outboxFolder;

	@PostConstruct
	private void postConstructor() throws IOException
	{
		File dir = Paths.get(outboxFolder).toFile();
		if (!dir.exists())
		{
			LOGGER.info("Create outbox folder: {}", outboxFolder);
			FileUtils.forceMkdir(dir);
		}
		dir = Paths.get(inboxFolder).toFile();
		if (!dir.exists())
		{
			LOGGER.info("Create inbox folder: {}", inboxFolder);
			FileUtils.forceMkdir(dir);
		}
	}

	@Override
	public void deleteInput(Set<Client> clients)
	{
		for (Client client : clients)
		{
			File file = Paths.get(inboxFolder, MessageFormat.format(fileNameTemplate, client.clientId.toString())).toFile();
			try
			{
				FileUtils.forceDelete(file);
			}
			catch (IOException e)
			{
				LOGGER.error("Error of deletion file.", e);
			}
		}
	}

	@Override
	public File recreateOutput(Long clientId) throws IOException
	{
		File file = Paths.get(outboxFolder, MessageFormat.format(fileNameTemplate, clientId)).toFile();
		file.createNewFile();
		return file;
	}
}
