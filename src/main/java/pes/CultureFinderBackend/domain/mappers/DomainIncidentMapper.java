package pes.CultureFinderBackend.domain.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pes.CultureFinderBackend.controllers.dtos.IncidentDTO;
import pes.CultureFinderBackend.controllers.dtos.SubmitIncidentDTO;
import pes.CultureFinderBackend.domain.models.IncidentEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DomainIncidentMapper {

    IncidentDTO entityToDTO(IncidentEntity incident);

    IncidentEntity dtoToEntity(IncidentDTO incidentDTO);

    @InheritInverseConfiguration
    List<IncidentDTO> listEntitiesToDTO(List<IncidentEntity> incidentEntity);

    @Mapping(target = "userId", source = "id")
    IncidentEntity submitDtoToEntity(SubmitIncidentDTO incident, String id);
}
