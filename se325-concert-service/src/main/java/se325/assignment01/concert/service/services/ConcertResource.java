package se325.assignment01.concert.service.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.common.dto.ConcertSummaryDTO;
import se325.assignment01.concert.common.dto.PerformerDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Performer;
import se325.assignment01.concert.service.mapper.ConcertMapper;
import se325.assignment01.concert.service.mapper.PerformerMapper;

@Path("/concert-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConcertResource {

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
	public Response login() {

		return null;
	}

	@POST
	@Path("/bookings")
	public Response makeBooking() {

		return null;
	}

	@GET
	@Path("/seats/{time}")
	public Response getSeatsForConcert(@PathParam("time") String time, @DefaultValue("Any") @QueryParam("status") String status) {

		return null;
	}

	@POST
	@Path("/subscribe/concertInfo")
	public Response subscribeToConcert() {

		return null;
	}

	private EntityManager createEM() {
		return PersistenceManager.instance().createEntityManager();
	}

}
