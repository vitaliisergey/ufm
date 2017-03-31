package by.intexsoft.ufm.service;

import java.util.Set;

/**
 * Service for generation output files
 */
public interface KieProcessorService
{
	/**
	 * Generate output files
	 */
	<T> void generate(Set<T> set );
}
