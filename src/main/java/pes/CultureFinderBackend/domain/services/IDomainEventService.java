package pes.CultureFinderBackend.domain.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pes.CultureFinderBackend.controllers.dtos.EventDTO;
import pes.CultureFinderBackend.domain.models.EventEntity;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IDomainEventService {

    Page<EventDTO> getAllEvents(Integer page, Integer size, Boolean enablePagination);

    static Page<EventDTO> changeListToPageEventDTO(List<EventDTO> events, Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        return new PageImpl<>(events, pageable, events.size());
    }

    static Page<EventEntity> changeListToPageEventEntity(List<EventEntity> events, Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        return new PageImpl<>(events, pageable, events.size());
    }

    EventDTO saveEvent(EventDTO event);

    void deleteEvent(Long id);

    boolean existsById(Long id);

    Optional<EventDTO> findById(Long id);

    Page<EventDTO> findAllByDate(LocalDate dataIni, LocalDate dataFi, Integer page, Integer size, Boolean enablePagination);

    Page<EventDTO> findAllByDenominacio(String denominacio, Integer page, Integer size, Boolean enablePagination);

    Page<EventDTO> findAllByPreu(String preu, Integer page, Integer size, Boolean enablePagination);

    List<String> findAllCategories();

    Page<EventDTO> findAllByFilters(String ambit, String categoria, String altres, LocalDate dataIni, LocalDate dataFi, String preu, String denominacio, String comarcaMunicipi, String descripcio, Float radi, Float latitud, Float longitud, Integer page, Integer size, Boolean enablePagination);

    Page<EventDTO> findAllByAmbit(String ambit, Integer page, Integer size, Boolean enablePagination);

    Page<EventDTO> findAllByCategoria(String categoria, Integer page, Integer size, Boolean enablePagination);

    Page<EventDTO> findAllByAltres(String altres, Integer page, Integer size, Boolean enablePagination);

    Page<EventDTO> findAllByComarcaMunicipi(String comarcaMunicipi, Integer page, Integer size, Boolean enablePagination);

    Page<EventDTO> findAllByDescripcio(String descripcio, Integer page, Integer size, Boolean enablePagination);

    EventDTO editEvent(EventDTO event);

    EventDTO rateEvent(String apiToken, Long eventId, Float score);

    void unrateEvent(String apiToken, Long eventId);

    boolean eventAlreadyRated(String apiToken, Long eventId);

    Page<EventDTO> getSuggestedEvents(String apiToken, Integer page, Integer size, Boolean enablePagination);

    Page<EventDTO> getPopularEvents(Integer page, Integer size, Boolean enablePagination);

    void updateDatabase() throws IOException, URISyntaxException;

    void POSTEventFromObject(EventEntity e, URI uri);

    Float getRating(String apiToken, Long eventId);
}
