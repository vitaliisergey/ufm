package by.intexsoft.ufm.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Model for client
 */
@JsonAutoDetect
public class Client
{
	/**
	 * Client identity
	 */
	@JsonProperty("clientId")
	public Long clientId;
	/**
	 * {@link java.util.Collection} of {@link Subscriber}s
	 */
	@JsonManagedReference
	@JsonProperty("subscribers")
	public List<Subscriber> subscribers;

	/**
	 * Client id getter
	 */
	public Long getClientId()
	{
		return clientId;
	}

	/**
	 * {@link Subscriber}s getter
	 */
	public List<Subscriber> getSubscribers()
	{
		return subscribers;
	}
}
