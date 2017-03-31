package by.intexsoft.ufm.service.impl;

import by.intexsoft.ufm.service.KieProcessorService;
import by.intexsoft.ufm.service.OutputGeneratorService;
import org.drools.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * Base implements {@link KieProcessorService}
 */
@Service
public class KieProcessorServiceImpl implements KieProcessorService
{
	private static final Logger LOGGER = LoggerFactory.getLogger(KieProcessorServiceImpl.class);
	private final StatelessKnowledgeSession statelessKnowledgeSession;
	private final OutputGeneratorService outputGeneratorService;

	/**
	 * Constructor for inject needed dependencies
	 */
	@Autowired
	public KieProcessorServiceImpl(@Qualifier("clientSession") StatelessKnowledgeSession statelessKnowledgeSession,
		OutputGeneratorService outputGeneratorService)
	{
		this.statelessKnowledgeSession = statelessKnowledgeSession;
		this.outputGeneratorService = outputGeneratorService;
	}

	@PostConstruct
	private void postInit()
	{
		statelessKnowledgeSession.setGlobal("logger", LOGGER);
		statelessKnowledgeSession.setGlobal("generator", outputGeneratorService);
	}

	@Override
	public <T> void generate(Set<T> set)
	{
		LOGGER.info("Running rules");
		statelessKnowledgeSession.execute(set);
		LOGGER.info("Finished running");
	}
}