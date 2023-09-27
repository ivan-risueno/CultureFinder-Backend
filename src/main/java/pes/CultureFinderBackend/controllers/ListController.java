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
import pes.CultureFinderBackend.controllers.dtos.EventDTO;
import pes.CultureFinderBackend.controllers.dtos.ListDTO;
import pes.CultureFinderBackend.domain.exceptions.Error;
import pes.CultureFinderBackend.domain.services.IDomainListService;

@RestController
@RequestMapping("/lists")
public class ListController {

    /*
     * Descripció: Instància de la classe que fa de lligam entre la capa de Domini i la de Dades(per a llistes)
     */
    @Autowired
    private IDomainListService iDomainListService;

    /*
     * Descripció: Crida a l'API que obté totes les llistes dins de la BD
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna l'status de la crida i les llistes en qüestió
     */
    @Operation(summary = "Gets all the lists of a logged user")
    @GetMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Page<ListDTO>> getAllUserLists(
            @RequestHeader("API-Token") String apiToken,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainListService.getAllUserLists(apiToken, page, size, enablePagination));
    }


    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments d'una llista
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets all the events of a specified list")
    @GetMapping(value = "/events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Page<EventDTO>> getAllEventsFromList(
            @RequestHeader("API-Token") String apiToken,
            @RequestParam Long listId,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainListService.getAllEventsFromList(apiToken, listId, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que afegeix una nova llista buida
     * Resultat: Retorna l'status de la crida i true si s'ha creat correctament la llista, false altrament
     */
    @Operation(summary = "Creates an empty list for a logged user")
    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "List successfully created"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
    })
    public ResponseEntity<ListDTO> createEmptyList(@RequestHeader("API-Token") String apiToken, @RequestParam String name) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iDomainListService.createEmptyList(apiToken, name));
    }

    /*
     * Descripció: Crida a l'API que edita el nom d'una llista
     * Resultat: Retorna l'status de la crida i la informació actualitzada
     */
    @Operation(summary = "Edits the name and description of a given list")
    @PutMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "List edited successfully"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "List not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<ListDTO> editName(@RequestHeader("API-Token") String apiToken, @RequestParam Long listId,
                                            @RequestParam(required = false) String name,
                                            @RequestParam(required = false) String description) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iDomainListService.editList(apiToken, listId, name, description));
    }

    /*
     * Descripció: Crida a l'API que afegeix un esdeveniment a una llista
     * Resultat: Retorna l'status de la crida i la informació actualitzada
     */
    @Operation(summary = "Adds an event to a list")
    @PostMapping(value = "/events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event added successfully to the list"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "List/Event not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<ListDTO> addEvent(@RequestHeader("API-Token") String apiToken, @RequestParam Long listId, @RequestParam Long eventId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iDomainListService.addEvent(apiToken, listId, eventId));
    }

    /*
     * Descripció: Crida a l'API que afegeix un esdeveniment a una llista
     * Resultat: Retorna l'status de la crida i la informació actualitzada
     */
    @Operation(summary = "Removes an event from a list")
    @DeleteMapping(value = "/events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event removed successfully from the list"),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "404", description = "List/Event not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Boolean> removeEventFromList(@RequestHeader("API-Token") String apiToken, @RequestParam Long listId, @RequestParam Long eventId) {
        iDomainListService.removeEvent(apiToken, listId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(!iDomainListService.existsEventInList(listId, eventId));
    }

    /*
     * Descripció: Crida a l'API que esborra una llista del sistema
     * Resultat: Retorna l'status de la crida i true si la llista s'ha esborrat correctament, false altrament
     */
    @Operation(summary = "Deletes a list of a logged user")
    @DeleteMapping()
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List sucessfully deleted"),
            @ApiResponse(responseCode = "404", description = "List not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Boolean> deleteList(@RequestHeader("API-Token") String apiToken, @RequestParam Long listId) {
        iDomainListService.deleteList(apiToken, listId);
        return ResponseEntity.ok(!iDomainListService.existsById(listId));
    }
}
