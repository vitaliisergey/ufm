package by.intexsoft.ufm.service.impl;

import by.intexsoft.ufm.service.ClientFileService;
import by.intexsoft.ufm.service.OutputGeneratorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Base implements {@link OutputGeneratorService}
 */
@Service("OutputGeneratorService")
public class OutputGeneratorServiceImpl implements OutputGeneratorService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(OutputGeneratorServiceImpl.class);
	private static final String CLIENT_ID = "clientId";
	private static final String SPENT_TOTAL = "spentTotal";
	private static final String IS_BIG = "isBig";
	private final ObjectMapper objectMapper;
	private final ClientFileService clientFileService;

	/**
	 * Constructor for inject needed dependencies
	 */
	@Autowired
	public OutputGeneratorServiceImpl(ObjectMapper objectMapper, ClientFileService clientFileService)
	{
		this.objectMapper = objectMapper;
		this.clientFileService = clientFileService;
	}

	@Override
	public void generate(Long clientId, Number total, boolean big)
	{
		try
		{
			ObjectNode objectNode = objectMapper.createObjectNode();
			objectNode.put(CLIENT_ID, clientId);
			objectNode.put(SPENT_TOTAL, total.longValue());
			objectNode.put(IS_BIG, big);
			objectMapper.writeValue(clientFileService.recreateOutput(clientId), objectNode);
		}
		catch (Exception e)
		{
			LOGGER.error("Error of writing outbox file for client: {} .", clientId, e);
		}
	}
}
