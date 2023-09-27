package pes.CultureFinderBackend.domain.services;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pes.CultureFinderBackend.controllers.dtos.IncidentDTO;
import java.util.List;
import org.springframework.data.domain.Page;
import pes.CultureFinderBackend.controllers.dtos.SubmitIncidentDTO;
import pes.CultureFinderBackend.domain.models.IncidentEntity;

public interface IDomainIncidentService {
    List<IncidentDTO> findByUserAndResolved(String apiToken, String userId, Boolean resolved);

    List<IncidentDTO> findByEventAndResolved(String apiToken, Long eventId, Boolean resolved);

    Page<IncidentDTO> getAllIncidents(String apiToken, Integer page, Integer size, Boolean enablePagination);

    static Page<IncidentEntity> changeListToPageIncidentEntity(List<IncidentEntity> incidents, Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        return new PageImpl<>(incidents, pageable, incidents.size());
    }

    static Page<IncidentDTO> changeListToPageIncidentDto(List<IncidentDTO> incidents, Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        return new PageImpl<>(incidents, pageable, incidents.size());
    }

    void deleteIncident(String apiToken, Long id);

    boolean existsById(Long id);

    void deleteIncidentsByEvent(String apiToken, Long eventId);

    IncidentDTO saveIncident(String apiToken, SubmitIncidentDTO incident) throws Exception;

    IncidentDTO editIncident(String apiToken, IncidentDTO incident);

    boolean existsByEventId(Long eventId);
}
