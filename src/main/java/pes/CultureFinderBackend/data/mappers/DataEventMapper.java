package pes.CultureFinderBackend.data.mappers;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import pes.CultureFinderBackend.data.models.Event;
import pes.CultureFinderBackend.data.repositories.IAssistanceRepository;
import pes.CultureFinderBackend.data.repositories.IEventRepository;
import pes.CultureFinderBackend.domain.models.EventEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class DataEventMapper {

    @Autowired
    private IEventRepository iEventRepository;  // No podem fer un autowired de eventService perquè es crea una dependència cíclica,
                                                // hem d'utilitzar el repositori directament

    @Autowired
    private IAssistanceRepository iAssistanceRepository;

    @InheritInverseConfiguration
    public abstract List<Event> listEntitiesToModels(List<EventEntity> eventEntity);

    public abstract List<EventEntity> listModelsToEntities(List<Event> event);

    @Mapping(target = "scores", ignore = true)
    public abstract Event entityToModel(EventEntity eventEntity);

    @Mapping(target = "numberOfAssistants", ignore = true)
    @Mapping(target = "score", ignore = true)
    public abstract EventEntity modelToEntity(Event event);

    @AfterMapping
    void calculateScoreAndNAssistants(Event event, @MappingTarget EventEntity entity) {
        Float score = 0.f;
        for (String user : event.getScores().keySet()) {
            score += event.getScores().get(user);
        }
        if (score != 0.f) score /= event.getScores().size();
        entity.setScore(score);

        entity.setNumberOfAssistants(iAssistanceRepository.getNAssistantsByEventId(entity.getId()));
    }

    @AfterMapping
    void mapScores(@MappingTarget Event event, EventEntity entity) {
        Map<String, Float> scores = new HashMap<>();
        List<String> users = iEventRepository.findRatingUsers(entity.getId());
        for (String user : users) {
            scores.put(user, iEventRepository.getRating(entity.getId(), user));
        }
        event.setScores(scores);
    }
}
