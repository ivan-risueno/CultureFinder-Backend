package pes.CultureFinderBackend.domain.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pes.CultureFinderBackend.controllers.dtos.RegisteredUserDTO;
import pes.CultureFinderBackend.controllers.dtos.UserDTO;
import pes.CultureFinderBackend.domain.models.UserEntity;
import pes.CultureFinderBackend.domain.services.IDomainUserService;

@Mapper(imports = IDomainUserService.class)
public interface DomainUserMapper {

    /*
     * Descripció: Mappeja una instància de RegisteredUserDTO a UserEntity
     */
    @Mapping(target = "profileImage", ignore = true)
    UserEntity registeredUserDTOToEntity(RegisteredUserDTO dto);

    /*
     * Descripció: Mappeja una instància de UserEntity a RegisteredUserDTO
     */
    RegisteredUserDTO entityToRegisteredUserDTO(UserEntity entity);

    /*
     * Descripció: Mappeja una instància de UserDTO a UserEntity
     */
    @Mapping(target = "id", ignore = true)
    UserEntity userDTOToEntity(UserDTO dto);

    /*
     * Descripció: Mappeja una instància de UserEntity a UserDTO
     */
    UserDTO entityToUserDTO(UserEntity entity);
}
