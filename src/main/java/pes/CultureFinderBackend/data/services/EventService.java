package pes.CultureFinderBackend.data.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.data.mappers.DataEventMapper;
import pes.CultureFinderBackend.data.models.Event;
import pes.CultureFinderBackend.data.repositories.IEventRepository;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.models.EventEntity;
import pes.CultureFinderBackend.domain.services.IDomainEventService;


import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class EventService implements IEventService {

    /*
     * Descripció: Instància del repositori que s'encarrega d'interactuar amb la taula events de la base de dades
     */
    @Autowired
    private IEventRepository iEventRepository;

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori d'assistències
     */
    @Autowired
    private IAssistanceService iAssistanceService;

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori d'incidències
     */
    @Autowired
    private IIncidentService iIncidentService;

    /*
     * Descripció: Mapper de dades que s'encarrega de fer la conversió entre Model i Entitat d'Event
     */
    @Autowired
    private DataEventMapper mapper;

    /*
     * Descripció: Crida al mètode d'EventRepository que guarda un esdeveniment
     * Resultat: Retorna l'esdeveniment que s'ha guardat
     */
    public EventEntity saveEvent(EventEntity event) {
        return mapper.modelToEntity(iEventRepository.save(mapper.entityToModel(event)));
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els esdeveniments
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna tots els esdeveniments de la BD, amb paginació si així s'ha indicat
     */
    public Page<EventEntity> getAllEvents(Integer page, Integer size, Boolean enablePagination) {
        List<EventEntity> events = mapper.listModelsToEntities(iEventRepository.findAll(enablePagination ? PageRequest.of(page, size) : Pageable.unpaged()).getContent());
        return IDomainEventService.changeListToPageEventEntity(events, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba un esdeveniment donat el seu identificador
     * Resultat: Retorna l'esdeveniment trobat a la BD
     */
    // Usamos optional porque en caso de que no exista el id de la entidad la base de datos devuelve null pointer exception
    public Optional<EventEntity> findById(Long id) {
        Optional<Event> e = iEventRepository.findById(id);
        if (e.isEmpty()) throw new ObjectNotFoundException("Event with id " + id + " not found");
        return Optional.ofNullable(mapper.modelToEntity(e.get()));
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que esborra un esdeveniment
     */
    public void deleteEvent(Long id) {
        if (!iEventRepository.existsById(id))  {
            throw new ObjectNotFoundException("Event with id " + id + " not found");
        }

        iEventRepository.deleteById(id);
        try {
            iAssistanceService.deleteAssistancesByEvent(id);
        } catch(ObjectNotFoundException ignored)  {}

        iIncidentService.deleteIncidentsByEvent(id);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que guarda un esdeveniment
     * <event>: Esdeveniment editat obtingut com a paràmetre de la crida de la API
     * Resultat: Retorna l'esdeveniment que s'ha guardat
     */
    public EventEntity editEvent (EventEntity event) {
        Event e = mapper.entityToModel(event);
        List<Event> eBd = iEventRepository.findAllByDescripcio(e.getDescripcio(), Pageable.unpaged()).getContent();
        iEventRepository.updateFields(e.getDataFi(), e.getDataInici(), e.getDenominacio(), e.getDescripcio(),
                e.getPreu(), e.getHorari(), e.getSubtitol(), e.getAmbit(), e.getCategoria(), e.getAltresCategories(), e.getLink(), e.getImatges(),
                e.getAdreca(), e.getComarcaIMunicipi(), e.getEmail(), e.getEspai(), e.getLatitud(), e.getLongitud(), e.getTelefon(), e.getImgApp(), eBd.get(0).getId());
        e.setId(eBd.get(0).getId());
        return mapper.modelToEntity(e);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que comprova si un esdeveniment existeix, donat el seu identificador
     * Resultat: Retorna un booleà que indica si l'esdeveniment existeix
     */
    public boolean existsById(Long id) {
        return iEventRepository.existsById(id);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els esdeveniments que contenen la denominació passada per paràmetre
     * Resultat: Retorna els esdeveniments que contenen la denominació passada per paràmetre
     */
    public Page<EventEntity> findAllByDenominacio(String denominacio, Integer page, Integer size, Boolean enablePagination) {
        List <EventEntity> eventEntities = mapper.listModelsToEntities(iEventRepository.findAllByDenominacio(denominacio, enablePagination ? PageRequest.of(page, size) : Pageable.unpaged()).getContent());
        return IDomainEventService.changeListToPageEventEntity(eventEntities, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els esdeveniments que contenen el preu passat per paràmetre
     * Resultat: Retorna els esdeveniments que contenen el preu passat per paràmetre
     */
    public Page<EventEntity> findAllByPreu(String preu, Integer page, Integer size, Boolean enablePagination) {
        List <EventEntity> eventEntities = mapper.listModelsToEntities(iEventRepository.findAllByPreu(preu, enablePagination ? PageRequest.of(page, size) : Pageable.unpaged()).getContent());
        return IDomainEventService.changeListToPageEventEntity(eventEntities, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els esdeveniments que es porten a terme dins de les dates passades per paràmetre
     * Resultat: Retorna els esdeveniments que compleixen la condició
     */
    public Page<EventEntity> findAllByDate(LocalDate dataIni, LocalDate dataFi, Integer page, Integer size, Boolean enablePagination) {
        List <EventEntity> eventEntities = mapper.listModelsToEntities(iEventRepository.findAllByDate(dataIni, dataFi, enablePagination ? PageRequest.of(page, size) : Pageable.unpaged()).getContent());
        return IDomainEventService.changeListToPageEventEntity(eventEntities, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els esdeveniments que es contenen la comarca i municipi passades per paràmetre
     * Resultat: Retorna els esdeveniments que compleixen la condició
     */
    public Page<EventEntity> findAllByComarcaMunicipi(String comarcaMunicipi, Integer page, Integer size, Boolean enablePagination) {
        List <EventEntity> eventEntities = mapper.listModelsToEntities(iEventRepository.findAllByComarcaMunicipi(comarcaMunicipi, enablePagination ? PageRequest.of(page, size) : Pageable.unpaged()).getContent());
        return IDomainEventService.changeListToPageEventEntity(eventEntities, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els esdeveniments que contenen la descripció passada per paràmetre
     * Resultat: Retorna els esdeveniments que compleixen la condició
     */
    public Page<EventEntity> findAllByDescripcio(String descripcio, Integer page, Integer size, Boolean enablePagination) {
        List <EventEntity> eventEntities = mapper.listModelsToEntities(iEventRepository.findAllByDescripcio(descripcio, enablePagination ? PageRequest.of(page, size) : Pageable.unpaged()).getContent());
        return IDomainEventService.changeListToPageEventEntity(eventEntities, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els esdeveniments que compleixen els filtres passats per paràmetre
     * Resultat: Retorna els esdeveniments que compleixen la condició
     */
    public Page<EventEntity>  findAllByFilters(String ambit, String categoria, String altres, LocalDate dataIni, LocalDate dataFi,
                                   String preu, String denominacio, String comarcaMunicipi, String descripcio, Float radi, Float latitud, Float longitud,
                                   Integer page, Integer size, Boolean enablePagination) {
        List <EventEntity> eventEntities = mapper.listModelsToEntities(iEventRepository.findAllByFilters(ambit, categoria, altres, dataIni, dataFi, preu, denominacio, comarcaMunicipi, descripcio, radi, latitud, longitud, enablePagination ? PageRequest.of(page, size) : Pageable.unpaged()).getContent());
        return IDomainEventService.changeListToPageEventEntity(eventEntities, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els esdeveniments que contenen l'àmbit passat per paràmetre
     * Resultat: Retorna els esdeveniments que compleixen la condició
     */
    public Page<EventEntity> findAllByAmbit(String ambit, Integer page, Integer size, Boolean enablePagination) {
        List <EventEntity> eventEntities = mapper.listModelsToEntities(iEventRepository.findAllByAmbit(ambit, enablePagination ? PageRequest.of(page, size) : Pageable.unpaged()).getContent());
        return IDomainEventService.changeListToPageEventEntity(eventEntities, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els esdeveniments que contenen la categoria passada per paràmetre
     * Resultat: Retorna els esdeveniments que compleixen la condició
     */
    public Page<EventEntity> findAllByCategoria(String categoria, Integer page, Integer size, Boolean enablePagination) {
        List <EventEntity> eventEntities = mapper.listModelsToEntities(iEventRepository.findAllByCategoria(categoria, enablePagination ? PageRequest.of(page, size) : Pageable.unpaged()).getContent());
        return IDomainEventService.changeListToPageEventEntity(eventEntities, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els àmbits, categories i altres categories que es troben a la BD
     */
    public List<String> findAllCategories() {
        return iEventRepository.findAllCategories();
    }

    /*
     * Descripció: Crida al mètode d'EventRepository que troba tots els esdeveniments que contenen les altres categories passades per paràmetre
     * Resultat: Retorna els esdeveniments que compleixen la condició
     */
    public Page<EventEntity> findAllByAltres(String altres, Integer page, Integer size, Boolean enablePagination) {
        List <EventEntity> eventEntities = mapper.listModelsToEntities(iEventRepository.findAllByAltres(altres, enablePagination ? PageRequest.of(page, size) : Pageable.unpaged()).getContent());
        return IDomainEventService.changeListToPageEventEntity(eventEntities, page, size, enablePagination);
    }

    /*
    * Descripció: Crida al mètode d'EventRepository que afegeix una valoració a un esdeveniment
    * Resultat: Retorna l'esdeveniment amb la valoració afegida
    */
    public EventEntity rateEvent(Long eventId, String userId, Float score) {
        iEventRepository.rateEvent(eventId, userId, score);
        Optional<Event> e = iEventRepository.findById(eventId);
        if (e.isEmpty()) throw new ObjectNotFoundException("Event not found");
        e.get().setScores(getRatingsAsAMap(e.get().getId()));
        return mapper.modelToEntity(e.get());
    }

    /*
    * Descripció: Comprova si un usuari ja ha fet una valoració d'un esdeveniment en concret
    */
    public boolean userAlreadyRatedEvent(Long eventId, String userId) {
        return iEventRepository.existsRating(eventId, userId);
    }

    /*
    * Descripció: Esborra la puntuació d'un esdeveniment per part d'un usuari
    */
    public void unrateEvent(Long eventId, String userId) {
        iEventRepository.deleteRating(eventId, userId);
    }

    /*
    * Descripció: Retorna un Map<String, Float> que conté els usuaris i la puntuació d'un esdeveniment
    */
    public Map<String, Float> getRatingsAsAMap(Long eventId) {
        Map<String, Float> scores = new HashMap<>();
        List<String> users = iEventRepository.findRatingUsers(eventId);
        for (String user : users) {
            scores.put(user, iEventRepository.getRating(eventId, user));
        }
        return scores;
    }

    /*
    * Descripció: Esborra totes les valoracions d'esdeveniments d'un usuari en concret
    */
    public void deleteRatingsByUserId(String userId) {
        iEventRepository.deleteRatingsByUserId(userId);
    }

    /*
    * Descripció: Obté els esdeveniments recomanats per categories d'un usuari en concret
    */
    public Page<EventEntity> getSuggestedEvents(String[] categories, Pageable pageable) {
        Page<Event> p;
        List<Event> l = new ArrayList<>();
        for (String category : categories) {
            p = iEventRepository.findRecommendationsFromGivenCategory(category, pageable);
            l.addAll(p.getContent());
        }
        return new PageImpl<>(mapper.listModelsToEntities(l), pageable, l.size());
    }

    /*
    * Descripció: Obté els esdeveniments amb més popularitat (els que tenen més assistents)
    */
    public Page<EventEntity> getPopularEvents(Pageable pageable) {
        List<Long> ids = iAssistanceService.getPopularEventIds();
        List<Event> l = new ArrayList<>();
        Optional<Event> e;
        for (Long id : ids) {
            e = iEventRepository.findById(id);
            if (e.isEmpty()) throw new ObjectNotFoundException("Event with id " + id + " not found");
            l.add(e.get());
        }
        return new PageImpl<>(mapper.listModelsToEntities(l), pageable, l.size());
    }

    /*
    * Descripció: Comprova si existeix un esdeveniment a la nostra BD amb la mateixa data d'inici, data de fi,
    * denominació i descripció.
    * Resultat: Retorna cert si existeix, en cas contrari retorna fals.
    */
    public boolean existsEventInOurDataBase(LocalDate dataInici, LocalDate dataFi, String denominacio, String descripcio) {
        return iEventRepository.existsEventInOurDataBase(dataInici, dataFi, denominacio, descripcio);
    }

    /*
     * Descripció: Obté la valoració d'un esdeveniment per part d'un usuari, o nul altrament
     */
    public Optional<Float> getRating(String userId, Long eventId) {
        return iEventRepository.findScoreByEventId(userId, eventId);
    }
}