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

import se325.assignment01.concert.common.dto.ConcertDTO;
import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.mapper.ConcertMapper;

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
				dtoConcerts.add(ConcertMapper.convert(c));
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

		return null;
	}

	@GET
	@Path("/concerts/summaries")
	public Response getSummaries() {

		return null;
	}

	@GET
	@Path("/performers")
	public Response getAllPerformers() {

		return null;
	}

	@GET
	@Path("/performers/{id}")
	public Response getPerformer(@PathParam("id") long id) {

		return null;
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
