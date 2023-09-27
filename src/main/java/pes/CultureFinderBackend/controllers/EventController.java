package pes.CultureFinderBackend.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.ErrorResponse;
import pes.CultureFinderBackend.controllers.dtos.EventDTO;
import pes.CultureFinderBackend.domain.exceptions.Error;
import pes.CultureFinderBackend.domain.services.IDomainEventService;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.Set;

import static pes.CultureFinderBackend.domain.businesslogic.EventLogic.processCategories;

@RestController
@RequestMapping("/events")
public class EventController {

    /*
    * Descripció: Instància de la classe que s'encarrega de prover servei a la BD (taula events)
    */
    @Autowired
    private IDomainEventService iDomainEventService;

    /*
     * Descripció: Crida a l'API que crea un esdeveniment i el guarda a la taula events
     * Resultat: Retorna l'status de la creació de l'esdeveniment
     */
    @Operation(summary = "Saves a concrete event to the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event successfully saved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Some of the provided parameters references entities that were not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "510", description = "Event already exists", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EventDTO> saveEvent(@Valid @RequestBody EventDTO event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iDomainEventService.saveEvent(event));
    }

    /*
     * Descripció: Crida a la API que obté tots els esdeveniments de la taula events, amb paginació opcional
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets all events from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All events retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<Page<EventDTO>> getAllEvents(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.getAllEvents(page, size, enablePagination));
    }

    /*
     * Descripció: Crida a la API que obté els esdeveniments recomanats a un usuari, amb paginació opcional
     * <apiToken>: API token de l'usuari en qüestió
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets the recommended events for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All events retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/suggestions")
    public ResponseEntity<Page<EventDTO>> getSuggestedEvents(
            @RequestHeader("API-Token") String apiToken,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.getSuggestedEvents(apiToken, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a la API que obté els esdeveniments recomanats per popularitat, amb paginació opcional
     * <page>: Nombre de la pàgina a mostrar, si enablePagination == true
     * <size>: Nombre d'elements per pàgina, si enablePagination == true
     * <enablePagination>: Indica si es vol utilitzar paginació per a mostrar el resultat
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets the most popular events")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All events retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/popularity")
    public ResponseEntity<Page<EventDTO>> getPopularEvents (
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.getPopularEvents(page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que esborra un esdeveniment de la taula events
     * Resultat: Retorna l'status de la crida
     */
    @Operation(summary = "Deletes a concrete event from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event successfully deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Event not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Boolean> deleteEvent(@PathVariable ("id") Long id) {
        iDomainEventService.deleteEvent(id);
        return ResponseEntity.ok(!iDomainEventService.existsById(id));
    }

    /*
     * Descripció: Crida a l'API que obté tots els camps d'un esdeveniment de la taula events
     * Resultat: Retorna l'status de la crida i l'esdeveniment en qüestió
     */
    @Operation(summary = "Gets an event with the specified id from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/{id}/allInfo")
    public ResponseEntity<Optional<EventDTO>> findEventByIdAllInfo(@PathVariable ("id") Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(iDomainEventService.findById(id));
    }

    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments dels quals es troben entre les donades per paràmetre
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets events with the specified range of dates from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/date")
    public ResponseEntity<Page<EventDTO>> findEventsByDate(
            @RequestParam LocalDate dataIni,
            @RequestParam LocalDate dataFi,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.findAllByDate(dataIni, dataFi, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments dels quals coincideix amb el paràmetre
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets events with the specified denomination from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/denominacio")
    public ResponseEntity<Page<EventDTO>> findEventsByDenominacio(
            @RequestParam(defaultValue = "") String denominacio,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.findAllByDenominacio(denominacio, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments dels quals coincideixen amb els paràmetres
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets events with the specified filters from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/allFilters")
    public ResponseEntity<Page<EventDTO>> findEventsByFilters(@RequestParam(required = false) String ambit,
                                                           @RequestParam(required = false) String categoria,
                                                           @RequestParam(required = false) String altres,
                                                           @RequestParam(required = false) LocalDate dataIni,
                                                           @RequestParam(required = false) LocalDate dataFi,
                                                           @RequestParam(required = false) String preu,
                                                           @RequestParam(required = false) String denominacio,
                                                           @RequestParam(required = false) String comarcaMunicipi,
                                                           @RequestParam(required = false) String descripcio,
                                                           @RequestParam(required = false) Float radi,
                                                           @RequestParam(required = false) Float latitud,
                                                           @RequestParam(required = false) Float longitud,
                                                           @RequestParam(required = false, defaultValue = "0") Integer page,
                                                           @RequestParam(required = false, defaultValue = "10") Integer size,
                                                           @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.findAllByFilters(ambit, categoria, altres, dataIni,
                dataFi, preu, denominacio, comarcaMunicipi, descripcio, radi, latitud, longitud, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments dels quals coincideixen amb els paràmetres
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets events with the specified distance, user position and date range from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/distance")
    public ResponseEntity<Page<EventDTO>> findEventsByDistance(
                                                              @RequestParam(required = false) LocalDate dataIni,
                                                              @RequestParam(required = false) LocalDate dataFi,
                                                              @RequestParam(required = false) Float radi,
                                                              @RequestParam(required = false) Float latitud,
                                                              @RequestParam(required = false) Float longitud,
                                                              @RequestParam(required = false, defaultValue = "0") Integer page,
                                                              @RequestParam(required = false, defaultValue = "10") Integer size,
                                                              @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.findAllByFilters(null, null, null, dataIni,
                dataFi, null, null, null, null, radi, latitud, longitud, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments dels quals coincideixen amb el donat per paràmetre
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets events with the specified ambit from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/ambit")
    public ResponseEntity<Page<EventDTO>> findEventsByAmbit(
            @RequestParam(defaultValue = "") String ambit,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.findAllByAmbit(ambit, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments dels quals coincideixen amb la donada per paràmetre
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets events with the specified category from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/categoria")
    public ResponseEntity<Page<EventDTO>> findEventsByCategoria(
            @RequestParam(defaultValue = "") String categoria,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.findAllByCategoria(categoria, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que obté totes les categories dels esdeveniments que tenim a la BD
     * Resultat: Retorna l'status de la crida i les categories en qüestió
     */
    @Operation(summary = "Gets the possible filter tags from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tags retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/tags")
    public ResponseEntity<Set<String>> findTags() {
        return ResponseEntity.ok(processCategories(iDomainEventService.findAllCategories()));
    }

    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments per altres-categories de les quals coincideixen amb les donades per paràmetre
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets events with the specified id from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/altres")
    public ResponseEntity<Page<EventDTO>> findEventsByAltres(
            @RequestParam(defaultValue = "") String altres,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.findAllByAltres(altres, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments dels quals coincideix amb el donat per paràmetre
     * Resultat: Retorna l'status de la crida i l'esdeveniment en qüestió
     */
    @Operation(summary = "Gets events with the specified price from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/preu")
    public ResponseEntity<Page<EventDTO>> findEventsByPreu(
            @RequestParam(defaultValue = "") String preu,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.findAllByPreu(preu, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments dels quals coincideixen amb el donat per paràmetre
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets events with the specified region from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/comarcaIMunicipi")
    public ResponseEntity<Page<EventDTO>> findEventsByComarcaMunicipi(
            @RequestParam(defaultValue = "") String comarcaMunicipi,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok(iDomainEventService.findAllByComarcaMunicipi(comarcaMunicipi, page, size, enablePagination));
    }

    /*
     * Descripció: Crida a l'API que obté tots els esdeveniments dels quals coincideixen amb la donada per paràmetre
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    @Operation(summary = "Gets events with the specified description from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event retrieved", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping(value = "/descripcio")
    public ResponseEntity<Page<EventDTO>> findEventsByDescripcio(
            @RequestParam(defaultValue = "") String descripcio,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false, defaultValue = "false") Boolean enablePagination) {
        return ResponseEntity.ok((iDomainEventService.findAllByDescripcio(descripcio, page, size, enablePagination)));
    }

    /*
     * Descripció: Crida a l'API que edita la informació d'un esdeveniment
     * Resultat: Retorna l'status de la crida i l'esdeveniment modificat
     */
    @Operation(summary = "Edits a concrete event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event edited successfully"),
            @ApiResponse(responseCode = "404", description = "Event not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    @PutMapping
    public ResponseEntity<EventDTO> editEvent (@Valid @RequestBody EventDTO event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iDomainEventService.editEvent(event));
    }

    /*
     * Descripció: Crida a l'API que crea un crea una valoració per a un esdeveniment
     * Resultat: Retorna l'status de la crida i l'esdeveniment amb la puntuació actualitzada
     */
    @Operation(summary = "Rates an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Event successfully rated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Some of the provided parameters references entities that were not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "510", description = "User already rated this event", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping(value = "/rate")
    public ResponseEntity<EventDTO> rateEvent(@RequestHeader("API-Token") String apiToken, @RequestParam Long eventId, @RequestParam Float score) {
        return ResponseEntity.status(HttpStatus.CREATED).body(iDomainEventService.rateEvent(apiToken, eventId, score));
    }

    /*
     * Descripció: Crida a l'API que crea un esborra una valoració per a un esdeveniment
     * Resultat: Retorna l'status de la crida i l'esdeveniment amb la puntuació actualitzada
     */
    @Operation(summary = "Removes the user rating of an event")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Event valuation successfully removed", content = @Content(mediaType = "application/json", schema = @Schema(implementation = EventDTO.class))),
            @ApiResponse(responseCode = "404", description = "Some of the provided parameters references entities that were not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping(value = "/rate")
    public ResponseEntity<Boolean> unrateEvent(@RequestHeader("API-Token") String apiToken, @RequestParam Long eventId) {
        iDomainEventService.unrateEvent(apiToken, eventId);
        return ResponseEntity.ok(!iDomainEventService.eventAlreadyRated(apiToken, eventId));
    }

    /*
     * Descripció: Crida a l'API que obté la valoració d'un esdeveniment per part d'un usuari, o null altrament
     * Resultat: Retorna l'status de la crida i la valoració
     */
    @Operation(summary = "Gets the rating of an event made by an user")
    @GetMapping(value = "/rating")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Event not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Error.class)))
    })
    public ResponseEntity<Float> getRating(@RequestHeader("API-Token") String apiToken, Long eventId) {
        return ResponseEntity.status(HttpStatus.OK).body(iDomainEventService.getRating(apiToken, eventId));
    }

    /*
     * Descripció: Crida a l'API de l'agenda cultural de Catalunya, que obté tots els esdeveniments a partir de la data quan es fa la crida
     * Resultat: Retorna l'status de la crida i els esdeveniments en qüestió
     */
    public static ResponseEntity<String> getEventsAPIAgendaCultural() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        char[] c = sdf.format(new Date()).toCharArray(); c[10] = 'T';

        String url = "https://analisi.transparenciacatalunya.cat/resource/rhpv-yr4f.json?$where=data_inici > '" + String.valueOf(c) + "'";
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-App-Token", "oF8LPFgjpX7nQkMfsSo8YO7s3");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
    }

}