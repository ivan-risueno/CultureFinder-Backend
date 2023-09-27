package pes.CultureFinderBackend.data.mappers;

import org.mapstruct.Mapper;
import pes.CultureFinderBackend.data.models.Assistance;
import pes.CultureFinderBackend.data.models.AssistanceId;
import pes.CultureFinderBackend.domain.models.AssistanceEntity;

@Mapper(componentModel = "spring")
public interface DataAssistanceMapper {
    Assistance entityToModel(AssistanceEntity entity);

    AssistanceEntity modelToEntity(Assistance model);

    AssistanceId entityToId(AssistanceEntity entity);
}
