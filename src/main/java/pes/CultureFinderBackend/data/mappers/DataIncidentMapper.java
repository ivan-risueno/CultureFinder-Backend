package pes.CultureFinderBackend.data.mappers;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import pes.CultureFinderBackend.data.models.Incident;
import pes.CultureFinderBackend.domain.models.IncidentEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DataIncidentMapper {
    @InheritInverseConfiguration
    List<Incident> listEntitiesToModels(List<IncidentEntity> incidentEntity);

    List<IncidentEntity> listModelsToEntities(List<Incident> incident);

    @InheritInverseConfiguration
    Incident entityToModel(IncidentEntity incidentEntity);

    IncidentEntity modelToEntity(Incident incident);
}
