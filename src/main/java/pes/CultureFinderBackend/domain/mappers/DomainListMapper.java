package pes.CultureFinderBackend.domain.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import pes.CultureFinderBackend.controllers.dtos.ListDTO;
import pes.CultureFinderBackend.domain.models.ListEntity;

@Mapper(componentModel = "spring")
public interface DomainListMapper {
    @Mapping(target = "firstImages", ignore = true)
    @Mapping(target = "NEvents", ignore = true)
    ListDTO entityToDTO(ListEntity list);

    ListEntity DTOToEntity(ListDTO list);

    @AfterMapping
    default void calculateNEvents(ListEntity list, @MappingTarget ListDTO dto) {
        dto.setNEvents(list.getEvents().size());
    }
}
