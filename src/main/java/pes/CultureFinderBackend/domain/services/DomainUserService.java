package pes.CultureFinderBackend.domain.services;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.controllers.dtos.RegisteredUserDTO;
import pes.CultureFinderBackend.controllers.dtos.UserAuthenticationDTO;
import pes.CultureFinderBackend.controllers.dtos.UserDTO;
import pes.CultureFinderBackend.data.services.ISavedEventsListService;
import pes.CultureFinderBackend.data.services.IUserService;
import pes.CultureFinderBackend.domain.businesslogic.UserLogic;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.mappers.DomainUserMapper;
import pes.CultureFinderBackend.domain.models.ListEntity;
import pes.CultureFinderBackend.domain.models.UserEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class DomainUserService implements IDomainUserService {

    /*
     * Descripció: Instància de la interfície que fa d'intermediària entre la capa de Domini i la de Dades
     */
    @Autowired
    private IUserService iUserService;

    /*
     * Descripció: Instància de la interfície que fa d'intermediària entre la capa de Domini i la de Dades
     */
    @Autowired
    private ISavedEventsListService iSavedEventsListService;

    /*
     * Descripció: Instància de la interfície que treballa amb les API tokens
     */
    @Autowired
    private ISecurityService iSecurityService;

    /*
     * Descripció: Instància del mapper de domini per als usuaris
     */
    private final DomainUserMapper domainUserMapper = Mappers.getMapper(DomainUserMapper.class);


    /*
     * Descripció: Dóna d'alta la sessió d'un usuari
     * <userAuthenticationDTO>: identificador de l'usuari que es vol loguejar
     * Resultat: Retorna l'API token de l'usuari loguejat
     */
    public String authenticate(UserAuthenticationDTO userDTO, String deviceToken) {
        if (!iUserService.existsById(userDTO.getUserId())) throw new ObjectNotFoundException("User " + userDTO.getUserId() + " not found");
        String key = UserLogic.getApiToken(userDTO);
        iUserService.createSession(userDTO.getUserId(), key, deviceToken);
        return key;
    }

    /*
     * Descripció: Guarda un usuari nou a la BD
     * Resultat: Retorna l'usuari guardat
     */
    public RegisteredUserDTO saveNewUser(RegisteredUserDTO userDTO) {
        UserEntity newUser = domainUserMapper.registeredUserDTOToEntity(userDTO);
        newUser.setProfileImage("");
        RegisteredUserDTO ret = domainUserMapper.entityToRegisteredUserDTO(iUserService.saveUser(newUser));
        ListEntity l = new ListEntity();
        l.setName("Favorits");
        l.setDescription("Llista dels esdeveniments que més m'interessen!");
        l.setUserId(userDTO.getId());
        l.setEvents(new HashSet<>());
        iSavedEventsListService.saveList(l);
        return ret;
    }

    /*
     * Descripció: Obté tots els usuaris dins de la BD
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna els usuaris en qüestió
     */
    public Page<UserDTO> getAllUsers(Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        Page<UserEntity> p = iUserService.getAllUsers(page, size, enablePagination);
        List<UserDTO> ret = new ArrayList<>();
        for (UserEntity user : p.getContent()) {
            ret.add(domainUserMapper.entityToUserDTO(user));
        }
        return new PageImpl<>(ret, pageable, ret.size());
    }

    /*
     * Descripció: Esborra un usuari mitjançant la seva API token
     * Resultat: Retorna l'identificador de l'usuari que s'ha esborrat
     */
    public String deleteUser(String apiToken) {
        return iUserService.deleteUserByApiToken(apiToken);
    }

    /*
     * Descripció: Comprova si un usuari existeix donat el seu identificador
     */
    public boolean existsById(String id) {
        return iUserService.existsById(id);
    }

    /*
     * Descripció: Obté el perfil d'un usuari loguejat
     * <apiToken>: API Token de l'usuari en qüestió
     */
    public Optional<UserDTO> getProfileByAPIToken(String apiToken) {
        String userId = (String) iSecurityService.buildMappedObjectFromApiToken(apiToken).get("userId");
        return Optional.ofNullable(domainUserMapper.entityToUserDTO(iUserService.findById(userId)));
    }

    /*
     * Descripció: Edita el perfil d'un usuari loguejat
     * <apiToken>: API Token de l'usuari en qüestió
     * <userDTO>: Perfil actualitzat de l'usuari
     */
    public UserDTO editProfile(String apiToken, UserDTO userDTO) {
        return domainUserMapper.entityToUserDTO(iUserService.editProfileByAPIToken(apiToken, domainUserMapper.userDTOToEntity(userDTO)));
    }

    /*
     * Descripció: Tanca la sessió d'un usuari loguejat
     * <apiToken>: API Token de l'usuari en qüestió
     */
    public void logout(String apiToken) {
        iUserService.logout(apiToken);
    }

    /*
     * Descripció: Comprova si un usuari està loguejat
     * <apiToken>: API Token de l'usuari en qüestió
     */
    public boolean isLoggedIn(String apiToken) {
        return iUserService.isLoggedIn(apiToken);
    }

    /*
    * Descripció: Obté el device token de la sessió d'un usuari en concret
    */
    public String getDeviceToken(String userId) {
        return iUserService.getDeviceToken(userId);
    }

    /*
    * Descripció: Comprova si un usuari es administrador
    */
    public boolean isAdmin(String userId) {
        return iUserService.isAdmin(userId);
    }

    /*
    * Descripció: Obté tots els identificadors dels usuaris loguejats al sistema
    */
    public List<String> getAllLoggedUserIds() {
        return iUserService.getAllLoggedUserIds();
    }
}
