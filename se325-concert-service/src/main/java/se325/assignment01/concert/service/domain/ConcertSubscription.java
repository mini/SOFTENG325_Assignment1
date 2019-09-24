package se325.assignment01.concert.service.domain;

import javax.ws.rs.container.AsyncResponse;

public class ConcertSubscription {

	public final AsyncResponse response;
	public final double percentageTarget;
    
	public ConcertSubscription(AsyncResponse response, int percentageBooked) {
		this.response = response;
		this.percentageTarget = percentageBooked / 100.0;
	}   
}
