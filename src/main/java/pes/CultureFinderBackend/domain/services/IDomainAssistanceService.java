package pes.CultureFinderBackend.domain.services;

import org.springframework.data.domain.Page;
import pes.CultureFinderBackend.controllers.dtos.AssistanceDTO;
import pes.CultureFinderBackend.controllers.dtos.EventDTO;

public interface IDomainAssistanceService {

    AssistanceDTO saveNewAssistance(String apiToken, Long eventId);

    Page<AssistanceDTO> getAllAssistances(Integer page, Integer size, Boolean enablePagination);

    void deleteAssistance(String apiToken, Long eventId);

    boolean existsById(String apiToken, Long eventId);

    Page<EventDTO> getAllUserAssistances(String apiToken, Integer page, Integer size, Boolean enablePagination);
}
