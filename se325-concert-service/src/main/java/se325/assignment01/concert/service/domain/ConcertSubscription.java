package se325.assignment01.concert.service.domain;

import javax.ws.rs.container.AsyncResponse;

public class ConcertSubscription {

	public final AsyncResponse response;
	public final double percentageBooked;
    
	public ConcertSubscription(AsyncResponse response, int percentageBooked) {
		this.response = response;
		this.percentageBooked = percentageBooked / 100.0;
	}   
}
