package by.intexsoft.ufm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * General configuration
 */
@Configuration
@PropertySource("classpath:application.properties")
@ImportResource("classpath:spring-drools.xml")
@EnableAsync(proxyTargetClass = true)
@ComponentScan("by.intexsoft.ufm.service")
public class ApplicationConfig
{
	/**
	 * Init {@link ObjectMapper} {@link Bean}
	 */
	@Bean
	public ObjectMapper create()
	{
		return new ObjectMapper();
	}
}
