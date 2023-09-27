package pes.CultureFinderBackend.data.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pes.CultureFinderBackend.data.models.Assistance;
import pes.CultureFinderBackend.data.models.AssistanceId;

import java.util.List;

@Transactional
@Repository
public interface IAssistanceRepository extends JpaRepository<Assistance, AssistanceId> {

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM assistances a WHERE a.event_id = :eventId", nativeQuery = true)
    void deleteByEventId(@Param("eventId") Long eventId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM assistances a WHERE a.user_id = :userId", nativeQuery = true)
    void deleteByUserId(@Param("userId") String userId);

    @Query(value = "SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM assistances a WHERE a.event_id = :eventId", nativeQuery = true)
    Boolean existsByEventId(@Param("eventId") Long eventId);

    @Query(value = "SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM assistances a WHERE a.user_id = :userId", nativeQuery = true)
    Boolean existsByUserId(@Param("userId") String userId);

    /*
     * Descripció: Obté el nombre d'assistents d'un esdeveniment en concret
     */
    @Query(value = "SELECT COUNT(a) FROM assistances a WHERE a.event_id = :eventId", nativeQuery = true)
    Integer getNAssistantsByEventId(Long eventId);

    /*
    * Descripció: Obté els identificadors dels esdeveniments més populars
    */
    @Query(value = "SELECT a.event_id FROM assistances a GROUP BY a.event_id ORDER BY COUNT(*) desc", nativeQuery = true)
    List<Long> getPopularEventIds();

    /*
    * Descripció: Obté els identificadors dels esdeveniments als que un usuari assistirà
    */
    @Query(value = "SELECT a.event_id FROM assistances a WHERE a.user_id = :userId", nativeQuery = true)
    List<Long> getAllByUserId(String userId);
}
