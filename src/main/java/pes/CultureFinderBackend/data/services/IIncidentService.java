package pes.CultureFinderBackend.data.services;

import org.springframework.data.domain.Page;
import pes.CultureFinderBackend.domain.models.IncidentEntity;

import java.util.List;

public interface IIncidentService {
    IncidentEntity saveIncident(IncidentEntity incident) throws Exception;

    void deleteIncident(Long id);

    IncidentEntity editIncident (IncidentEntity incident);

    boolean existsById(Long id);

    void deleteIncidentsByEvent(Long eventId);

    List<IncidentEntity> findByUserAndResolved(String userId, Boolean resolved);

    List<IncidentEntity> findByEventAndResolved(Long eventId, Boolean resolved);

    List<IncidentEntity> findByEventAndUserAndResolved(String userId, Long eventId, Boolean resolved);

    Page<IncidentEntity> getAllIncidents(Integer page, Integer size, Boolean enablePagination);

    boolean existsByEventId(Long eventId);

    Page<IncidentEntity> getIncidentsByUser(String userId, Integer page, Integer size, Boolean enablePagination);

    IncidentEntity findById(Long id);

    void deleteIncidentsByEventAndUserId(Long eventId, String userId);
}
