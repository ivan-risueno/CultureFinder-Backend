package pes.CultureFinderBackend.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.controllers.dtos.EventDTO;
import pes.CultureFinderBackend.controllers.dtos.ListDTO;
import pes.CultureFinderBackend.data.services.IEventService;
import pes.CultureFinderBackend.data.services.ISavedEventsListService;
import pes.CultureFinderBackend.data.services.IUserService;
import pes.CultureFinderBackend.domain.businesslogic.EventLogic;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.exceptions.PermissionDeniedException;
import pes.CultureFinderBackend.domain.mappers.DomainEventMapper;
import pes.CultureFinderBackend.domain.mappers.DomainListMapper;
import pes.CultureFinderBackend.domain.models.EventEntity;
import pes.CultureFinderBackend.domain.models.ListEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DomainListService implements IDomainListService {

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori de les llistes.
    */
    @Autowired
    private ISavedEventsListService iSavedEventsListService;

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori dels usuaris.
     */
    @Autowired
    private IUserService iUserService;

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori dels esdeveniment.
     */
    @Autowired
    private IEventService iEventService;

    /*
     * Descripció: Mapper de domini que s'encarrega de fer la conversió entre DTO i Entitat de llista.
     */
    @Autowired
    private DomainListMapper mapper;

    /*
     * Descripció: Mapper de domini que s'encarrega de fer la conversió entre DTO i Entitat d'Event.
     */
    @Autowired
    private DomainEventMapper eventMapper;

    /*
     * Descripció: Crida al mètode de SavedEventsListService que troba totes les llistes d'un usuari
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna totes les llistes d'un usuari, amb paginació si així s'ha indicat
     */
    public Page<ListDTO> getAllUserLists(String apiToken, Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        String userId = iUserService.getProfileByAPIToken(apiToken).getId();
        List<ListEntity> l = iSavedEventsListService.getAllUserLists(userId, page, size, enablePagination).toList();
        List<ListDTO> ret = new ArrayList<>();
        ListDTO dto;
        for (ListEntity list : l) {
            dto = mapper.entityToDTO(list);
            addImagesToListDTO(dto);
            ret.add(dto);
        }
        return new PageImpl<>(ret, pageable, ret.size());
    }

    /*
     * Descripció: Crida al mètode de SavedEventsListService que troba tots els esdeveniments continguts a una llista
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna tots els esdeveniments d'una llista, amb paginació si així s'ha indicat
     */
    public Page<EventDTO> getAllEventsFromList(String apiToken, Long listId, Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        String userId = iUserService.getProfileByAPIToken(apiToken).getId();
        if (!userId.equals(iSavedEventsListService.getOwner(listId)))
            throw new PermissionDeniedException("User with id + " + userId + " is not the owner of the list");
        List<EventEntity> l = iSavedEventsListService.getAllEventsFromList(listId, page, size, enablePagination).toList();
        List<EventDTO> ret = new ArrayList<>();
        for (EventEntity e : l) {
            ret.add(eventMapper.entityToDTO(e));
        }
        return new PageImpl<>(ret, pageable, ret.size());
    }

    /*
     * Descripció: Crea una llista nova i la guarda a la BD
     * <apiToken>: API token de l'usuari propietari de la llista
     * <name>: Nom de la nova llista
     * Resultat: Es retorna un DTO que representa la llista nova
     */
    public ListDTO createEmptyList(String apiToken, String name) {
        String userId = iUserService.getProfileByAPIToken(apiToken).getId();
        ListEntity newList = new ListEntity();
        newList.setUserId(userId);
        newList.setName(name);
        newList.setDescription("Llista dels esdeveniments que m'interessen!");
        newList.setEvents(new HashSet<>());
        return mapper.entityToDTO(iSavedEventsListService.saveList(newList));
    }

    /*
     * Descripció: Edita el nom d'una llista guardada a la BD
     * <apiToken>: API token de l'usuari propietari de la llista
     * <listId>: Identificador de la llista
     * <name>: Nou nom per a la llista
     * Resultat: Es retorna un DTO que representa la llista actualitzada
     */
    public ListDTO editList(String apiToken, Long listId, String name, String description) {
        String userId = iUserService.getProfileByAPIToken(apiToken).getId();
        ListEntity l = iSavedEventsListService.getListById(listId);
        if (!userId.equals(iSavedEventsListService.getOwner(listId)))
            throw new PermissionDeniedException("User with id + " + userId + " is not the owner of the list");
        if (name != null && !name.equals("")) l.setName(name);
        if (description != null && !description.equals("")) l.setDescription(description);
        return mapper.entityToDTO(iSavedEventsListService.editList(l));
    }

    /*
    * Descripció: Comprova si una llista existeix a la BD
    */
    public boolean existsById(Long listId) {
        return iSavedEventsListService.existsById(listId);
    }

    /*
     * Descripció: Esborra una llista existent a la BD
     */
    public void deleteList(String apiToken, Long listId) {
        String userId = iUserService.getProfileByAPIToken(apiToken).getId();
        if (!userId.equals(iSavedEventsListService.getOwner(listId)))
            throw new PermissionDeniedException("User with id + " + userId + " is not the owner of the list");
        if (!iSavedEventsListService.existsById(listId)) throw new ObjectNotFoundException("List with id " + listId + " not found");
        iSavedEventsListService.deleteList(listId);
    }

    /*
    * Descripció: Afegeix un esdeveniment a una llista de la BD
    */
    public ListDTO addEvent(String apiToken, Long listId, Long eventId) {
        String userId = iUserService.getProfileByAPIToken(apiToken).getId();
        if (!userId.equals(iSavedEventsListService.getOwner(listId)))
            throw new PermissionDeniedException("User with id + " + userId + " is not the owner of the list");
        if (!iSavedEventsListService.existsById(listId)) throw new ObjectNotFoundException("List with id " + listId + " not found");
        ListEntity l = iSavedEventsListService.getListById(listId);
        if (l.getEvents().contains(eventId)) throw new PermissionDeniedException("The list already contains the event");
        Set<Long> events = l.getEvents();
        events.add(eventId);
        l.setEvents(events);
        ListDTO dto = mapper.entityToDTO(iSavedEventsListService.saveList(l));
        addImagesToListDTO(dto);
        return dto;
    }

    /*
    * Afegeix les primeres quatre imatges a un DTO de llista
    */
    private void addImagesToListDTO(ListDTO dto) {
        List<EventEntity> eventsFromList = new ArrayList<>();
        for (Long eventId : dto.getEvents()) {
            eventsFromList.add(iEventService.findById(eventId).get());
        }
        dto.setFirstImages(EventLogic.getImages(eventsFromList));
    }

    /*
     * Descripció: Esborra un esdeveniment d'una llista de la BD
     */
    public ListDTO removeEvent(String apiToken, Long listId, Long eventId) {
        String userId = iUserService.getProfileByAPIToken(apiToken).getId();
        if (!userId.equals(iSavedEventsListService.getOwner(listId)))
            throw new PermissionDeniedException("User with id + " + userId + " is not the owner of the list");
        if (!iSavedEventsListService.existsById(listId)) throw new ObjectNotFoundException("List with id " + listId + " not found");
        ListEntity l = iSavedEventsListService.getListById(listId);
        if (!l.getEvents().contains(eventId)) throw new ObjectNotFoundException("The list does not contain the event");
        Set<Long> events = l.getEvents();
        events.remove(eventId);
        l.setEvents(events);
        return mapper.entityToDTO(iSavedEventsListService.saveList(l));
    }

    /*
    * Descripció: Comprova si un esdeveniment es troba a una llista d'esdeveniments
    */
    public boolean existsEventInList(Long listId, Long eventId) {
        if (!iSavedEventsListService.existsById(listId)) throw new ObjectNotFoundException("List with id " + listId + "not found");
        return iSavedEventsListService.existsEventInList(listId, eventId);
    }
}
