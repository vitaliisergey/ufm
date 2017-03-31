package by.intexsoft.ufm.service;

/**
 * Service for generate output files
 */
public interface OutputGeneratorService
{
	/**
	 * Generate output file
	 */
	void generate(Long clientId, Number sum, boolean big);
}
