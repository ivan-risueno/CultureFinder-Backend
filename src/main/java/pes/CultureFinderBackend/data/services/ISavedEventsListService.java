package pes.CultureFinderBackend.data.services;

import org.springframework.data.domain.Page;
import pes.CultureFinderBackend.domain.models.EventEntity;
import pes.CultureFinderBackend.domain.models.ListEntity;

public interface ISavedEventsListService {
    ListEntity saveList(ListEntity list);
    Page<ListEntity> getAllUserLists(String userId, Integer page, Integer size, Boolean enablePagination);
    Page<EventEntity> getAllEventsFromList(Long listId, Integer page, Integer size, Boolean enablePagination);
    ListEntity editList (ListEntity list);
    boolean existsById(Long id);
    void deleteList(Long id);
    void deleteListsByUserId(String userId);
    String getOwner(Long listId);
    ListEntity getListById(Long listId);
    boolean existsEventInList(Long listId, Long eventId);
}
