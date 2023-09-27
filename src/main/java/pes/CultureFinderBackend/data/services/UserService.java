package pes.CultureFinderBackend.data.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.data.mappers.DataUserMapper;
import pes.CultureFinderBackend.data.models.User;
import pes.CultureFinderBackend.data.repositories.IUserRepository;
import pes.CultureFinderBackend.domain.exceptions.ObjectAlreadyExistsException;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.models.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {

    /*
     * Descripció: Instància del respositori que s'encarrega d'interactuar amb les taules dels usuaris
     */
    @Autowired
    private IUserRepository iUserRepository;

    /*
     * Descripció: Instància del servei que s'encarrrega d'interactuar amb la taula d'esdeveniments
     */
    @Autowired
    private IEventService iEventService;

    /*
     * Descripció: Instància del servei que s'encarrrega d'interactuar amb la taula de les assistències
     */
    @Autowired
    private IAssistanceService iAssistanceService;

    /*
     * Descripció: Instància del servei que s'encarrrega d'interactuar amb la taula de les llistes
     */
    @Autowired
    private ISavedEventsListService iSavedEventsListService;

    /*
     * Descripció: Instància del mapper de la capa de Dades per als usuaris
     */
    @Autowired
    private DataUserMapper dataUserMapper;

    /*
     * Descripció: Crida al mètode de UserRepository que guarda un usuari
     * <ObjectNotFoundException>: Exepció que es llença quan l'usuari té valor nul
     * <ObjectAlreadyExistsException>: Exepció que es llença quan l'usuari que es vol guardar ja es troba a la base de dades
     * Resultat: Retorna l'usuari que s'ha guardat
     */
    public UserEntity saveUser(UserEntity user) {
        if (user == null) throw new ObjectNotFoundException("User provided is null");
        if (user.getId() == null) throw new ObjectNotFoundException("User identifier provided is null");
        if (iUserRepository.existsById(user.getId()))
            throw new ObjectAlreadyExistsException("User already exists");

        return dataUserMapper.ModelToEntity(iUserRepository.save(dataUserMapper.EntityToModel(user)));
    }

    /*
     * Descripció: Crida al mètode de UserRepository que troba tots els usuaris
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna tots els usauris de la BD, amb paginació si així s'ha indicat
     */
    public Page<UserEntity> getAllUsers(Integer page, Integer size, Boolean enablePagination) {
        Pageable pageable = enablePagination ? PageRequest.of(page, size) : Pageable.unpaged();
        Page<User> p = iUserRepository.findAll(pageable);
        List<UserEntity> ret = new ArrayList<>();
        for (User u : p.getContent()) {
            ret.add(dataUserMapper.ModelToEntity(u));
        }
        return new PageImpl<>(ret, pageable, ret.size());
    }

    /*
     * Descripció: Mètode que edita un usuari i el guarda a la BD
     * <user>: Usuari editat obtingut com a paràmetre de la crida de la API
     * <ObjectNotFoundException>: Exepció que es llença quan l'usuari no té identificador
     * <ObjectNotFoundException>: Exepció que es llença quan l'usuari que es vol guardar no es troba a la BD
     * Resultat: Retorna l'usuari que s'ha guardat
     */
    public UserEntity editUser (User user) {
        if (user.getId() == null) throw new ObjectNotFoundException("User ID not provided");
        if (!iUserRepository.existsById(user.getId())) throw new ObjectNotFoundException("User " + user.getId() + " not found");
        return dataUserMapper.ModelToEntity(iUserRepository.save(user));
    }

    /*
     * Descripció: Crida al mètode de UserRepository que comprova si un usuari existeix, donat el seu identificador
     * Resultat: Retorna un booleà que indica si l'usuari existeix
     */
    public boolean existsById(String id) {
        return iUserRepository.existsById(id);
    }

    /*
     * Descripció: Obté la informació del perfil d'un usuari loguejat
     * <apiToken>: API token de l'usuari loguejat
     * <ObjectNotFoundException>: Exepció que es llença quan l'usuari no es troba loguejat al sistema
     * Resultat: Retorna l'usuari trobat
     */
    public UserEntity getProfileByAPIToken(String apiToken) {
        String userId = iUserRepository.getUserIdByAPIToken(apiToken);
        if (userId == null) throw new ObjectNotFoundException("User not found");
        Optional<User> u = iUserRepository.findById(userId);
        if (u.isEmpty()) throw new ObjectNotFoundException("User not found");
        return dataUserMapper.ModelToEntity(u.get());
    }

    /*
     * Descripció: Guarda els canvis del perfil d'un usuari loguejat
     * <apiToken>: API token de l'usuari loguejat
     * <u>: Perfil actualitzat de l'usuari en qüestió
     * Resultat: Retorna el perfil actualitzat de l'usuari
     */
    public UserEntity editProfileByAPIToken(String apiToken, UserEntity u) {
        String userId = iUserRepository.getUserIdByAPIToken(apiToken);
        User newUser = dataUserMapper.EntityToModel(u);
        newUser.setId(userId);
        return dataUserMapper.ModelToEntity(iUserRepository.save(newUser));
    }

    /*
     * Descripció: Dóna d'alta la sessió d'un usuari
     * <userID>: Identificador de l'usuari en qüestió
     * <apiToken>: API token de l'usuari que es vol loguejat
     * <ObjectAlreadyExistsException>: Exepció que es llença si l'usuari ja està loguejat
     */
    public void createSession(String userId, String apiToken, String deviceToken) {
        if (iUserRepository.existsSessionById(userId)) throw new ObjectAlreadyExistsException("User is already logged in");
        iUserRepository.createSession(userId, apiToken, deviceToken);
    }

    /*
     * Descripció: Esborra la sessió d'un usuari
     * <apiToken>: API token de l'usuari loguejat
     * <ObjectNotFoundException>: Exepció que es llença si l'usuari no està loguejat
     */
    public void logout(String apiToken) {
        if (!iUserRepository.existsSessionByToken(apiToken)) throw new ObjectNotFoundException("User is not logged in");
        iUserRepository.deleteSession(apiToken);
    }

    /*
     * Descripció: Comprova si un usuari està loguejat
     * <apiToken>: API token de l'usuari
     */
    public boolean isLoggedIn(String apiToken) {
        return iUserRepository.existsSessionByToken(apiToken);
    }

    /*
     * Descripció: Esborra un usuari loguejat de la BD
     * <apiToken>: API token de l'usuari loguejat
     * <ObjectNotFoundException>: Exepció que es llença si l'usuari no es troba a la BD
     * Resultat: Esborra la sessió i l'usuari de la BD i retorna l'identificador de l'usuari esborrat
     */
    public String deleteUserByApiToken(String apiToken) {
        String userId = iUserRepository.getUserIdByAPIToken(apiToken);
        if (userId == null) throw new ObjectNotFoundException("User not found");
        iAssistanceService.deleteAssistancesByUserId(userId);
        iEventService.deleteRatingsByUserId(userId);
        iSavedEventsListService.deleteListsByUserId(userId);
        iUserRepository.deleteSession(apiToken);
        iUserRepository.deleteById(userId);
        return userId;
    }

    /*
    * Desripció: Obté un usuari donat el seu identificador
    */
    public UserEntity findById(String userId) {
        Optional<User> u = iUserRepository.findById(userId);
        if (u.isEmpty()) throw new ObjectNotFoundException("User with id " + userId + " not found");
        return dataUserMapper.ModelToEntity(u.get());
    }

    /*
    * Descripció: Obté el deviceToken d'una sessió d'un usuari en concret
    */
    public String getDeviceToken(String userId) {
        return iUserRepository.findDeviceTokenByUserId(userId);
    }

    /*
     * Descripció: Comprova si un usuari es administrador
     */
    public boolean isAdmin(String userId) {
        return iUserRepository.isAdmin(userId);
    }

    /*
     * Descripció: Obté tots els identificadors dels usuaris loguejats al sistema
     */
    public List<String> getAllLoggedUserIds() {
        return iUserRepository.findAllLoggedUserIds();
    }
}