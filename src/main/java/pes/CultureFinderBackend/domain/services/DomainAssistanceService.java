package pes.CultureFinderBackend.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.controllers.dtos.AssistanceDTO;
import pes.CultureFinderBackend.controllers.dtos.EventDTO;
import pes.CultureFinderBackend.data.services.IAssistanceService;
import pes.CultureFinderBackend.data.services.IEventService;
import pes.CultureFinderBackend.data.services.IUserService;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.mappers.DomainAssistanceMapper;
import pes.CultureFinderBackend.domain.mappers.DomainEventMapper;
import pes.CultureFinderBackend.domain.models.AssistanceEntity;
import pes.CultureFinderBackend.domain.models.EventEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DomainAssistanceService implements IDomainAssistanceService {

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori d'assistències
     */
    @Autowired
    private IAssistanceService iAssistanceService;

    /*
     * Descripció: Instància del servei que s'encarrega de generar i parsejar json web tokens
     */
    @Autowired
    private ISecurityService iSecurityService;

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori d'esdeveniments
     */
    @Autowired
    private IEventService iEventService;

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori d'usuaris
     */
    @Autowired
    private IUserService iUserService;

    /*
    * Descripció: Instància del mapper que mapeja entre assistències entitats i DTOs
    */
    @Autowired
    private DomainAssistanceMapper mapper;

    /*
     * Descripció: Instància del mapper que mapeja entre esdeveniments entitats i DTOs
     */
    @Autowired
    private DomainEventMapper eventMapper;

    /*
    * Descripció: Crida a l'AssistanceService que guarda una assistència
    */
    public AssistanceDTO saveNewAssistance(String apiToken, Long eventId) {
        return mapper.entityToDTO(iAssistanceService.saveAssistance(apiToken, eventId));
    }

    /*
     * Descripció: Crida a l'AssistanceService que obté totes les assistències
     */
    public Page<AssistanceDTO> getAllAssistances(Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        Page<AssistanceEntity> p = iAssistanceService.getAllAssistances(page, size, enablePagination);
        List<AssistanceDTO> ret = new ArrayList<>();
        for (AssistanceEntity entity : p.getContent()) {
            ret.add(mapper.entityToDTO(entity));
        }
        return new PageImpl<>(ret, pageable, ret.size());
    }

    /*
     * Descripció: Crida a l'AssistanceService que esborra una assistència
     */
    public void deleteAssistance(String apiToken, Long eventId) {
        iAssistanceService.deleteAssistance(apiToken, eventId);
    }

    /*
     * Descripció: Crida a l'AssistanceService que comprova si existeix una assistència
     */
    public boolean existsById(String apiToken, Long eventId) {
        return iAssistanceService.existsById(apiToken, eventId);
    }

    /*
    * Descripció: Obté els esdeveniments als que un usuari assistirà
    */
    public Page<EventDTO> getAllUserAssistances(String apiToken, Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        String userId;
        try {
            userId = (String) iSecurityService.buildMappedObjectFromApiToken(apiToken).get("userId");
        } catch (Exception e) {
            throw new ObjectNotFoundException("User not found");
        }
        if (!iUserService.existsById(userId)) throw new ObjectNotFoundException("User " + userId + " not found");
        List<Long> eventIds = iAssistanceService.getAllByUserId(userId);
        List<EventDTO> ret = new ArrayList<>();
        Optional<EventEntity> e;
        for (Long id : eventIds) {
            e = iEventService.findById(id);
            if (e.isEmpty()) throw new ObjectNotFoundException("Event with id " + id + " not found");
            ret.add(eventMapper.entityToDTO(e.get()));
        }
        return new PageImpl<>(ret, pageable, ret.size());
    }
}
