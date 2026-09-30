package se.lexicon.lexiconwsecommercejpa.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import se.lexicon.lexiconwsecommercejpa.exception.EmailAlreadyExistsException;
import se.lexicon.lexiconwsecommercejpa.exception.ResourceNotFoundException;

/*
 * WHY THIS CLASS EXISTS AT ALL, since the exceptions already carry
 * @ResponseStatus and Spring would answer 404/409 without any help:
 *
 * Without a handler, the response body is whatever Spring's default error
 * resolver produces - and for @ResponseStatus exceptions that default body does
 * NOT carry your message. The client gets a status code and little else, so
 * "that email is already taken" and "that id does not exist" are
 * indistinguishable from the outside. @ExceptionHandler puts the actual message
 * in the body.
 *
 * The spec does not state the status codes or the body shape - they are your
 * decision. The values below are the honest ones; change them if you have a
 * reason to.
 *
 * @RestControllerAdvice, not @ControllerAdvice: the difference is whether the
 * return value is serialized to JSON. With plain @ControllerAdvice a
 * ResponseEntity would come back as an object literal, not JSON, and the client
 * would be parsing text. This is the one annotation in the class that you
 * should not "simplify" away.
 *
 * NOTE both custom exceptions are thrown from the SERVICE, never from a
 * controller - these handlers exist so that a service can stay ignorant of HTTP
 * entirely.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

  // TODO: Part 4. 404 - the id the client asked for is not there.
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException e) {
    throw new UnsupportedOperationException("TODO: GlobalExceptionHandler.handleResourceNotFound");
  }

  // TODO: Part 4. 409 - the request was well formed but clashes with existing
  // state. Not 400: nothing about the request was malformed.
  //
  // WATCH OUT: this handler is currently unreachable. CustomerServiceImpl still
  // throws IllegalArgumentException for a duplicate email, not
  // EmailAlreadyExistsException, so until you change the service this method
  // never fires and duplicates still come back as 500.
  @ExceptionHandler(EmailAlreadyExistsException.class)
  public ResponseEntity<String> handleEmailExists(EmailAlreadyExistsException e) {
    throw new UnsupportedOperationException("TODO: GlobalExceptionHandler.handleEmailExists");
  }

  // TODO: Part 4. 400 - the client's JSON did not match the constraints on the
  // request record (@NotBlank, @Email, @NotNull on CustomerRequest).
  //
  // Thrown by Spring when @Valid fails, before your method is ever entered -
  // which is exactly why the endpoint itself never needs to check.
  //
  // e.getBindingResult() holds every field error at once. Reporting only
  // e.getMessage() is the common shortcut and it is a bad one: it dumps an
  // internal toString that names no field the client can act on.
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<String> handleValidation(MethodArgumentNotValidException e) {
    throw new UnsupportedOperationException("TODO: GlobalExceptionHandler.handleValidation");
  }
}
