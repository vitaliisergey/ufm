package by.intexsoft.ufm;

import by.intexsoft.ufm.config.ApplicationConfig;
import by.intexsoft.ufm.service.ClientProcessorService;
import by.intexsoft.ufm.service.InputMonitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * General application
 */
public class StartApplication
{
	private static final Logger LOGGER = LoggerFactory.getLogger(StartApplication.class);

	/**
	 * Main method for start application
	 */
	public static void main(String[] args) throws Exception
	{
		LOGGER.info("Start application...");
		Queue<String> names = new LinkedBlockingQueue<>();
		ApplicationContext context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
		InputMonitorService inputMonitorService = context.getBean(InputMonitorService.class);
		ClientProcessorService clientProcessor = context.getBean(ClientProcessorService.class);
		inputMonitorService.start(names);
		LOGGER.info("Application started");
		clientProcessor.start((BlockingQueue) names);
	}
}
