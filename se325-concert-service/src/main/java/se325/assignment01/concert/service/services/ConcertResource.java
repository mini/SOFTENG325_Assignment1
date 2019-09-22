package se325.assignment01.concert.service.services;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.EntityManager;
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
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se325.assignment01.concert.common.dto.BookingRequestDTO;
import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.common.dto.SeatDTO;
import se325.assignment01.concert.common.dto.UserDTO;
import se325.assignment01.concert.common.types.BookingStatus;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.domain.User;
import se325.assignment01.concert.service.jaxrs.LocalDateTimeParam;
import se325.assignment01.concert.service.mapper.ConcertMapper;
import se325.assignment01.concert.service.mapper.PerformerMapper;
import se325.assignment01.concert.service.mapper.SeatMapper;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

	public static final String AUTH_COOKIE = "auth";
	private static final Logger LOGGER = LoggerFactory.getLogger(ConcertResource.class);

	@GET
	@Path("/concerts")
	public Response getAllConcerts() {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			List<Concert> concerts = em.createQuery("SELECT c FROM CONCERTS c", Concert.class).getResultList();
			em.getTransaction().commit();

			if (concerts.isEmpty()) {
				return Response.noContent().build();
			}

			Set<ConcertDTO> dtoConcerts = new HashSet<>();
			for (Concert c : concerts) {
				dtoConcerts.add(ConcertMapper.toDTO(c));
			}

			GenericEntity<Set<ConcertDTO>> out = new GenericEntity<Set<ConcertDTO>>(dtoConcerts) {
			};
			return Response.ok(out).build();
		} finally {
			em.close();
		}
	}

	@GET
	@Path("/concerts/{id}")
	public Response getConcert(@PathParam("id") long id) {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			Concert concert = em.find(Concert.class, id);
			em.getTransaction().commit();

			if (concert == null) {
				return Response.status(Status.NOT_FOUND).build();
			}
			ConcertDTO dto = ConcertMapper.toDTO(concert);
			return Response.ok(dto).build();
		} finally {
			em.close();
		}
	}

	@GET
	@Path("/concerts/summaries")
	public Response getSummaries() {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			List<Concert> concerts = em.createQuery("SELECT c FROM CONCERTS c", Concert.class).getResultList();
			em.getTransaction().commit();

			if (concerts.isEmpty()) {
				return Response.noContent().build();
			}

			Set<ConcertSummaryDTO> dtoSummaries = new HashSet<>();
			for (Concert c : concerts) {
				dtoSummaries.add(ConcertMapper.toSummaryDTO(c));
			}

			GenericEntity<Set<ConcertSummaryDTO>> out = new GenericEntity<Set<ConcertSummaryDTO>>(dtoSummaries) {
			};
			return Response.ok(out).build();
		} finally {
			em.close();
		}
	}

	@GET
	@Path("/performers")
	public Response getAllPerformers() {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			List<Performer> concerts = em.createQuery("SELECT p FROM PERFORMERS p", Performer.class).getResultList();
			em.getTransaction().commit();

			if (concerts.isEmpty()) {
				return Response.noContent().build();
			}

			Set<PerformerDTO> dtoPerformers = new HashSet<>();
			for (Performer p : concerts) {
				dtoPerformers.add(PerformerMapper.toDTO(p));
			}

			GenericEntity<Set<PerformerDTO>> out = new GenericEntity<Set<PerformerDTO>>(dtoPerformers) {
			};
			return Response.ok(out).build();
		} finally {
			em.close();
		}
	}

	@GET
	@Path("/performers/{id}")
	public Response getPerformer(@PathParam("id") long id) {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			Performer performer = em.find(Performer.class, id);
			em.getTransaction().commit();

			if (performer == null) {
				return Response.status(Status.NOT_FOUND).build();
			}
			PerformerDTO dto = PerformerMapper.toDTO(performer);
			return Response.ok(dto).build();
		} finally {
			em.close();
		}
	}

	@POST
	@Path("/login")
	public Response login(UserDTO attempt) {
		EntityManager em = createEM();
		try {
			em.getTransaction().begin();
			User user = null;
			try {
				user = em.createQuery("SELECT u FROM USERS u where u.username = :username", User.class)
				        .setParameter("username", attempt.getUsername()).getSingleResult();
			} catch (NoResultException e) {
				// Username not found
			}
			em.getTransaction().commit();

			if (user == null || !user.getPassword().equals(attempt.getPassword())) {
				return Response.status(Status.UNAUTHORIZED).build();
			}

			return Response.ok().cookie(newSession(user, em)).build();
		} finally {
			em.close();
		}
	}

	@POST
	@Path("/bookings")
	public Response makeBooking(BookingRequestDTO request, @CookieParam(AUTH_COOKIE) Cookie authCookie) {
		EntityManager em = createEM();
		try {
			User user = getLoggedInUser(authCookie, em);
			if (user == null) {
				return Response.status(Status.UNAUTHORIZED).build();
			}

			em.getTransaction().begin();
			
			Concert concert = em.find(Concert.class, request.getConcertId());
			if(concert == null || !concert.getDates().contains(request.getDate())) {
				em.getTransaction().commit();
				return Response.status(Status.BAD_REQUEST).build();
			}

			List<Seat> requestedSeats = em
			        .createQuery("SELECT s FROM SEATS s WHERE s.label IN :label AND s.date = :date", Seat.class)
			        .setParameter("label", request.getSeatLabels())
			        .setParameter("date", request.getDate())
			        .getResultList();


			for (Seat seat : requestedSeats) {
				if (seat.getIsBooked()) {
					em.getTransaction().commit();
					return Response.status(Status.FORBIDDEN).build();
				}
			}

			em.createQuery("UPDATE SEATS s SET s.isBooked = true WHERE s.label IN :label AND s.date = :date")
			        .setParameter("label", request.getSeatLabels())
			        .setParameter("date", request.getDate())
			        .executeUpdate();

			em.getTransaction().commit();

			return Response.created(URI.create("/concert-service/seats/" + request.getDate().format(ISO_LOCAL_DATE_TIME) + "?status=Booked")).build();
		} finally {
			em.close();
		}
	}

	@GET
	@Path("/bookings")
	public Response getUserBookings(@CookieParam(AUTH_COOKIE) Cookie authCookie) {
		EntityManager em = createEM();
		try {
			User user = getLoggedInUser(authCookie, em);
			if (user == null) {
				return Response.status(Status.UNAUTHORIZED).build();
			}
			em.refresh(user); // TODO Revisit
			GenericEntity<Set<Seat>> out = new GenericEntity<Set<Seat>>(user.getBookings()) {};
			return Response.ok(out).build();
			
		} finally {
			em.close();
		}
	}

	@GET
	@Path("/seats/{date}")
	public Response getSeatStatus(@PathParam("date") String dateString, @DefaultValue("Any") @QueryParam("status") BookingStatus status) {
		LocalDateTime date = new LocalDateTimeParam(dateString).getLocalDateTime();
		EntityManager em = createEM();
		try {
			
			em.getTransaction().begin();
			TypedQuery<Seat> selectQuery;
			if(status == BookingStatus.Any) { // TODO Use criteria methods
				selectQuery = em.createQuery("SELECT s FROM SEATS s WHERE s.date = :date", Seat.class)
								.setParameter("date", date);
			} else {
				selectQuery = em.createQuery("SELECT s FROM SEATS s WHERE s.date = :date AND isBooked = :status", Seat.class)
								.setParameter("date", date)
								.setParameter("status", status == BookingStatus.Booked);
			}
			List<Seat> seats = selectQuery.getResultList();
			em.getTransaction().commit();	
			
			Set<SeatDTO> dtoSeats = new HashSet<>();
			for (Seat seat : seats) {
				dtoSeats.add(SeatMapper.toDTO(seat));
			}
			
			GenericEntity<Set<SeatDTO>> out = new GenericEntity<Set<SeatDTO>>(dtoSeats) {};
			return Response.ok(out).build();
			
		} finally {
			em.close();
		}
	}

	@POST
	@Path("/subscribe/concertInfo")
	public Response subscribeToConcert() {

		return null;
	}

	private EntityManager createEM() {
		return PersistenceManager.instance().createEntityManager();
	}

	private NewCookie newSession(User user, EntityManager em) {
		user.setSessionId(UUID.randomUUID());
		em.getTransaction().begin();
		em.merge(user);
		em.getTransaction().commit();
		return new NewCookie(AUTH_COOKIE, user.getSessionId().toString());
	}

	private User getLoggedInUser(Cookie authCookie, EntityManager em) {
		if (authCookie == null) {
			return null;
		}

		User found = null;
		em.getTransaction().begin();
		try {
			found = em.createQuery("SELECT u FROM USERS u where u.sessionId = :uuid", User.class)
			        .setParameter("uuid", UUID.fromString(authCookie.getValue())).getSingleResult();
		} catch (NoResultException e) {
			// Not logged in
		}
		em.getTransaction().commit();
		return found;
	}

}
