package pes.CultureFinderBackend.domain.mappers;

import org.mapstruct.Mapper;
import pes.CultureFinderBackend.controllers.dtos.AssistanceDTO;
import pes.CultureFinderBackend.domain.models.AssistanceEntity;

@Mapper(componentModel = "spring")
public interface DomainAssistanceMapper {
    AssistanceDTO entityToDTO(AssistanceEntity entity);

    AssistanceEntity DTOToEntity(AssistanceDTO dto);
}

