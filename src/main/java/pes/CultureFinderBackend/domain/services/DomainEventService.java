package pes.CultureFinderBackend.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pes.CultureFinderBackend.controllers.dtos.EventDTO;
import pes.CultureFinderBackend.data.models.Event;
import pes.CultureFinderBackend.data.services.IEventService;
import pes.CultureFinderBackend.data.services.IUserService;
import pes.CultureFinderBackend.domain.businesslogic.EventLogic;
import pes.CultureFinderBackend.domain.businesslogic.UserLogic;
import pes.CultureFinderBackend.domain.exceptions.ObjectAlreadyExistsException;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.mappers.DomainEventMapper;
import pes.CultureFinderBackend.domain.models.EventEntity;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DomainEventService implements IDomainEventService {

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori d'events
     */
    @Autowired
    private IEventService iEventService;

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori d'usuaris
     */
    @Autowired
    private IUserService iUserService;

    /*
     * Descripció: Mapper de domini que s'encarrega de fer la conversió entre DTO i Entitat d'Event
     */
    @Autowired
    private DomainEventMapper mapper;

    /*
     * Descripció: Instància de la interfície que treballa amb les API tokens
     */
    @Autowired
    private ISecurityService iSecurityService;

    /*
     * Descripció: Instància de la classe que tracta els usuaris aplicat lògica de negoci
     */
    @Autowired
    private UserLogic userLogic;

    /*
     * Descripció: Crida al mètode d'EventService que guarda un esdeveniment a la BD mapejant el DTO a Entitat
     * Resultat: Retorna l'esdeveniment que s'ha guardat en format DTO
     */
    public EventDTO saveEvent(EventDTO event) {
        if (event == null) {
            throw new ObjectNotFoundException("Event not found");
        }

        EventEntity eventEntity = mapper.dtoToEntity(event);
        List<EventEntity> eventsEntities = iEventService.findAllByFilters(event.getAmbit(), event.getCategoria(), event.getAltresCategories(), event.getDataInici(), event.getDataFi(),
                event.getPreu(), event.getDenominacio(), event.getComarcaIMunicipi(), event.getDescripcio(), null, 0.0f, 0.0f, 0, 10, true).getContent();
        if (eventsEntities.size() > 0) {
            if (iEventService.existsById(eventsEntities.get(0).getId())) {
                throw new ObjectAlreadyExistsException("Event already exists");
            }
        }

        return mapper.entityToDTO(iEventService.saveEvent(eventEntity));
    }

    /*
     * Descripció: Crida al mètode d'EventService que esborra un esdeveniment
     */
    public void deleteEvent(Long id) {
        iEventService.deleteEvent(id);
    }

    /*
     * Descripció: Crida al mètode d'EventService que comprova si existeix un esdeveniment donat un id
     * Resultat: Retorna cert si existeix l'esdeveniment, fals en cas contrari
     */
    public boolean existsById(Long id) {
        return iEventService.existsById(id);
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba tots els esdeveniments. Es fa la conversió per cada esdeveniment
     * d'EventEntity a EventDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna tots els esdeveniments de la BD, amb paginació si així s'ha indicat
     */
    public Page<EventDTO> getAllEvents(Integer page, Integer size, Boolean enablePagination) {
        List<EventEntity> eventsEntity = iEventService.getAllEvents(page, size, enablePagination).getContent();
        List<EventDTO> eventsDTO = mapper.listEntitiesToDTO(eventsEntity);
        return IDomainEventService.changeListToPageEventDTO(eventsDTO, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba l'esdeveniment donat un id. Es fa la conversió d'EventEntity a EventDTO
     * Resultat: Retorna l'esdeveniment de la BD amb l'id indicat
     */
    public Optional<EventDTO> findById(Long id) {
        EventEntity e = iEventService.findById(id).orElse(null);
        return Optional.ofNullable(mapper.entityToDTO(e));
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba tots els esdeveniments donat un rang de dates. Es fa la conversió per cada esdeveniment
     * de EventEntity a EventDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els esdeveniments de la BD donat un rang de dates, amb paginació si així s'ha indicat
     */
    public Page<EventDTO> findAllByDate(LocalDate dataIni, LocalDate dataFi, Integer page, Integer size, Boolean enablePagination) {
        List<EventDTO> eventDtos = mapper.listEntitiesToDTO(iEventService.findAllByDate(dataIni, dataFi, page, size, enablePagination).getContent());
        return IDomainEventService.changeListToPageEventDTO(eventDtos, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba tots els esdeveniments donat una denominació. Es fa la conversió per cada esdeveniment
     * de EventEntity a EventDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els esdeveniments de la BD donat una denominació, amb paginació si així s'ha indicat
     */
    public Page<EventDTO> findAllByDenominacio(String denominacio, Integer page, Integer size, Boolean enablePagination) {
        List<EventDTO> eventDtos = mapper.listEntitiesToDTO(iEventService.findAllByDenominacio(denominacio, page, size, enablePagination).getContent());
        return IDomainEventService.changeListToPageEventDTO(eventDtos, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba tots els esdeveniments donat un preu. Es fa la conversió per cada esdeveniment
     * de EventEntity a EventDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els esdeveniments de la BD donat un preu, amb paginació si així s'ha indicat
     */
    public Page<EventDTO> findAllByPreu(String preu, Integer page, Integer size, Boolean enablePagination) {
        List<EventDTO> eventDtos = mapper.listEntitiesToDTO(iEventService.findAllByPreu(preu, page, size, enablePagination).getContent());
        return IDomainEventService.changeListToPageEventDTO(eventDtos, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventService que indica totes les categories amb les quals es pot filtrar
     * Resultat: Retorna els tags amb els quals es pot filtrar
     */
    public List<String> findAllCategories() {
        return iEventService.findAllCategories();
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba tots els esdeveniments donat un conjunt de filtres. Es fa la conversió per cada esdeveniment
     * de EventEntity a EventDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els esdeveniments de la BD donat un conjunt de filtres, amb paginació si així s'ha indicat
     */
    public Page<EventDTO> findAllByFilters(String ambit, String categoria, String altres, LocalDate dataIni, LocalDate dataFi, String preu, String denominacio, String comarcaMunicipi, String descripcio, Float radi, Float latitud, Float longitud, Integer page, Integer size, Boolean enablePagination) {
        List<EventDTO> eventDtos = mapper.listEntitiesToDTO(iEventService.findAllByFilters(ambit, categoria, altres, dataIni, dataFi, preu, denominacio, comarcaMunicipi, descripcio, radi, latitud, longitud, page, size, enablePagination).getContent());
        return IDomainEventService.changeListToPageEventDTO(eventDtos, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba tots els esdeveniments donat l'àmbit. Es fa la conversió per cada esdeveniment
     * de EventEntity a EventDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els esdeveniments de la BD donat l'àmbit, amb paginació si així s'ha indicat
     */
    public Page<EventDTO> findAllByAmbit(String ambit, Integer page, Integer size, Boolean enablePagination) {
        List<EventDTO> eventDtos = mapper.listEntitiesToDTO(iEventService.findAllByAmbit(ambit, page, size, enablePagination).getContent());
        return IDomainEventService.changeListToPageEventDTO(eventDtos, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba tots els esdeveniments donada la categoria. Es fa la conversió per cada esdeveniment
     * de EventEntity a EventDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els esdeveniments de la BD donada la categoria, amb paginació si així s'ha indicat
     */
    public Page<EventDTO> findAllByCategoria(String categoria, Integer page, Integer size, Boolean enablePagination) {
        List<EventDTO> eventDtos = mapper.listEntitiesToDTO(iEventService.findAllByCategoria(categoria, page, size, enablePagination).getContent());
        return IDomainEventService.changeListToPageEventDTO(eventDtos, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba tots els esdeveniments donades altres categories. Es fa la conversió per cada esdeveniment
     * de EventEntity a EventDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els esdeveniments de la BD donades altres categories, amb paginació si així s'ha indicat
     */
    public Page<EventDTO> findAllByAltres(String altres, Integer page, Integer size, Boolean enablePagination) {
        List<EventDTO> eventDtos = mapper.listEntitiesToDTO(iEventService.findAllByAltres(altres, page, size, enablePagination).getContent());
        return IDomainEventService.changeListToPageEventDTO(eventDtos, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba tots els esdeveniments donada comarca i municipi. Es fa la conversió per cada esdeveniment
     * de EventEntity a EventDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els esdeveniments de la BD donada comarca i municipi, amb paginació si així s'ha indicat
     */
    public Page<EventDTO> findAllByComarcaMunicipi(String comarcaMunicipi, Integer page, Integer size, Boolean enablePagination) {
        List<EventDTO> eventDtos = mapper.listEntitiesToDTO(iEventService.findAllByComarcaMunicipi(comarcaMunicipi, page, size, enablePagination).getContent());
        return IDomainEventService.changeListToPageEventDTO(eventDtos, page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'EventService que troba tots els esdeveniments donada una descripció. Es fa la conversió per cada esdeveniment
     * de EventEntity a EventDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els esdeveniments de la BD donada una descripció, amb paginació si així s'ha indicat
     */
    public  Page<EventDTO> findAllByDescripcio(String descripcio, Integer page, Integer size, Boolean enablePagination) {
        List<EventDTO> eventDtos = mapper.listEntitiesToDTO(iEventService.findAllByDescripcio(descripcio, page, size, enablePagination).getContent());
        return IDomainEventService.changeListToPageEventDTO(eventDtos, page, size, enablePagination);
    }

    /*
     * Descripció: Crida a l'EventService que modifica un esdeveniment donat. Es fa la conversió d'EventDTO a EventEntity
     * Resultat: Retorna l'esdeveniment modificat mapejat a DTO
     */
    public EventDTO editEvent(EventDTO event) {
        EventEntity eventEntity = mapper.dtoToEntity(event);
        return mapper.entityToDTO(iEventService.editEvent(eventEntity));
    }

    /*
    * Descripció: Crida a l'EventService que afegeix una puntuació d'un esdeveniment per part d'un usuari loguejat
    * Resultat: Retorna l'esdeveniment modificat mapejat a DTO
    */
    public EventDTO rateEvent(String apiToken, Long eventId, Float score) {
        if (iEventService.findById(eventId).isEmpty()) throw new ObjectNotFoundException("Event with id " + eventId + " not found");
        String userId = iUserService.getProfileByAPIToken(apiToken).getId();
        if (iEventService.userAlreadyRatedEvent(eventId, userId)) throw new ObjectAlreadyExistsException("Event already rated by the user");
        EventEntity e = iEventService.rateEvent(eventId, userId, score);
        return mapper.entityToDTO(e);
    }

    /*
    * Descripció: Crida a l'EventService que esborra una puntuació d'un esdeveniment per part d'un usuari loguejat
    */
    public void unrateEvent(String apiToken, Long eventId) {
        if (iEventService.findById(eventId).isEmpty()) throw new ObjectNotFoundException("Event with id " + eventId + " not found");
        String userId = iUserService.getProfileByAPIToken(apiToken).getId();
        if (!iEventService.userAlreadyRatedEvent(eventId, userId)) throw new ObjectNotFoundException("The event is not rated by this user");
        iEventService.unrateEvent(eventId, userId);
    }

    /*
    * Descripció: Crida a l'EventService que comprova si un esdeveniment ha sigut puntuat per un usuari loguejat
    */
    public boolean eventAlreadyRated(String apiToken, Long eventId) {
        String userId = iUserService.getProfileByAPIToken(apiToken).getId();
        return iEventService.userAlreadyRatedEvent(eventId, userId);
    }

    /*
    * Descripció: Crida a l'EventService que obté recomanacions d'esdeveniments per a un usuari en concret
    * <apiToken>: API Token de l'usuari a qui s'ha de fer la recomanació
    * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
    * <size>: Nombre d'elements per pàgina, si enablePagination == true
    * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
    * Resultat: Retorna els esdeveniments recomanats segons les categories de l'usuari, amb paginació si així s'ha indicat
    */
    public Page<EventDTO> getSuggestedEvents(String apiToken, Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        String[] categories = iUserService.getProfileByAPIToken(apiToken).getPreferredCategories().split(",");
        Page<EventEntity> p = iEventService.getSuggestedEvents(categories, pageable);
        List<EventDTO> ret = new ArrayList<>();
        for (EventEntity e : p.getContent()) {
            ret.add(mapper.entityToDTO(e));
        }
        return new PageImpl<>(ret, pageable, ret.size());
    }

    /*
     * Descripció: Crida a l'EventService que obté els esddeveniments més populars
     * <apiToken>: API Token de l'usuari a qui s'ha de fer la recomanació
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els esdeveniments amb més valoracions en ordre
     */
    public Page<EventDTO> getPopularEvents(Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        Page<EventEntity> p = iEventService.getPopularEvents(pageable);
        List<EventDTO> ret = new ArrayList<>();
        for (EventEntity e : p.getContent()) {
            ret.add(mapper.entityToDTO(e));
        }
        return new PageImpl<>(ret, pageable, ret.size());
    }

    /*
    * Descripció: Mètode que s'executa cada dia a les 16:00 per a actualitzar la base de dades amb els esdeveniments de la API.
    */
    @Scheduled(cron = "0 0 16 * * ?")
    public void updateDatabase() throws IOException {
        URL url = new URL("http://localhost:8080/events");
        List<EventEntity> updatedEvents = EventLogic.fetchEvents();
        for (EventEntity eventUpdated : updatedEvents) {
            if (!iEventService.existsEventInOurDataBase(eventUpdated.getDataInici(), eventUpdated.getDataFi(),
                    eventUpdated.getDenominacio(), eventUpdated.getDescripcio())) {
                try {
                    POSTEventFromObject(eventUpdated, url.toURI());
                } catch (Exception e) {
                        System.out.println("Aquest esdeveniment s'ha afegit al fer l'actualització de la base de dades: " + eventUpdated.getDenominacio() + "\n" +
                                "--------------------------------------------------------------------------------------------------------");
                }
            }
        }

        System.out.println("Update finished.");
        try {
            userLogic.checkNearEvents();
        } catch (Exception ignored) {}
    }

    /*
     * Descripció: Crida POST a l'API per a afegir un esdeveniment a la base de dades
     */
    public void POSTEventFromObject(EventEntity e, URI uri) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForEntity(uri, iEventService.saveEvent(e), Event.class);
    }

    /*
     * Descripció: Obté la valoració d'un esdeveniment per part d'un usuari, o nul altrament
     */
    public Float getRating(String apiToken, Long eventId) {
        String userId = (String) iSecurityService.buildMappedObjectFromApiToken(apiToken).get("userId");
        Optional<Float> rating = iEventService.getRating(userId, eventId);
        return rating.orElse(null);
    }
}
