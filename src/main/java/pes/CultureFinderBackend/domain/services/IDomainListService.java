package pes.CultureFinderBackend.domain.services;

import org.springframework.data.domain.Page;
import pes.CultureFinderBackend.controllers.dtos.EventDTO;
import pes.CultureFinderBackend.controllers.dtos.ListDTO;

public interface IDomainListService {
    Page<ListDTO> getAllUserLists(String apiToken, Integer page, Integer size, Boolean enablePagination);

    Page<EventDTO> getAllEventsFromList(String apiToken, Long listId, Integer page, Integer size, Boolean enablePagination);

    ListDTO createEmptyList(String apiToken, String name);

    ListDTO editList(String apiToken, Long listId, String name, String description);

    boolean existsById(Long listId);

    void deleteList(String apiToken, Long listId);

    ListDTO addEvent(String apiToken, Long listId, Long eventId);

    ListDTO removeEvent(String apiToken, Long listId, Long eventId);

    boolean existsEventInList(Long listId, Long eventId);
}
