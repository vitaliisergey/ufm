package by.intexsoft.ufm.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model for subscriber
 */
@JsonAutoDetect
public class Subscriber
{
	/**
	 * Link to {@link Client}
	 */
	@JsonBackReference
	public Client client;
	/**
	 * Identity of subscriber
	 */
	@JsonProperty("id")
	public Long id;
	/**
	 * Spent of subscriber
	 */
	@JsonProperty("spent")
	public Long spent;

	/**
	 * {@link Client} getter
	 */
	public Client getClient()
	{
		return client;
	}

	/**
	 * Identity getter
	 */
	public Long getId()
	{
		return id;
	}

	/**
	 * Spent getter
	 */
	public Long getSpent()
	{
		return spent;
	}
}
