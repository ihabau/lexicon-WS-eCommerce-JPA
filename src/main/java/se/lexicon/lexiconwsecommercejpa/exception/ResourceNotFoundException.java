package se.lexicon.lexiconwsecommercejpa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * @ResponseStatus IS THE PART THAT DOES THE WORK. When the exception reaches
 * Spring's dispatcher it looks for an @ExceptionHandler that claims it; finding
 * none, it falls back to this annotation and the client gets 404 with this
 * message as the body instead of a 500 and a stack trace. So a controller does
 * NOT need try/catch just to turn "not found" into a sensible status - without
 * the annotation, every "not found" in the project would be a 500 and the fix
 * would be a try/catch in every method.
 *
 * Thrown in the SERVICE, not the controller, because that is where the rule
 * "this id does not exist" is applied. The general shape: the layer that KNOWS
 * something is wrong throws; the layer that KNOWS HOW TO REPORT it maps the
 * exception to a response (@RestControllerAdvice, Part 4).
 *
 * RuntimeException, not Exception, so the service need not declare `throws` on
 * every method - a missing row is a normal business outcome, not a programming
 * error. That is also load-bearing for @Transactional: Spring rolls back on a
 * RuntimeException but COMMITS by default on a checked one, so an unchecked
 * exception is what makes placeOrder's all-or-nothing guarantee actually hold.
 *
 * Keep the message in the "Customer not found with id 7" shape: it says WHICH
 * resource and echoes the id the client sent, so a log line can be matched to it
 * without guessing. The resource name is baked into the string, which is a
 * decision rather than an accident - passing the type as a second constructor
 * argument is the tidier shape if you want it.
 *
 * NOT DONE, and optional: a DuplicateEmailException for register/update. Both
 * currently throw IllegalArgumentException, which carries no HTTP status, so a
 * duplicate email reaches the client as a 500 - the client's fault reported as
 * a server fault. 409 CONFLICT is the honest answer. Copy this class's shape:
 *   @ResponseStatus(HttpStatus.CONFLICT)
 *   public class DuplicateEmailException extends RuntimeException { ... }
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Recommended for serializable classes. IDE advice rather than a JPA rule,
    // but harmless to satisfy - the value is arbitrary, conventionally 1L.
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
