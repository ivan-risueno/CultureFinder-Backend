package pes.CultureFinderBackend.data.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.data.mappers.DataAssistanceMapper;
import pes.CultureFinderBackend.data.models.Assistance;
import pes.CultureFinderBackend.data.models.AssistanceId;
import pes.CultureFinderBackend.data.repositories.IAssistanceRepository;
import pes.CultureFinderBackend.data.repositories.IEventRepository;
import pes.CultureFinderBackend.data.repositories.IUserRepository;
import pes.CultureFinderBackend.domain.exceptions.ObjectAlreadyExistsException;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.models.AssistanceEntity;

import java.util.ArrayList;
import java.util.List;

@Service
public class AssistanceService implements IAssistanceService {

    /*
     * Descripció: Instància del repositori que s'encarrega d'interactuar amb la taula assitances
     */
    @Autowired
    private IAssistanceRepository iAssistanceRepository;

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
     * Descripció: Instància del mapper de dades que mappeja entitats a models i models a entitats d'assistències
     */
    @Autowired
    private DataAssistanceMapper mapper;

    /*
     * Descripció: Crida al mètode d'AssistanceRepository que guarda una assistència
     * Resultat: Retorna l'assistència que s'ha guardat
     */
    public AssistanceEntity saveAssistance(String apiToken, Long eventId) {
        String userId = iUserRepository.getUserIdByAPIToken(apiToken);
        if (iUserRepository.findById(userId).isEmpty()) {
            throw new ObjectNotFoundException("User " + userId + " not found");
        }
        else if (iEventRepository.findById(eventId).isEmpty()) {
            throw new ObjectNotFoundException("Event " + eventId + " not found");
        }

        AssistanceEntity assistance = new AssistanceEntity(userId, eventId);
        AssistanceId id = mapper.entityToId(assistance);

        if (iAssistanceRepository.existsById(id)) {
            throw new ObjectAlreadyExistsException("User + " + userId + " has already an assistance to this event");
        }
        return mapper.modelToEntity(iAssistanceRepository.save(mapper.entityToModel(assistance)));
    }

    /*
     * Descripció: Crida al mètode d'AssistanceRepository que troba totes les assistències
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna totes les asssistències de la BD, amb paginació si així s'ha indicat
     */
    public Page<AssistanceEntity> getAllAssistances(Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        Page<Assistance> p = iAssistanceRepository.findAll(pageable);
        List<AssistanceEntity> ret = new ArrayList<>();
        for (Assistance a : p.getContent()) {
            ret.add(mapper.modelToEntity(a));
        }
        return new PageImpl<>(ret, pageable, ret.size());
    }

    /*
     * Descripció: Crida al mètode d'AssistanceRepository que comprova si una assistència existeix, donat el seu identificador
     * Resultat: Retorna un booleà que indica si l'assistència existeix
     */
    public boolean existsById(String apiToken, Long eventId) {
        String userId = iUserRepository.getUserIdByAPIToken(apiToken);
        return iAssistanceRepository.existsById(mapper.entityToId(new AssistanceEntity(userId, eventId)));
    }

    /*
    * Descripció: Esborra totes les assistències d'un esdeveniment
    */
    public void deleteAssistancesByEvent(Long eventId) {
        if (!iAssistanceRepository.existsByEventId(eventId)) {
            throw new ObjectNotFoundException("Assistances with event id " + eventId + " were not found");
        }
        iAssistanceRepository.deleteByEventId(eventId);
    }

    /*
     * Descripció: Esborra totes les assistències d'un usuari
     */
    public void deleteAssistancesByUserId(String userId) {
        iAssistanceRepository.deleteByUserId(userId);
    }

    /*
     * Descripció: Crida al mètode d'AssistanceRepository que esborra una assistència
     */
    public void deleteAssistance(String apiToken, Long eventId) {
        String userId = iUserRepository.getUserIdByAPIToken(apiToken);
        AssistanceId id = mapper.entityToId(new AssistanceEntity(userId, eventId));
        if (!iAssistanceRepository.existsById(id)) {
            throw new ObjectNotFoundException("Assistance with user id " + id.getUserId() + " and event id " + id.getEventId() + " not found");
        }
        iAssistanceRepository.deleteById(id);
    }

    /*
    * Descripció: Crida al mètode d'AssistanceRepository que obté els identificadors dels esdeveniments més populars
    */
    public List<Long> getPopularEventIds() {
        return iAssistanceRepository.getPopularEventIds();
    }

    /*
    * Descripció: Crida al mètode d'AssistanceRepository que obté els identificadors dels esdeveniments als que un usuari assistirà
    */
    public List<Long> getAllByUserId(String userId) {
        return iAssistanceRepository.getAllByUserId(userId);
    }
}
