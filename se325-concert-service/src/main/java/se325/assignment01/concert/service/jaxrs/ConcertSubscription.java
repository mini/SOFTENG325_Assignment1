package se325.assignment01.concert.service.jaxrs;

import javax.ws.rs.container.AsyncResponse;

/**
 * A POJO to store a client connection and when to respond
 */
public class ConcertSubscription {

	public final AsyncResponse response;
	public final double percentageTarget;
    
	public ConcertSubscription(AsyncResponse response, int percentageBooked) {
		this.response = response;
		this.percentageTarget = percentageBooked / 100.0;
	}   
}
