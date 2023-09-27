package pes.CultureFinderBackend.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pes.CultureFinderBackend.domain.exceptions.Error;
import pes.CultureFinderBackend.domain.exceptions.ObjectAlreadyExistsException;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.exceptions.PermissionDeniedException;

@ControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    /*
     * Descripció: Enllaça l'exepció ObjectNotFoundException amb el codi d'error 404
     * Resultat: Retorna una instància d'Error amb codi 404 i missatge ex.getMessage()
     */
    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ResponseEntity<Object> handleObjectNotFoundException(ObjectNotFoundException ex) {
        Error error = new Error(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /*
     * Descripció: Enllaça l'exepció ObjectAlreadyExistsException amb el codi d'error 510
     * Resultat: Retorna una instància d'Error amb codi 510 i missatge ex.getMessage()
     */
    @ExceptionHandler(ObjectAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<Object> handleObjectAlreadyExistsException(ObjectAlreadyExistsException ex) {
        Error error = new Error(510, ex.getMessage());  // el codi 510 no existeix, l'hem creat nosaltres
        return ResponseEntity.status(510).body(error);
    }

    /*
     * Descripció: Enllaça l'exepció PermissionDeniedException amb el codi d'error 403
     * Resultat: Retorna una instància d'Error amb codi 403 i missatge ex.getMessage()
     */
    @ExceptionHandler(PermissionDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ResponseEntity<Object> handlePermissionDeniedException(PermissionDeniedException ex) {
        Error error = new Error(HttpStatus.FORBIDDEN.value(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN.value()).body(error);
    }
}