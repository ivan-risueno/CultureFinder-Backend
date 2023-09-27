package pes.CultureFinderBackend.data.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pes.CultureFinderBackend.domain.models.EventEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public interface IEventService {
    EventEntity rateEvent(Long eventId, String userId, Float score);

    void unrateEvent(Long eventId, String userId);

    void deleteRatingsByUserId(String userId);

    boolean userAlreadyRatedEvent(Long eventId, String userId);

    EventEntity editEvent (EventEntity event);

    Page<EventEntity> getAllEvents(Integer page, Integer size, Boolean enablePagination);

    EventEntity saveEvent(EventEntity e);

    void deleteEvent(Long id);

    Optional<EventEntity> findById(Long id);

    Page<EventEntity>  findAllByAltres(String altres, Integer page, Integer size, Boolean enablePagination);

    Page<EventEntity> findAllByDenominacio(String denominacio, Integer page, Integer size, Boolean enablePagination);

    Page<EventEntity> findAllByPreu(String preu, Integer page, Integer size, Boolean enablePagination);

    Page<EventEntity> findAllByDate(LocalDate dataIni, LocalDate dataFi, Integer page, Integer size, Boolean enablePagination);

    Page<EventEntity> findAllByComarcaMunicipi(String comarcaMunicipi, Integer page, Integer size, Boolean enablePagination);

    Page<EventEntity> findAllByDescripcio(String descripcio, Integer page, Integer size, Boolean enablePagination);

    Page<EventEntity> findAllByFilters(String ambit, String categoria, String altres, LocalDate dataIni, LocalDate dataFi,
                                       String preu, String denominacio, String comarcaMunicipi, String descripcio, Float radi, Float latitud, Float longitud,
                                       Integer page, Integer size, Boolean enablePagination);

    Page<EventEntity> findAllByAmbit(String ambit, Integer page, Integer size, Boolean enablePagination);

    Page<EventEntity> findAllByCategoria(String categoria, Integer page, Integer size, Boolean enablePagination);

    Page<EventEntity> getSuggestedEvents(String[] categories, Pageable pageable);

    Page<EventEntity> getPopularEvents(Pageable pageable);

    List<String> findAllCategories();

    boolean existsById(Long id);

    boolean existsEventInOurDataBase(LocalDate dataInici, LocalDate dataFi, String denominacio, String descripcio);

    Optional<Float> getRating(String userId, Long eventId);
}
