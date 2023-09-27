package pes.CultureFinderBackend.data.mappers;

import org.mapstruct.Mapper;
import pes.CultureFinderBackend.data.models.SavedEventsList;
import pes.CultureFinderBackend.domain.models.ListEntity;

@Mapper(componentModel = "spring")
public interface DataListMapper {
    ListEntity modelToEntity(SavedEventsList list);
    SavedEventsList entityToModel(ListEntity list);
}
