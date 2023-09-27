package pes.CultureFinderBackend.data.services;

import org.springframework.data.domain.Page;
import pes.CultureFinderBackend.domain.models.UserEntity;

import java.util.List;

public interface IUserService {
    UserEntity saveUser(UserEntity user);

    Page<UserEntity> getAllUsers(Integer page, Integer size, Boolean enablePagination);

    boolean existsById(String id);

    UserEntity getProfileByAPIToken(String apiToken);

    UserEntity editProfileByAPIToken(String apiToken, UserEntity u);

    void createSession(String userId, String apiToken, String deviceToken);

    void logout(String apiToken);

    boolean isLoggedIn(String apiToken);

    String deleteUserByApiToken(String apiToken);

    UserEntity findById(String userId);

    String getDeviceToken(String userId);

    boolean isAdmin(String userId);

    List<String> getAllLoggedUserIds();
}
