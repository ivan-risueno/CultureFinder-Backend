package pes.CultureFinderBackend.domain.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import pes.CultureFinderBackend.controllers.dtos.EventDTO;
import pes.CultureFinderBackend.domain.models.EventEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class DomainEventMapper {

    public abstract EventDTO entityToDTO(EventEntity event);

    public abstract EventEntity dtoToEntity(EventDTO eventDTO);

    @InheritInverseConfiguration
    public abstract List<EventDTO> listEntitiesToDTO(List<EventEntity> eventEntity);

    public abstract List<EventEntity> listDTOToEntities(List<EventDTO> eventDTO);
}
