package pes.CultureFinderBackend.data.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pes.CultureFinderBackend.data.models.Event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IEventRepository extends JpaRepository<Event, Long> {

    /*
     * Descripció: Troba els esdeveniments que contenen la denominació passada per paràmetre
     * Resultat: Retorna una llista d'esdeveniments que compleixen la condició
     */
    @Query(value = "SELECT e FROM Event e WHERE e.denominacio LIKE %?1% AND e.dataInici >= current date",
    countQuery = "SELECT COUNT(e) FROM Event e WHERE e.denominacio LIKE %?1% AND e.dataInici >= current date")
    Page<Event> findAllByDenominacio(String denominacio, Pageable pageable);

    /*
     * Descripció: Troba els esdeveniments que contenen el preu passat per paràmetre
     * Resultat: Retorna una llista d'esdeveniments que compleixen la condició
     */
    @Query(value = "SELECT e FROM Event e WHERE e.preu LIKE %?1% AND e.dataInici >= current date",
    countQuery = "SELECT COUNT(e) FROM Event e WHERE e.preu LIKE %?1% AND e.dataInici >= current date")
    Page<Event> findAllByPreu(String preu, Pageable pageable);

    /*
     * Descripció: Troba els esdeveniments dels quals es troben incloses dins de les passades per paràmetre
     * Resultat: Retorna una llista d'esdeveniments que compleixen la condició
     */
    @Query(value = "SELECT e FROM Event e WHERE e.dataInici >= cast(:dataIni as date) AND e.dataFi <= cast(:dataFi as date)",
    countQuery = "SELECT COUNT(e) FROM Event e WHERE e.dataInici >= ?1 AND e.dataFi <= ?2")
    Page<Event> findAllByDate(LocalDate dataIni, LocalDate dataFi, Pageable pageable);

    /*
     * Descripció: Troba els esdeveniments que contenen la comarca i municipi passats per paràmetre
     * Resultat: Retorna una llista d'esdeveniments que compleixen la condició
     */
    @Query(value = "SELECT e FROM Event e WHERE e.comarcaIMunicipi LIKE %?1% AND e.dataInici >= current date",
    countQuery = "SELECT COUNT(e) FROM Event e WHERE e.comarcaIMunicipi LIKE %?1% AND e.dataInici >= current date")
    Page<Event> findAllByComarcaMunicipi(String comarcaMunicipi, Pageable pageable);

    /*
     * Descripció: Troba els esdeveniments que contenen la descripció passada per paràmetre
     * Resultat: Retorna una llista d'esdeveniments que compleixen la condició
     */
    @Query(value = "SELECT e FROM Event e WHERE e.descripcio = :descripcio AND e.dataInici >= current date",
    countQuery = "SELECT COUNT(e) FROM Event e WHERE e.descripcio = :descripcio AND e.dataInici >= current date")
    Page<Event> findAllByDescripcio(String descripcio, Pageable pageable);

    /*
     * Descripció: Troba els esdeveniments que contenen la categoria passada per paràmetre
     * Resultat: Retorna una llista d'esdeveniments que compleixen la condició
     */
    @Query(value = "SELECT e FROM Event e WHERE e.categoria LIKE %?1% AND e.dataInici >= current date",
    countQuery = "SELECT COUNT(e) FROM Event e WHERE e.categoria LIKE %?1% AND e.dataInici >= current date")
    Page<Event> findAllByCategoria(String categoria, Pageable pageable);

    /*
     * Descripció: Troba els esdeveniments que contenen l'àmbit passat per paràmetre
     * Resultat: Retorna una llista d'esdeveniments que compleixen la condició
     */
    @Query(value = "SELECT e FROM Event e WHERE e.ambit LIKE %?1% AND e.dataInici >= current date",
    countQuery = "SELECT COUNT(e) FROM Event e WHERE e.ambit LIKE %?1% AND e.dataInici >= current date")
    Page<Event> findAllByAmbit(String ambit, Pageable pageable);

    /*
     * Descripció: Troba els esdeveniments que contenen les altres categories passades per paràmetre
     * Resultat: Retorna una llista d'esdeveniments que compleixen la condició
     */
    @Query(value = "SELECT e FROM Event e WHERE e.altresCategories LIKE %?1% AND e.dataInici >= current date",
    countQuery = "SELECT COUNT(e) FROM Event e WHERE e.altresCategories LIKE %?1% AND e.dataInici >= current date")
    Page<Event> findAllByAltres(String altres, Pageable pageable);

    /*
     * Descripció: Troba els esdeveniments que contenen els atributs com els que passen per paràmetre
     * Resultat: Retorna una llista d'esdeveniments que compleixen la condició
     */
    @Query("SELECT e FROM Event e WHERE (:ambit is null OR e.ambit LIKE %:ambit%) AND (:categoria is null OR e.categoria LIKE %:categoria%) AND (:altres is null OR e.altresCategories LIKE %:altres%) AND ((cast(:dataIni as date) is not null AND e.dataInici >= :dataIni) OR (cast(:dataIni as date) is null AND e.dataInici >= current date)) AND (cast(:dataFi as date) is null OR e.dataFi <= :dataFi) AND (:preu is null OR e.preu LIKE %:preu%) AND (:denominacio is null OR e.denominacio LIKE %:denominacio%) AND (:comarcaMunicipi is null OR e.comarcaIMunicipi LIKE %:comarcaMunicipi%) AND (:descripcio is null OR e.descripcio LIKE %:descripcio%) AND (:radi is null OR (e.latitud - :latitud)*(e.latitud - :latitud) + (e.longitud - :longitud)*(e.longitud - :longitud) <= :radi)")
    Page<Event> findAllByFilters(@Param("ambit") String ambit, @Param("categoria") String categoria, @Param("altres") String altres, @Param("dataIni") LocalDate dataIni, @Param("dataFi") LocalDate dataFi, @Param("preu") String preu, @Param("denominacio") String denominacio, @Param("comarcaMunicipi") String comarcaMunicipi, @Param("descripcio") String descripcio, @Param("radi") Float radi, @Param("latitud") Float latitud, @Param("longitud") Float longitud, Pageable pageable);

    /*
     * Descripció: Troba tots els àmbits, categories i altres categories que surten a la taula esdeveniments
     */
    @Query("SELECT DISTINCT e.ambit, e.categoria, e.altresCategories FROM Event e")
    List<String> findAllCategories();

    /*
     * Descripció: Troba els esdeveniments que compleixen amb les condicions especificades
     */
    @Modifying
    @Query("UPDATE Event e SET e.dataFi = :dataFi, e.dataInici = :dataInici, e.denominacio = :denominacio, e.descripcio = :descripcio, " +
            "e.preu = :preu, e.horari = :horari, e.subtitol = :subtitol, e.ambit = :ambit, e.categoria = :categoria, e.altresCategories = :altres, " +
            "e.link = :link, e.imatges = :imatges, e.adreca = :adreca, e.comarcaIMunicipi = :comarcaIMunicipi, e.email = :email, " +
            "e.espai = :espai, e.latitud = :latitud, e.longitud = :longitud, e.telefon = :telefon, e.imgApp = :imgApp WHERE e.id = :id")
    @Transactional
    void updateFields(@Param("dataFi") LocalDate dataFi, @Param("dataInici") LocalDate dataInici, @Param("denominacio") String denominacio,
                      @Param("descripcio") String descripcio, @Param("preu") String preu, @Param("horari") String horari, @Param("subtitol") String subtitol,
                      @Param("ambit") String ambit, @Param("categoria") String categoria, @Param("altres") String altresCategories, @Param("link") String link,
                      @Param("imatges") String imatges, @Param("adreca") String adreca, @Param("comarcaIMunicipi") String comarcaIMunicipi, @Param("email") String email,
                      @Param("espai") String espai, @Param("latitud") Float latitud, @Param("longitud") Float longitud, @Param("telefon") String telefon,
                      @Param("imgApp") String imgApp, @Param("id") Long id);

    /*
     * Descripció: Retorna true si existeix una valoració d'un esdeveniment per a un usuari en concret, false altrament
     */
    @Query(value = "SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM event_ratings e " +
            "WHERE e.user_id = :userId AND e.event_id = :eventId", nativeQuery = true)
    boolean existsRating(@Param("eventId") Long eventId, @Param("userId") String userId);

    /*
     * Descripció: Afegeix una valoració a la taula event_ratings per a un esdeveniment i usuari concrets
     */
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO event_ratings (event_id, score, user_id) values(:eventId, :score, :userId)", nativeQuery = true)
    void rateEvent(@Param("eventId") Long eventId, @Param("userId") String userId, @Param("score") Float score);

    /*
     * Descripció: Troba tots els usuaris que han puntuat un esdeveniment en concret
     */
    @Query(value = "SELECT e.user_id FROM event_ratings e WHERE event_id = :eventId", nativeQuery = true)
    List<String> findRatingUsers(@Param("eventId") Long eventId);

    /*
     * Descripció: Donat un usuari i un esdeveniment, troba la valoració d'aquest usuari d'aquest esdeveniment
     */
    @Query(value = "SELECT e.score FROM event_ratings e WHERE e.event_id = :eventId AND e.user_id = :userId", nativeQuery = true)
    Float getRating(@Param("eventId") Long eventId, @Param("userId") String user);

    /*
     * Descripció: Esborra la puntuació d'un usuari d'un esdeveniment
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM event_ratings e WHERE e.event_id = :eventId AND e.user_id = :userId", nativeQuery = true)
    void deleteRating(@Param("eventId") Long eventId, @Param("userId") String userId);

    /*
     * Descripció: Esborra les puntuacions d'un usuari de tots els esdeveniments
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM event_ratings e WHERE e.user_id = :userId", nativeQuery = true)
    void deleteRatingsByUserId(@Param("userId") String userId);

    /*
    * Descripció: Obté els esdeveniments que formen part d'una categoria en concret
    */
    @Query(value = "SELECT e FROM Event e WHERE e.altresCategories LIKE :category OR e.ambit LIKE :category OR e.categoria LIKE :category")
    Page<Event> findRecommendationsFromGivenCategory(@Param("category") String category, Pageable pageable);

    /*
     * Descripció: Comprova si existeix un esdeveniment a la base de dades per gestionar l'actualització
     */
    @Query(value = "SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM events e " +
            "WHERE e.data_inici = :dataInici AND e.data_fi = :dataFi AND e.denominacio = :denominacio AND e.descripcio = :descripcio", nativeQuery = true)
    boolean existsEventInOurDataBase(LocalDate dataInici, LocalDate dataFi, String denominacio, String descripcio);

    /*
     * Descripció: Obté la valoració d'un esdeveniment per part d'un usuari, o nul altrament
     */
    @Query(value = "SELECT e.score FROM event_ratings e WHERE e.user_id = :userId AND e.event_id = :eventId", nativeQuery = true)
    Optional<Float> findScoreByEventId(String userId, Long eventId);
}