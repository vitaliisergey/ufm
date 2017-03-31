package by.intexsoft.ufm.service;

import java.util.Queue;

/**
 * Monitor for FS
 */
public interface InputMonitorService
{
	/**
	 * Start monitoring
	 */
	void start(Queue<String> clientIds);
}
