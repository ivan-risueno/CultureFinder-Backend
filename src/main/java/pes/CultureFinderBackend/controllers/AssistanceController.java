package pes.CultureFinderBackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pes.CultureFinderBackend.controllers.dtos.AssistanceDTO;
import pes.CultureFinderBackend.controllers.dtos.EventDTO;
import pes.CultureFinderBackend.data.models.Assistance;
import pes.CultureFinderBackend.domain.exceptions.Error;
import pes.CultureFinderBackend.domain.services.IDomainAssistanceService;

@RestController
@RequestMapping("/assistances")
public class AssistanceController {

    /*
     * Descripció: Instància del servei de domini de les Assistències
     */
    @Autowired
    private IDomainAssistanceService iDomainAssistanceService;

    /*
     * Descripció: Crida a la API que crea una assistència i la guarda a la taula assistances
     * Resultat: Retorna l'status de la creació de l'assistència
     */
    @Operation(summary = "Saves a concrete assistance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Assistance successfully saved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AssistanceDTO.class))),
            @ApiResponse(responseCode = "404", description = "Assistance not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "510", description = "Assistance already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @PostMapping
    public ResponseEntity<AssistanceDTO> saveAssistance(@RequestHeader("API-Token") String apiToken, @RequestParam Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iDomainAssistanceService.saveNewAssistance(apiToken, eventId));
    }

    /*
     * Descripció: Crida a la API que comprova si un usuari assisteix a un esdeveniment
     * <apiToken>: API token de l'usuari en qüestió
     * <eventId>: Identificador de l'esdeveniment
     * Resultat: Retorna l'status de la crida i true si l'usuari assisteix, false altrament
     */
    @Operation(summary = "Checks if an user will assist to a specific event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information successfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @GetMapping
    public ResponseEntity<Boolean> checkIfExistentAssistance (
            @RequestHeader("API-Token") String apiToken,
            @RequestParam Long eventId) {
        return ResponseEntity.ok(iDomainAssistanceService.existsById(apiToken, eventId));
    }

    /*
     * Descripció: Crida a la API que obté tots els esdeveniments als quals assistirà un usuari
     * <apiToken>: API token de l'usuari en qüestió
     * Resultat: Retorna els esdeveniments als que l'usuari assistirà, amb paginació opcional
     */
    @Operation(summary = "Gets all user-specific assistances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information successfully retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @GetMapping(value = "/events")
    public ResponseEntity<Page<EventDTO>> getAllUserAssistances (
            @RequestHeader("API-Token") String apiToken,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainAssistanceService.getAllUserAssistances(apiToken, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que esborra una assistència d'un usuari per a un esdeveniment
     * Resultat: Retorna l'status de la crida
     */
    @Operation(summary = "Deletes a concrete assistance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Assistance successfully deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Assistance.class))),
            @ApiResponse(responseCode = "404", description = "Assistance not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @DeleteMapping
    public ResponseEntity<Boolean> deleteAssistance(@RequestHeader("API-Token") String apiToken, @RequestParam Long eventId) {
        iDomainAssistanceService.deleteAssistance(apiToken, eventId);
        return ResponseEntity.ok(!iDomainAssistanceService.existsById(apiToken, eventId));
    }
}