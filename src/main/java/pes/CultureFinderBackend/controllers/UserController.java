package pes.CultureFinderBackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pes.CultureFinderBackend.controllers.dtos.RegisteredUserDTO;
import pes.CultureFinderBackend.controllers.dtos.UserAuthenticationDTO;
import pes.CultureFinderBackend.controllers.dtos.UserDTO;
import pes.CultureFinderBackend.domain.exceptions.Error;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.services.IDomainUserService;
import pes.CultureFinderBackend.domain.services.INotificationService;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    /*
     * Descripció: Instància de la classe que fa de lligam entre la capa de Domini i la de Dades(per a usuaris)
     */
    @Autowired
    private IDomainUserService iDomainUserService;

    /*
     * Descripció: Instància del servei de domini que envia notificacions
     */
    @Autowired
    private INotificationService iNotificationService;

    /*
     * Descripció: Crida a l'API que obté tots els usuaris dins de la BD
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna l'status de la crida i els usuaris en qüestió
     */
    @Operation(summary = "Gets the profile of all the users registered in the system")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainUserService.getAllUsers(page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que obté la informació del perfil d'un usuari loguejat
     * <apiToken>: API token de l'usuari en qüestió(única per a cada usuari)
     * Resultat: Retorna l'status de la crida i la informació demanada
     */
    @Operation(summary = "Gets the profile info of a logged user")
    @GetMapping(value = "/profile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Optional<UserDTO>> getUserProfile(@RequestHeader("API-Token") String apiToken) {
        Optional<UserDTO> u = iDomainUserService.getProfileByAPIToken(apiToken);
        if (u.isEmpty()) throw new ObjectNotFoundException("User not found");
        return ResponseEntity.status(HttpStatus.OK).body(u);
    }

    /*
     * Descripció: Crida a l'API que registra un usuari nou dins del sistema
     * <user>: usuari que s'ha de registrar, amb els camps <name, password, birthDate, preferredCategories>
     * Resultat: Retorna l'status de la crida i la informació del nou usuari, amb el camp <password> a null
     */
    @Operation(summary = "Registers an user into the system")
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User successfully registered"),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "510", description = "User already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<RegisteredUserDTO> registerUser(@Valid @RequestBody RegisteredUserDTO user) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iDomainUserService.saveNewUser(user));
    }

    /*
     * Descripció: Crida a l'API que implementa el login
     * <userAuthenticationDTO>: identificador de l'usuari que vol fer el login
     * Resultat: Retorna l'status de la crida i l'API token per a aquell usuari
     */
    @Operation(summary = "Logs an user in")
    @PostMapping(value = "/authenticate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated"),
            @ApiResponse(responseCode = "510", description = "User is already logged in", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<String> findUserByCredentials(
            @RequestHeader("DeviceToken") String deviceToken,
            @RequestBody UserAuthenticationDTO userAuthenticationDTO) {
        return ResponseEntity.status(HttpStatus.OK).body(iDomainUserService.authenticate(userAuthenticationDTO, deviceToken));
    }

    /*
     * Descripció: Crida a l'API que edita els camps d'un usuari loguejat
     * <userDTO>: camps <name, birthDate, profileImage, preferredCategories> actualitzats
     * Resultat: Retorna l'status de la crida i la informació actualitzada
     */
    @Operation(summary = "Edits the profile of a logged user")
    @PutMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User edited successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<UserDTO> editProfile(@RequestHeader("API-Token") String apiToken, @RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iDomainUserService.editProfile(apiToken, userDTO));
    }

    /*
     * Descripció: Crida a l'API que esborra un usuari del sistema
     * Resultat: Retorna l'status de la crida i true si l'usuari s'ha esborrat correctament, false altrament
     */
    @Operation(summary = "Deletes a logged user")
    @DeleteMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User sucessfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Boolean> deleteUser(@RequestHeader("API-Token") String apiToken) {
        String deletedId = iDomainUserService.deleteUser(apiToken);
        return ResponseEntity.ok(!iDomainUserService.existsById(deletedId));
    }

    /*
     * Descripció: Crida a l'API que implementa el log out
     * Resultat: Retorna l'status de la crida i true si l'usuari ja no té una sessió iniciada, false altrament
     */
    @Operation(summary = "Logs out an user")
    @PostMapping(value = "/logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logged out successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Boolean> logout(@RequestHeader("API-Token") String apiToken) {
        iDomainUserService.logout(apiToken);
        return ResponseEntity.ok(!iDomainUserService.isLoggedIn(apiToken));
    }



    /*
     * Descripció: Crida a l'API que envia totes les notificacions als usuaris
     * Resultat: Retorna l'status de la crida i true
     */
    @Operation(summary = "Sends all notifications to a user")
    @GetMapping(value = "/notifications")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Error sending all notifications", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Boolean> sendNotifications(@RequestHeader("API-Token") String apiToken) {
        iNotificationService.sendAllNotifications(apiToken);
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }
}
