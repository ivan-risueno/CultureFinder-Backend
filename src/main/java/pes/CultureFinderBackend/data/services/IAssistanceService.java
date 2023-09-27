package pes.CultureFinderBackend.data.services;

import org.springframework.data.domain.Page;
import pes.CultureFinderBackend.domain.models.AssistanceEntity;

import java.util.List;

public interface IAssistanceService {
    AssistanceEntity saveAssistance(String apiToken, Long eventId);

    Page<AssistanceEntity> getAllAssistances(Integer page, Integer size, Boolean enablePagination);

    boolean existsById(String apiToken, Long eventId);

    void deleteAssistancesByEvent(Long id);

    void deleteAssistancesByUserId(String userId);

    void deleteAssistance(String apiToken, Long eventId);

    List<Long> getPopularEventIds();

    List<Long> getAllByUserId(String userId);
}
