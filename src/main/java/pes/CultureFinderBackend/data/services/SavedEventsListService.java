package pes.CultureFinderBackend.data.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.data.mappers.DataEventMapper;
import pes.CultureFinderBackend.data.mappers.DataListMapper;
import pes.CultureFinderBackend.data.models.Event;
import pes.CultureFinderBackend.data.models.SavedEventsList;
import pes.CultureFinderBackend.data.repositories.IEventRepository;
import pes.CultureFinderBackend.data.repositories.ISavedEventsListRepository;
import pes.CultureFinderBackend.data.repositories.IUserRepository;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.exceptions.PermissionDeniedException;
import pes.CultureFinderBackend.domain.models.EventEntity;
import pes.CultureFinderBackend.domain.models.ListEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SavedEventsListService implements ISavedEventsListService {

    /*
     * Descripció: Instància del repositori que s'encarrega d'interactuar amb la tauls SavedEventsList
     */
    @Autowired
    private ISavedEventsListRepository iSavedEventsListRepository;

    /*
     * Descripció: Instància del repositori que s'encarrega d'interactuar amb la taula users
     */
    @Autowired
    private IUserRepository iUserRepository;

    /*
     * Descripció: Instància del repositori que s'encarrega d'interactuar amb la taula events
     */
    @Autowired
    private IEventRepository iEventRepository;

    /*
     * Descripció: Instància del mapper que s'encarrega de mapejar Model a Entity i viceversa
     */
    @Autowired
    private DataListMapper mapper;

    /*
     * Descripció: Instància del mapper que s'encarrega de mapejar Model a Entity i viceversa
     */
    @Autowired
    private DataEventMapper eventMapper;

    /*
     * Descripció: Crida al mètode d'un SavedEventsListRepository que guarda una llista d'esdeveniments
     * Resultat: Retorna la llista que s'ha guardat
     */
    public ListEntity saveList(ListEntity savedEventsList) {
        if (iUserRepository.findById(savedEventsList.getUserId()).isEmpty()) {
            throw new ObjectNotFoundException("The list belongs to an user with id " + savedEventsList.getUserId() + ", which was not found");
        }

        for (Long eventId : savedEventsList.getEvents()) {
            if (iEventRepository.findById(eventId).isEmpty())  {
                throw new ObjectNotFoundException("The list contains an event with id " + eventId + ", which was not found");
            }
        }

        return mapper.modelToEntity(iSavedEventsListRepository.save(mapper.entityToModel(savedEventsList)));
    }

    /*
     * Descripció: Crida al mètode de SavedEventsListRepository que troba totes les llistes de la taula lists
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna totes les llistes de la BD, amb paginació si així s'ha indicat
     */
    public Page<ListEntity> getAllUserLists(String userId, Integer page, Integer size, Boolean enablePagination) {
        if (iUserRepository.findById(userId).isEmpty()) {
            throw new ObjectNotFoundException("The list belongs to an user with id " + userId + ", which was not found");
        }
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        Page<SavedEventsList> p = iSavedEventsListRepository.findAllByUserId(userId, pageable);
        List<ListEntity> ret = new ArrayList<>();

        for (SavedEventsList l : p.getContent()) {
            ret.add(mapper.modelToEntity(l));
        }
        return new PageImpl<>(ret, pageable, ret.size());
    }

    /*
    * Descripció: Crida al mètode de ISavedEventsListRepository que obté tots els esdeveniments d'una llista
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna tots els esdeveniments d'una llista, amb paginació si així s'ha indicat
    */
    public Page<EventEntity> getAllEventsFromList(Long listId, Integer page, Integer size, Boolean enablePagination) {
        Optional<SavedEventsList> l = iSavedEventsListRepository.findById(listId);
        if (l.isEmpty()) throw new ObjectNotFoundException(("List with identifier " + listId + "not found"));
        List<EventEntity> ret = new ArrayList<>();
        Optional<Event> e;
        for (Long eventId : l.get().getEvents()) {
            e = iEventRepository.findById(eventId);
            if (e.isEmpty()) throw new ObjectNotFoundException(("Event with identifier " + eventId + "not found"));
            ret.add(eventMapper.modelToEntity(e.get()));
        }

        return new PageImpl<>(ret, enablePagination ? PageRequest.of(page, size) : Pageable.unpaged(), ret.size());
    }

    /*
     * Descripció: Crida al mètode de ISavedEventsListRepository que guarda una llista
     * <list>: Llista editada obtinguda com a paràmetre de la crida de l'API
     * Resultat: Retorna la llista que s'ha guardat
     */
    public ListEntity editList (ListEntity list) {
        if (list.getName().equals("Favorits")) throw new PermissionDeniedException("Permission denied. Name \"Favorits\" is a reserved word");
        if (iSavedEventsListRepository.findById(list.getId()).isPresent()) {
            if (iUserRepository.findById(list.getUserId()).isEmpty()) {
                throw new ObjectNotFoundException("The list belongs to an user with id " + list.getUserId() + ", which was not found");
            }

            for (Long eventId : list.getEvents()) {
                if (iEventRepository.findById(eventId).isEmpty())  {
                    throw new ObjectNotFoundException("The list contains an event with id \" + eventId + \", which was not found");
                }
            }
        } else {
            throw new ObjectNotFoundException("List with id " + list.getId() + " not found");
        }

        return mapper.modelToEntity(iSavedEventsListRepository.save(mapper.entityToModel(list)));
    }

    /*
     * Descripció: Crida al mètode de SavedEventsListRepository que comprova si existeix una llista amb un determinat identificador
     * <id>: Identificador de la llista
     * Resultat: Retorna cert si existeix la llista, fals en cas contrari
     */
    public boolean existsById(Long id) { return iSavedEventsListRepository.existsById(id); }

    /*
     * Descripció: Crida al mètode de SavedEventsListRepository que esborra una llista donat el seu identificador
     */
    public void deleteList(Long id) {
        iSavedEventsListRepository.deleteById(id);
    }

    /*
     * Descripció: Crida al mètode de SavedEventsListRepository que esborra una llista donat l'identificador de l'usuari
     */
    public void deleteListsByUserId(String userId) {
        iSavedEventsListRepository.deleteAllByUserId(userId);
    }

    /*
    * Descripció: Crida al mètode de SavedEventsListRepository que retorna l'identificador de l'usuari propietari de la llista
    */
    public String getOwner(Long listId) {
        Optional<SavedEventsList> l = iSavedEventsListRepository.findById(listId);
        if (l.isEmpty()) throw new ObjectNotFoundException("List with id " + listId + " not found");
        return l.get().getUserId();
    }

    /*
    * Descripció: Obté una llista donat el seu identificador
    */
    public ListEntity getListById(Long listId) {
        Optional<SavedEventsList> l = iSavedEventsListRepository.findById(listId);
        if (l.isEmpty()) throw new ObjectNotFoundException("List with id " + listId + " not found");
        return mapper.modelToEntity(l.get());
    }

    /*
    * Descripció: Comprova si un esdeveniment es troba a una llista
    */
    public boolean existsEventInList(Long listId, Long eventId) {
        return iSavedEventsListRepository.existsEventInList(listId, eventId);
    }
}
