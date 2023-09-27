package pes.CultureFinderBackend.data.mappers;

import org.mapstruct.Mapper;
import pes.CultureFinderBackend.data.models.User;
import pes.CultureFinderBackend.domain.models.UserEntity;

@Mapper(componentModel = "spring")
public interface DataUserMapper {

    /*
     * Descripció: Mappeja una instància de model d'usuari a un usuari entitat de domini
     */
    UserEntity ModelToEntity(User model);

    /*
     * Descripció: Mappeja una instància d'usuari entitat de domini a model d'usuari
     */
    User EntityToModel(UserEntity entity);
}
