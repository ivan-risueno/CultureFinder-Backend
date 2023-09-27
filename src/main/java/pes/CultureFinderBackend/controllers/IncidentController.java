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
import pes.CultureFinderBackend.controllers.dtos.IncidentDTO;
import pes.CultureFinderBackend.controllers.dtos.SubmitIncidentDTO;
import pes.CultureFinderBackend.data.models.Incident;
import pes.CultureFinderBackend.domain.services.IDomainIncidentService;

import java.util.List;

@RestController
@RequestMapping("incidents")
public class IncidentController {

    /*
     * Descripció: Instància de la classe que s'encarrega de proveïr el servei de Domini d'Incidències
     */
    @Autowired
    private IDomainIncidentService iDomainIncidentService;

    /*
     * Descripció: Crida a l'API que obté una incidència concreta donat un identificador per paràmetre
     * Resultat: Retorna l'status de la crida i la incidència en qüestió
     */
    @Operation(summary = "Gets a set of incidents by an specific user mail and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The incident identified created by the user with the mail specified", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Incident.class))),
            @ApiResponse(responseCode = "404", description = "Incident not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @GetMapping(value = "/filters/users")
    public ResponseEntity<List<IncidentDTO>> findByUserAndResolved(@RequestHeader("API-Token") String apiToken, String userId, Boolean resolved) {
        return ResponseEntity.status(HttpStatus.OK).body(iDomainIncidentService.findByUserAndResolved(apiToken, userId, resolved));
    }

    /*
     * Descripció: Crida a l'API que obté una incidència concreta donat un identificador per paràmetre
     * Resultat: Retorna l'status de la crida i la incidència en qüestió
     */
    @Operation(summary = "Gets a set of incidents by an specific event and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The incident identified related with the event with the id specified", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Incident.class))),
            @ApiResponse(responseCode = "404", description = "Incident not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @GetMapping(value = "/filters/events")
    public ResponseEntity<List<IncidentDTO>> findByEventAndResolved(@RequestHeader("API-Token") String apiToken, Long eventId, Boolean resolved) {
        return ResponseEntity.status(HttpStatus.OK).body(iDomainIncidentService.findByEventAndResolved(apiToken, eventId, resolved));
    }

    /*
     * Descripció: Crida a l'API que obté totes les incidències de la taula incidents o les creades per l'usuari, amb paginació opcional
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna l'status de la crida i les incidències en qüestió
     */
    @Operation(summary = "Gets all incidents if the user is admin, otherwise gets all incidents related with the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All incidents retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Incident.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @GetMapping
    public ResponseEntity<Page<IncidentDTO>> getAllIncidents(
            @RequestHeader("API-Token") String apiToken,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainIncidentService.getAllIncidents(apiToken, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que esborra una incidència de la taula incidents
     * Resultat: Retorna l'status de la crida
     */
    @Operation(summary = "Deletes a concrete incident specified by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident successfully deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Incident.class))),
            @ApiResponse(responseCode = "404", description = "Incident not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @DeleteMapping
    public ResponseEntity<Boolean> deleteIncident(@RequestHeader("API-Token") String apiToken, @RequestParam Long id) {
        iDomainIncidentService.deleteIncident(apiToken, id);
        return ResponseEntity.ok(!iDomainIncidentService.existsById(id));
    }

    /*
     * Descripció: Crida a l'API que elimina una incidència donat un identificador d'esdeveniment
     * Resultat: Retorna l'status de l'esborrat de la incidència i
     */
    @Operation(summary = "Deletes all the incidents with its event id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Incident successfully saved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Incident.class))),
            @ApiResponse(responseCode = "200", description = "Incident successfully saved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Incident.class))),
            @ApiResponse(responseCode = "404", description = "Incident with this event id does not exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Incident.class))),
            @ApiResponse(responseCode = "500", description = "Some of the provided parameters references entities that were not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @DeleteMapping("/event")
    public ResponseEntity<Boolean> deleteIncidentByEvent(@RequestHeader("API-Token") String apiToken, @RequestParam Long eventId) {
            iDomainIncidentService.deleteIncidentsByEvent(apiToken, eventId);
            return ResponseEntity.ok(!iDomainIncidentService.existsByEventId(eventId));
    }

    /*
     * Descripció: Crida a l'API que crea una incidència i la guarda a la taula incidents
     * Resultat: Retorna l'status de la creació de la incidència i la incidència en qüestió
     */
    @Operation(summary = "Saves a concrete incident in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Incident successfully saved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Incident.class))),
            @ApiResponse(responseCode = "500", description = "Some of the provided parameters references entities that were not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "510", description = "Incident already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @PostMapping
    public ResponseEntity<IncidentDTO> saveIncident(@RequestHeader("API-Token") String apiToken, @Valid @RequestBody SubmitIncidentDTO incident) throws Exception {
        return ResponseEntity.ok(iDomainIncidentService.saveIncident(apiToken, incident));
    }

    /*
     * Descripció: Crida a l'API que edita la informació d'una incidència
     * Resultat: Retorna l'status de la crida i la incidència modificada
     */
    @Operation(summary = "Edits a concrete incident")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Incident successfully edited", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Incident.class))),
            @ApiResponse(responseCode = "500", description = "Some of the provided parameters references entities that were not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @PutMapping
    public ResponseEntity<IncidentDTO> editIncident (@RequestHeader("API-Token") String apiToken, @Valid @RequestBody IncidentDTO incident) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iDomainIncidentService.editIncident(apiToken, incident));
    }
}
