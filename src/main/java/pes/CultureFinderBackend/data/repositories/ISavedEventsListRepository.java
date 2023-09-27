package pes.CultureFinderBackend.data.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pes.CultureFinderBackend.data.models.SavedEventsList;

public interface ISavedEventsListRepository extends JpaRepository<SavedEventsList, Long> {

    /*
     * Descripció: Esborra les llistes d'un usuari que es troba loguejat al sistema
     */
    @Transactional
    void deleteAllByUserId(String userId);

    /*
    * Descripció: Troba totes les llistes d'un usuari en concret
    */
    Page<SavedEventsList> findAllByUserId(String userId, Pageable pageable);

    /*
    * Descripció: Comprova si un esdeveniment es troba a una llista
    */
    @Query(value = "SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM saved_events_list_events l " +
            "WHERE l.saved_events_list_id = :listId AND l.events = :eventId", nativeQuery = true)
    boolean existsEventInList(Long listId, Long eventId);
}
