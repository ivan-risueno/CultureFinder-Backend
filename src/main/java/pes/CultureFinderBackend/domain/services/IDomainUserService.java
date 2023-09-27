package pes.CultureFinderBackend.domain.services;

import org.springframework.data.domain.Page;
import pes.CultureFinderBackend.controllers.dtos.RegisteredUserDTO;
import pes.CultureFinderBackend.controllers.dtos.UserAuthenticationDTO;
import pes.CultureFinderBackend.controllers.dtos.UserDTO;

import java.util.List;
import java.util.Optional;

public interface IDomainUserService {
    String authenticate(UserAuthenticationDTO userDTO, String deviceToken);

    RegisteredUserDTO saveNewUser(RegisteredUserDTO userDTO);

    Page<UserDTO> getAllUsers(Integer page, Integer size, Boolean enablePagination);

    String deleteUser(String apiToken);

    boolean existsById(String id);

    Optional<UserDTO> getProfileByAPIToken(String apiToken);

    UserDTO editProfile(String apiToken, UserDTO userDTO);

    void logout(String apiToken);

    boolean isLoggedIn(String apiToken);

    String getDeviceToken(String userId);

    boolean isAdmin(String userId);

    List<String> getAllLoggedUserIds();
}
