package pes.CultureFinderBackend.data.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.data.mappers.DataIncidentMapper;
import pes.CultureFinderBackend.data.models.Incident;
import pes.CultureFinderBackend.data.repositories.IEventRepository;
import pes.CultureFinderBackend.data.repositories.IIncidentRepository;
import pes.CultureFinderBackend.data.repositories.IUserRepository;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.models.IncidentEntity;
import pes.CultureFinderBackend.domain.services.IDomainIncidentService;

import java.util.List;

@Service
public class IncidentService implements IIncidentService {

    /*
     * Descripció: Instància del repositori que s'encarrega d'interactuar amb la taula incidents
     */
    @Autowired
    private IIncidentRepository iIncidentRepository;
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
     * Descripció: Instància del mapper que s'encarrega de fer la conversió de DTO a Entity i viceversa
     */
    @Autowired
    private DataIncidentMapper mapper;

    /*
     * Descripció: Crida al mètode d'un IncidentRepository que guarda una incidència
     * Resultat: Retorna la incidència que s'ha guardat
     */
    public IncidentEntity saveIncident(IncidentEntity incident) {
        if (iUserRepository.findById(incident.getUserId()).isEmpty()) {
            throw new ObjectNotFoundException("User with id " + incident.getUserId() + " not found");
        }

        else if (iEventRepository.findById(incident.getEventId()).isEmpty())  {
            throw new ObjectNotFoundException("Event with id " + incident.getEventId() + " not found");
        }

        return mapper.modelToEntity(iIncidentRepository.save(mapper.entityToModel(incident)));
    }

    /*
     * Descripció: Crida al mètode d'IncidentRepository que troba totes les incidències de la taula incidents
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna tots els esdeveniments de la BD, amb paginació si així s'ha indicat
     */
    public Page<IncidentEntity> getAllIncidents(Integer page, Integer size, Boolean enablePagination) {
        return IDomainIncidentService.changeListToPageIncidentEntity(mapper.listModelsToEntities(iIncidentRepository.findAll(enablePagination
                ? PageRequest.of(page, size) : Pageable.unpaged()).getContent()), page, size, enablePagination);
    }

    public  Page<IncidentEntity> getIncidentsByUser(String userId, Integer page, Integer size, Boolean enablePagination) {
        return IDomainIncidentService.changeListToPageIncidentEntity(mapper.listModelsToEntities(iIncidentRepository.findAllByUser(userId, enablePagination
                ? PageRequest.of(page, size) : Pageable.unpaged()).getContent()), page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'IncidentRepository que troba una incidència donat l'id de l'usuari i el seu status
     * Resultat: Retorna les incidències trobades a la BD
     */
    public List<IncidentEntity> findByUserAndResolved(String userId, Boolean resolved) {

        if (iUserRepository.findById(userId).isEmpty()) {
            throw new ObjectNotFoundException("User with id " + userId + " not found");
        }

        return mapper.listModelsToEntities(iIncidentRepository.findByUserAndResolved(userId, resolved));
    }

    /*
     * Descripció: Crida al mètode d'IncidentRepository que troba una incidència donat l'id del event i el seu status
     * Resultat: Retorna les incidències trobades a la BD
     */
    public List<IncidentEntity> findByEventAndResolved(Long eventId, Boolean resolved) {
        return mapper.listModelsToEntities(iIncidentRepository.findByEventAndResolved(eventId, resolved));
    }

    public List<IncidentEntity> findByEventAndUserAndResolved(String userId, Long eventId, Boolean resolved) {
        return mapper.listModelsToEntities(iIncidentRepository.findByEventAndUserAndResolved(userId, eventId, resolved));
    }
    /*
     * Descripció: Crida al mètode d'IncidentRepository que guarda una incidència
     * <incident>: Incidència editada obtinguda com a paràmetre de la crida de l'API
     * Resultat: Retorna la incidència que s'ha guardat
     */
    public IncidentEntity editIncident (IncidentEntity incident) {
        Incident i = mapper.entityToModel(incident);
        if (iIncidentRepository.findById(i.getId()).isPresent()) {
            if (iUserRepository.findById(i.getUserId()).isEmpty()) {
                throw new ObjectNotFoundException("User with id " + i.getUserId() + " not found");
            }

            else if (iEventRepository.findById(i.getEventId()).isEmpty())  {
                throw new ObjectNotFoundException("Event with id " + i.getEventId() + " not found");
            }

            return mapper.modelToEntity(iIncidentRepository.save(i));
        } else {
            throw new ObjectNotFoundException("Incident with id " + i.getId() + " not found");
        }
    }

    /*
     * Descripció: Crida al mètode d'IncidentRepository que comprova si existeix una incidència amb un determinat identificador
     * <incidentId>: Identificador de la incidència
     * Resultat: Retorna cert si existeix la incidència, fals en cas contrari
     */
    public boolean existsById(Long id) { return iIncidentRepository.existsById(id); }

    /*
     * Descripció: Crida al mètode d'IncidentRepository que esborra una incidència donat el seu identificador
     */
    public void deleteIncident(Long id) { iIncidentRepository.deleteById(id); }

    /*
     * Descripció: Crida al mètode d'IncidentRepository que esborra una incidència donat l'identificador de l'esdeveniment
     */
    public void deleteIncidentsByEvent(Long eventId) {
        if (!iEventRepository.existsById(eventId)) {
            throw new ObjectNotFoundException("Event with id " + eventId + " not found");
        }

        if (!iIncidentRepository.existsByEventId(eventId)) {
            throw new ObjectNotFoundException("Incidents with event id " + eventId + " not found");
        }

        iIncidentRepository.deleteByEventId(eventId);
    }

    /*
     * Descripció: Crida al mètode d'IncidentRepository que esborra una incidència donat l'identificador de l'esdeveniment
     * Resultat: Retorna cert en cas que existeix una incidència amb l'identificador de l'esdeveniment, fals en cas contrari
     */
    public boolean existsByEventId(Long eventId)  { return iIncidentRepository.existsByEventId(eventId); }

    /*
     * Descripció: Crida al mètode d'IncidentRepository que consulta una incidència donat l'identificador propi
     * Resultat: Retorna cert en cas que existeixi una incidència amb l'identificador corresponent, fals en cas contrari
     */
    public IncidentEntity findById(Long id) {
        return mapper.modelToEntity(iIncidentRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Incident with id " + id + " not found")));
    }

    public void deleteIncidentsByEventAndUserId(Long eventId, String userId) {
        iIncidentRepository.deleteByEventIdAndUserId(eventId, userId);
    }
}
