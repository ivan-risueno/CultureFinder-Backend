package pes.CultureFinderBackend.domain.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.controllers.dtos.IncidentDTO;
import pes.CultureFinderBackend.controllers.dtos.SubmitIncidentDTO;
import pes.CultureFinderBackend.data.services.IIncidentService;
import pes.CultureFinderBackend.data.services.IUserService;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.mappers.DomainIncidentMapper;
import pes.CultureFinderBackend.domain.models.IncidentEntity;
import pes.CultureFinderBackend.domain.models.UserEntity;

import java.util.List;

@Service
public class DomainIncidentService implements IDomainIncidentService {
    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori d'incidents.
     */
    @Autowired
    private IIncidentService iIncidentService;

    /*
     * Descripció: Instància del servei que s'encarrega d'interactuar amb el repositori d'users.
     */
    @Autowired
    private IUserService iUserService;

    /*
     * Descripció: Mapper de domini que s'encarrega de fer la conversió entre DTO i Entitat d'Incident.
     */
    @Autowired
    private DomainIncidentMapper mapper;

    /*
     * Descripció: Crida al mètode d'IncidentService que filtra les incidències per usuari i status mapejant el DTO a Entitat.
     * Resultat: Retorna les incidències que compleixen el filtratge per id d'usuari i status.
     */
    public List<IncidentDTO> findByUserAndResolved(String apiToken, String userId, Boolean resolved) {
        UserEntity uE = iUserService.getProfileByAPIToken(apiToken);
        List<IncidentEntity> incidentEntities;
        if (!uE.getIsAdmin() && !uE.getId().equals(userId)) {
                throw new ObjectNotFoundException("You are not allowed to see these incidents");
        } else {
            incidentEntities = iIncidentService.findByUserAndResolved(userId, resolved);
        }

        return mapper.listEntitiesToDTO(incidentEntities);
    }

    /*
     * Descripció: Crida al mètode d'IncidentService que filtra les incidències per id d'esdeveniment i status mapejant el DTO a Entitat.
     * Resultat: Retorna les incidències que compleixen el filtratge per id d'esdeveniments i status.
     */
    public List<IncidentDTO> findByEventAndResolved(String apiToken, Long eventId, Boolean resolved) {
        UserEntity uE = iUserService.getProfileByAPIToken(apiToken);
        List<IncidentEntity> incidentEntities;
        if (uE.getIsAdmin()) {
            incidentEntities = iIncidentService.findByEventAndResolved(eventId, resolved);
        } else {
            incidentEntities = iIncidentService.findByEventAndUserAndResolved(uE.getId(), eventId, resolved);
        }

        return mapper.listEntitiesToDTO(incidentEntities);
    }

    /*
     * Descripció: Crida al mètode d'IncidentService que troba totes les incidències. Es fa la conversió per cada incidència
     * d'IncidentEntity a IncidentDTO. Si s'ha indicat paginació, es fa la conversió de la llista de DTO a pàgina.
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna totes les incidències de la BD, amb paginació si així s'ha indicat.
     */
    public Page<IncidentDTO> getAllIncidents(String apiToken, Integer page, Integer size, Boolean enablePagination) {
        UserEntity uE = iUserService.getProfileByAPIToken(apiToken);
        List<IncidentEntity> incidentEntities;

        if (uE.getIsAdmin()) {
            incidentEntities = IDomainIncidentService.changeListToPageIncidentEntity(iIncidentService.getAllIncidents(page, size, enablePagination).getContent(), page, size, enablePagination).getContent();
        } else {
            incidentEntities = IDomainIncidentService.changeListToPageIncidentEntity(iIncidentService.getIncidentsByUser(uE.getId(), page, size, enablePagination).getContent(), page, size, enablePagination).getContent();
        }

        return IDomainIncidentService.changeListToPageIncidentDto(mapper.listEntitiesToDTO(incidentEntities), page, size, enablePagination);
    }

    /*
     * Descripció: Crida al mètode d'IncidentService que esborra una incidència.
     */
    public void deleteIncident(String apiToken, Long id) {
        UserEntity uE = iUserService.getProfileByAPIToken(apiToken);

        if (!uE.getIsAdmin()) {
            IncidentEntity incidentEntity = iIncidentService.findById(id);
            System.out.print(incidentEntity.getUserId() + " " + uE.getId());
            if (incidentEntity.getUserId().equals(uE.getId())) {
                iIncidentService.deleteIncident(id);
            }
        } else {
            iIncidentService.deleteIncident(id);
        }

    }

    /*
     * Descripció: Crida al mètode d'IncidentService que comprova si existeix una incidència donat un id.
     * Resultat: Retorna cert si existeix la incidència, fals en cas contrari.
     */
    public boolean existsById(Long id) {
        return iIncidentService.existsById(id);
    }

    /*
     * Descripció: Crida al mètode d'IncidentService que esborra incidències donat un identificador d'esdeveniment.
     */
    public void deleteIncidentsByEvent(String apiToken, Long eventId) {
        UserEntity uE = iUserService.getProfileByAPIToken(apiToken);
        if(uE.getIsAdmin()) {
            iIncidentService.deleteIncidentsByEvent(eventId);
        } else {
            iIncidentService.deleteIncidentsByEventAndUserId(eventId, uE.getId());
        }
    }

    /*
     * Descripció: Crida al mètode d'IncidentService que guarda una incidència a la BD mapejant el DTO a Entitat.
     * Resultat: Retorna l'incidència que s'ha guardat en format DTO.
     */
    public IncidentDTO saveIncident(String apiToken, SubmitIncidentDTO incident) throws Exception {
        UserEntity uE = iUserService.getProfileByAPIToken(apiToken);
        if (incident == null) {
            throw new ObjectNotFoundException("Incident not found");
        }


        return mapper.entityToDTO(iIncidentService.saveIncident(mapper.submitDtoToEntity(incident, uE.getId())));
    }

    /*
     * Descripció: Crida a l'IncidentService que modifica una incidència donada. Es fa la conversió d'IncidentDTO a IncidentEntity.
     * Resultat: Retorna l'esdeveniment modificat mapejat a DTO.
     */
    public IncidentDTO editIncident(String apiToken, IncidentDTO incident) {
        UserEntity uE = iUserService.getProfileByAPIToken(apiToken);
        if (incident == null) {
            throw new ObjectNotFoundException("Incident not found");
        }

        if (!uE.getIsAdmin() && !uE.getId().equals(incident.getUserId())) {
            throw new ObjectNotFoundException("You are not allowed to edit incidents for other users");
        }

        return mapper.entityToDTO(iIncidentService.editIncident(mapper.dtoToEntity(incident)));
    }

    /*
     * Descripció: Crida al mètode d'IncidentService que comprova si existeix una incidència donat un id d'esdeveniment.
     * Resultat: Retorna cert si existeix la incidència, fals en cas contrari.
     */
    public boolean existsByEventId(Long eventId) {
        return iIncidentService.existsByEventId(eventId);
    }
}
