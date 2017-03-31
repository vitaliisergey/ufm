package by.intexsoft.ufm.service;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import by.intexsoft.ufm.model.Client;

/**
 * Service for working with files
 */
public interface ClientFileService
{
	/**
	 * Delete input client files
	 */
	void deleteInput(Set<Client> clients);

	/**
	 * Create new output file for client
	 */
	File recreateOutput(Long clientId)throws IOException;
}
