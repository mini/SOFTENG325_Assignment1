package se325.assignment01.concert.service.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.common.dto.BookingRequestDTO;
import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertInfoNotificationDTO;
import se325.assignment01.concert.common.dto.ConcertInfoSubscriptionDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.common.dto.UserDTO;
import se325.assignment01.concert.common.types.BookingStatus;
import se325.assignment01.concert.service.domain.Booking;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.domain.User;
import se325.assignment01.concert.service.jaxrs.ConcertSubscription;
import se325.assignment01.concert.service.jaxrs.LocalDateTimeParam;
import se325.assignment01.concert.service.mapper.BookingMapper;
import se325.assignment01.concert.service.mapper.ConcertMapper;
import se325.assignment01.concert.service.mapper.PerformerMapper;
import se325.assignment01.concert.service.mapper.SeatMapper;
import se325.assignment01.concert.service.util.TheatreLayout;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

	
	/* NOTES
	 * 
	 * - Would use more optimistic locks but they weren't working... (I did have @Version tags in those classes) 
	 *   Especially for the get single/all concerts/performers end points. 
	 * 
	 * - Wasn't sure how to handle the async end point. Because the client is directly connected to an instance,
	 *   if another instance handles a booking, how would the first instance know when to send the notification?
	 *   You could store which user is listening to which concert and on which instance... but you'd need to keep polling
	 *   the database?
	 */
	
	public static final String AUTH_COOKIE = "auth";

	private static final ConcurrentHashMap<LocalDateTime, LinkedList<ConcertSubscription>> subscriptions = new ConcurrentHashMap<>();
	private static final ExecutorService threadPool = Executors.newSingleThreadExecutor();

	/**
	 * Gets all concerts stored in database
	 */
	@GET
	@Path("/concerts")
	public Response getAllConcerts() {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			List<Concert> concerts = em.createQuery("SELECT c FROM Concert c", Concert.class)
					.setLockMode(LockModeType.PESSIMISTIC_READ)
					.getResultList();

			if (concerts.isEmpty()) {
				return Response.noContent().build();
			}

			// Convert to DTOs
			Set<ConcertDTO> dtoConcerts = new HashSet<>();
			concerts.forEach(concert -> dtoConcerts.add(ConcertMapper.toDTO(concert)));
			em.getTransaction().commit();

			GenericEntity<Set<ConcertDTO>> out = new GenericEntity<Set<ConcertDTO>>(dtoConcerts) {};
			return Response.ok(out).build();
		} finally {
			if (em.getTransaction().isActive()) { 
				em.getTransaction().commit(); // Mainly for early returns
			}
			em.close();
		}
	}
	
	/**
	 * Gets a concert stored in database from its id.
	 * Returns 404 if it doesn't exist
	 */
	@GET
	@Path("/concerts/{id}")
	public Response getConcert(@PathParam("id") long id) {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			Concert concert = em.find(Concert.class, id, LockModeType.PESSIMISTIC_READ);

			if (concert == null) {
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.ok(ConcertMapper.toDTO(concert)).build();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}
			em.close();
		}
	}

	/**
	 * Gets all concert's summaries stored in database
	 */
	@GET
	@Path("/concerts/summaries")
	public Response getSummaries() {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			List<Concert> concerts = em.createQuery("SELECT c FROM Concert c", Concert.class)
					.setLockMode(LockModeType.PESSIMISTIC_READ)
					.getResultList();

			if (concerts.isEmpty()) {
				return Response.noContent().build();
			}

			// Convert to DTOs
			Set<ConcertSummaryDTO> dtoSummaries = new HashSet<>();
			concerts.forEach(concert -> dtoSummaries.add(ConcertMapper.toSummaryDTO(concert)));
			em.getTransaction().commit();

			GenericEntity<Set<ConcertSummaryDTO>> out = new GenericEntity<Set<ConcertSummaryDTO>>(dtoSummaries) {};
			return Response.ok(out).build();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}
			em.close();
		}
	}

	/**
	 * Gets all performers stored in database
	 */
	@GET
	@Path("/performers")
	public Response getAllPerformers() {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			List<Performer> performers = em.createQuery("SELECT p FROM Performer p", Performer.class)
					.setLockMode(LockModeType.PESSIMISTIC_READ)
					.getResultList();

			if (performers.isEmpty()) {
				return Response.noContent().build();
			}

			// Convert to DTOs
			Set<PerformerDTO> dtoPerformers = new HashSet<>();
			performers.forEach(performer -> dtoPerformers.add(PerformerMapper.toDTO(performer)));
			em.getTransaction().commit();

			GenericEntity<Set<PerformerDTO>> out = new GenericEntity<Set<PerformerDTO>>(dtoPerformers) {};
			return Response.ok(out).build();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}
			em.close();
		}
	}

	/**
	 * Gets a performer stored in database from its id.
	 * Returns 404 if it doesn't exist
	 */
	@GET
	@Path("/performers/{id}")
	public Response getPerformer(@PathParam("id") long id) {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			Performer performer = em.find(Performer.class, id, LockModeType.PESSIMISTIC_READ);

			if (performer == null) {
				return Response.status(Status.NOT_FOUND).build();
			}
			return Response.ok(PerformerMapper.toDTO(performer)).build();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}
			em.close();
		}
	}

	/**
	 * Checks the supplied credentials against the database.
	 * Sets a cookie with a session id if there's a match, 
	 * otherwise returns 401
	 */
	@POST
	@Path("/login")
	public Response login(UserDTO attempt) {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			User user;
			try {
				user = em.createQuery("SELECT u FROM User u where u.username = :username AND u.password = :password", User.class)
						.setParameter("username", attempt.getUsername())
						.setParameter("password", attempt.getPassword())
						.setLockMode(LockModeType.PESSIMISTIC_READ)
						.getSingleResult();
			} catch (NoResultException e) { // No username-password match
				return Response.status(Status.UNAUTHORIZED).build();
			} finally {
				em.getTransaction().commit();
			}

			return Response.ok().cookie(newSession(user, em)).build();
		} finally {
			em.close();
		}
	}

	/**
	 * Creates a booking for the specified concert, date, and seats for the logged in user.
	 * Returns 401 is not logged in, 400 if no concert-date exists, 403 if requested seats are unavailable
	 */
	@POST
	@Path("/bookings")
	public Response makeBooking(BookingRequestDTO request, @CookieParam(AUTH_COOKIE) Cookie authCookie) {
		EntityManager em = createEM();
		try {
			User user = getLoggedInUser(authCookie, em);
			if (user == null) {
				return Response.status(Status.UNAUTHORIZED).build();
			}
			
			em.getTransaction().begin(); // Check concert and time
			Concert concert = em.find(Concert.class, request.getConcertId(), LockModeType.PESSIMISTIC_READ);
			if (concert == null || !concert.getDates().contains(request.getDate())) {
				return Response.status(Status.BAD_REQUEST).build();
			}
			em.getTransaction().commit();
			
			em.getTransaction().begin(); // Check if requested seats are available
			List<Seat> freeReqestedSeats = em.createQuery("SELECT s FROM Seat s WHERE s.label IN :label AND s.date = :date AND s.isBooked = false", Seat.class)
					.setParameter("label", request.getSeatLabels())
					.setParameter("date", request.getDate())
					.setLockMode(LockModeType.PESSIMISTIC_WRITE)
					.getResultList();

			if (freeReqestedSeats.size() != request.getSeatLabels().size()) {
				return Response.status(Status.FORBIDDEN).build();
			}

			// All good, make booking
			Booking booking = new Booking(user, request.getConcertId(), request.getDate());
			booking.getSeats().addAll(freeReqestedSeats);
			freeReqestedSeats.forEach(seat -> seat.setIsBooked(true));
			em.persist(booking);

			// Get remaining seats for notifications
			int freeSeats = em.createQuery("SELECT COUNT(s) FROM Seat s WHERE s.date = :date AND s.isBooked = false", Long.class)
					.setParameter("date", request.getDate())
					.getSingleResult()
					.intValue();
			checkSubscribers(request.getDate(), freeSeats);
			
			em.getTransaction().commit();

			return Response.created(URI.create("/concert-service/bookings/" + booking.getId())).build();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}
		}
	}

	/**
	 * Gets the logged in user's bookings.
	 * Returns 401 if client isn't logged in.
	 */
	@GET
	@Path("/bookings")
	public Response getUsersBookings(@CookieParam(AUTH_COOKIE) Cookie authCookie) {
		EntityManager em = createEM();
		try {
			User user = getLoggedInUser(authCookie, em);
			if (user == null) {
				return Response.status(Status.UNAUTHORIZED).build();
			}

			// Get bookings and convert to DTOs
			em.getTransaction().begin();
			em.lock(user, LockModeType.OPTIMISTIC);
			Set<BookingDTO> dtoBookings = new HashSet<>();
			user.getBookings().forEach(booking -> dtoBookings.add(BookingMapper.toDTO(booking)));
			
			em.getTransaction().commit();
			
			GenericEntity<Set<BookingDTO>> out = new GenericEntity<Set<BookingDTO>>(dtoBookings) {};
			return Response.ok(out).build();
		} finally {
			em.close();
		}
	}

	/**
	 * Gets a specific booking entry
	 * Returns 401 if client isn't logged in, 403 if logged in user is not the booking's owner
	 */
	@GET
	@Path("/bookings/{id}")
	public Response getBooking(@PathParam("id") Long id, @CookieParam(AUTH_COOKIE) Cookie authCookie) {
		EntityManager em = createEM();
		try {
			User user = getLoggedInUser(authCookie, em);
			if (user == null) {
				return Response.status(Status.UNAUTHORIZED).build();
			}

			em.getTransaction().begin();
			Booking booking = em.find(Booking.class, id, LockModeType.PESSIMISTIC_READ);

			if (!booking.getUser().equals(user)) {
				return Response.status(Status.FORBIDDEN).build();
			}

			return Response.ok(BookingMapper.toDTO(booking)).build();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}
			em.close();
		}
	}

	/**
	 * Get the status for all seats for a concert date. Can filter results by appending ?status=[BookingStatus]
	 * Returns 400 for invalid dates
	 */
	@GET
	@Path("/seats/{date}")
	public Response getSeatStatus(@PathParam("date") String dateString, @DefaultValue("Any") @QueryParam("status") BookingStatus status) {
		LocalDateTime date = new LocalDateTimeParam(dateString).getLocalDateTime();
		EntityManager em = createEM();
		try {
			// Check date is valid
			em.getTransaction().begin();
			boolean validDate = em.createQuery("SELECT COUNT(s) FROM Seat s WHERE s.date = :date", Long.class)
					.setLockMode(LockModeType.PESSIMISTIC_READ)
					.setParameter("date", new LocalDateTimeParam(dateString).getLocalDateTime())
					.getSingleResult()
					.intValue() > 0;
			if(!validDate) {
				return Response.status(Status.BAD_REQUEST).build();
			}
			em.getTransaction().commit();

			// Get seat statuses
			em.getTransaction().begin();
			TypedQuery<Seat> selectQuery;
			if (status == BookingStatus.Any) {
				selectQuery = em.createQuery("SELECT s FROM Seat s WHERE s.date = :date", Seat.class)
						.setParameter("date", date);
			} else {
				selectQuery = em.createQuery("SELECT s FROM Seat s WHERE s.date = :date AND isBooked = :status", Seat.class)
						.setParameter("date", date)
						.setParameter("status", status == BookingStatus.Booked);
			}
			List<Seat> seats = selectQuery
					.setLockMode(LockModeType.PESSIMISTIC_READ)
					.getResultList();
			
			// Convert to DTOs
			Set<SeatDTO> dtoSeats = new HashSet<>();
			seats.forEach(seat -> dtoSeats.add(SeatMapper.toDTO(seat)));

			em.getTransaction().commit();

			GenericEntity<Set<SeatDTO>> out = new GenericEntity<Set<SeatDTO>>(dtoSeats) {};
			return Response.ok(out).build();

		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}
			em.close();
		}
	}

	/**
	 * End point to subscribe to an alert when percentage of seats booked reaches the specified threshold.
	 * Returns 401 if not logged in, 400 if concert doesn't exist
	 */
	@POST
	@Path("/subscribe/concertInfo")
	public void subscribeToConcert(@Suspended AsyncResponse reponse, @CookieParam(AUTH_COOKIE) Cookie authCookie, ConcertInfoSubscriptionDTO request) {
		EntityManager em = createEM();
		try {
			User user = getLoggedInUser(authCookie, em);
			if (user == null) {
				reponse.resume(Response.status(Status.UNAUTHORIZED).build());
				return;
			}

			em.getTransaction().begin();

			// Validate target concert
			Concert concert = em.find(Concert.class, request.getConcertId(), LockModeType.PESSIMISTIC_READ);
			if (concert == null || !concert.getDates().contains(request.getDate())) {
				reponse.resume(Response.status(Status.BAD_REQUEST).build());
				return;
			}
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().commit();
			}
			em.close();
		}
		synchronized (subscriptions) { // Race condition between the two statements
			if (!subscriptions.contains(request.getDate())) {
				subscriptions.put(request.getDate(), new LinkedList<>());
			}
		}
		subscriptions.get(request.getDate()).add(new ConcertSubscription(reponse, request.getPercentageBooked()));
	}

	/* -------- Helper Methods -------- */

	private EntityManager createEM() { // Reduce some clutter
		return PersistenceManager.instance().createEntityManager();
	}

	/**
	 * Generates a new session id for the specified user. Id will be saved to
	 * database and a cookie
	 * 
	 * @param user
	 *            the user to associate the id with
	 * @param em
	 *            current entity manager context
	 * @return a cookie with a new session id/uuid
	 */
	private NewCookie newSession(User user, EntityManager em) {
		em.getTransaction().begin();
		em.lock(user, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
		user.setSessionId(UUID.randomUUID());
		em.getTransaction().commit();
		return new NewCookie(AUTH_COOKIE, user.getSessionId().toString());
	}

	/**
	 * Gets the logged in user.
	 * 
	 * @param authCookie
	 *            the auth cookie containing the session id/uuid
	 * @param em
	 *            current entity manager context
	 * @return the user associated with the auth cookie, otherwise null
	 */
	private User getLoggedInUser(Cookie authCookie, EntityManager em) {
		if (authCookie == null) { // No chance if no cookie
			return null;
		}

		User found = null;
		em.getTransaction().begin();
		try {
			found = em.createQuery("SELECT u FROM User u where u.sessionId = :uuid", User.class)
					.setParameter("uuid", UUID.fromString(authCookie.getValue())) // So many possibilities that chance of a collision is ~0
					.setLockMode(LockModeType.OPTIMISTIC)
					.getSingleResult();
		} catch (NoResultException e) {
			// No associated user, must login again
		}
		em.getTransaction().commit();
		return found;
	}

	/**
	 * Checks all notification subscriptions for the concert at a given DateTime.
	 * Will resume the related AsyncResponse threads.
	 * 
	 * @param key
	 *            DateTime of a concert
	 * @param seatsAvailable
	 *            number of seats not yet booked
	 */
	private void checkSubscribers(LocalDateTime key, int seatsAvailable) {
		threadPool.submit(() -> {
			double percentageBooked = 1.0 - seatsAvailable / (double) TheatreLayout.NUM_SEATS_IN_THEATRE;
			for (Iterator<ConcertSubscription> iterator = subscriptions.get(key).iterator(); iterator.hasNext();) {
				ConcertSubscription sub = iterator.next();
				if (percentageBooked >= sub.percentageTarget) {
					iterator.remove(); // So they only receive notification once
					sub.response.resume(Response.ok(new ConcertInfoNotificationDTO(seatsAvailable)).build());
				}
			}
		});
	}
}
