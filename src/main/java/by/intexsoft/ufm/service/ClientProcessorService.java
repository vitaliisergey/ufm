package by.intexsoft.ufm.service;

import java.util.Set;
import java.util.concurrent.BlockingQueue;

import by.intexsoft.ufm.model.Client;
import org.springframework.scheduling.annotation.Async;

/**
 * Service for processing client input data
 */
public interface ClientProcessorService
{
	/**
	 * Start processing files from queue
	 */
	void start(BlockingQueue<String> names);

	/**
	 * Using for testing
	 */
	void oneStart(BlockingQueue<String> names, Set<Client> clients);
}
