package se325.assignment01.concert.service.util;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se325.assignment01.concert.service.domain.Concert;
import se325.assignment01.concert.service.domain.Seat;
import se325.assignment01.concert.service.services.PersistenceManager;

public class ConcertUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(ConcertUtils.class);

    /**
     * This method will clear all seat and booking data from the database. Then, it will create all Seat objects for
     * all concerts and dates.
     */
    public static void initConcerts() {
        LOGGER.debug("initConcerts(): Creating the Application");

        EntityManager em = PersistenceManager.instance().createEntityManager();
        try {

            // Get all concerts
            em.getTransaction().begin();
            TypedQuery<Concert> query = em.createQuery("SELECT c FROM Concert c", Concert.class);
            List<Concert> concerts = query.getResultList();

            // Get all dates for all concerts
            Set<LocalDateTime> allDates = new HashSet<>();
            for (Concert c : concerts) {
                Set<LocalDateTime> dates = c.getDates();
                allDates.addAll(dates);
            }
            em.getTransaction().commit();

            LOGGER.debug("initConcerts(): There are " + allDates.size() + " concert dates");

            // For each concert date, create the seats for that date and persist them.
            int seatCount = 0;
            for (LocalDateTime date : allDates) {

                em.getTransaction().begin();
                Set<Seat> seatsForDate = TheatreLayout.createSeatsFor(date);
                for (Seat s : seatsForDate) {
                    em.persist(s);
                    seatCount++;
                }
                em.getTransaction().commit();

                // Ensures we aren't braking the EM with thousands of seat entities.
                em.clear();
            }

            LOGGER.debug("initConcerts(): Created " + seatCount + " seats!");
        } finally {
            em.close();
        }
    }
}
