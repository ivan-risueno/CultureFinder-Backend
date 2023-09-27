package pes.CultureFinderBackend.data.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pes.CultureFinderBackend.data.models.Incident;

import java.util.List;

@Transactional
@Repository
public interface IIncidentRepository extends JpaRepository<Incident, Long> {

    /*
     * Descripció: Esborra les incidències d'un esdeveniment concret.
     */
    @Modifying
    @Query(value = "DELETE FROM incidents WHERE event_id =:eventId", nativeQuery = true)
    void deleteByEventId(@Param("eventId") Long eventId);

    /*
     * Descripció: Consulta si existeix una incidència donada un identificador d'esdeveniment.
     */
    @Query(value = "SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END FROM incidents i WHERE i.event_id = :eventId", nativeQuery = true)
    boolean existsByEventId(Long eventId);

    /*
     * Descripció: Consulta incidències donat un identificador d'usuari i status corresponent.
     */
    @Query("SELECT i FROM Incident i WHERE i.userId = :userId AND " +
            " i.isResolved = :resolved")
    List<Incident> findByUserAndResolved(String userId, Boolean resolved);

    /*
     * Descripció: Consulta incidències donat un identificador d'esdeveniment i status corresponent.
     */
    @Query("SELECT i FROM Incident i WHERE (:eventId = 0 OR i.eventId = :eventId) AND " +
            " (i.isResolved = :resolved)")
    List<Incident> findByEventAndResolved(Long eventId, Boolean resolved);

    /*
     * Descripció: Consulta totes les incidències donat un identificador d'usuari
     */
    @Query("SELECT i FROM Incident i WHERE i.userId = :userId")
    Page<Incident> findAllByUser(String userId, Pageable pageable);

    @Query("SELECT i FROM Incident i WHERE (i.eventId = :eventId) AND " +
            " (i.userId = :userId) AND (i.isResolved = :resolved)")
    List<Incident> findByEventAndUserAndResolved(String userId, Long eventId, Boolean resolved);

    /*
     * Descripció: Esborra totes les incidències donat un identificador d'usuari i d'esdeveniment.
     */
    @Modifying
    @Query(value = "DELETE FROM incidents WHERE event_id =:eventId AND user_id =:userId", nativeQuery = true)
    void deleteByEventIdAndUserId(@Param("eventId") Long eventId, @Param("userId") String userId);
}
