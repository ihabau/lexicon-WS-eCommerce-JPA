package se.lexicon.lexiconwsecommercejpa.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 * The conflict case for register/update: the email is syntactically fine but
 * somebody else already owns it.
 *
 * Why this exists at all, given both call sites already throw
 * IllegalArgumentException: IllegalArgumentException carries no HTTP status, so
 * a duplicate email currently reaches the client as a 500 - the CLIENT's fault
 * (they picked a taken address) reported as a SERVER fault. 409 CONFLICT is the
 * honest answer. Same reasoning as ResourceNotFoundException's 404, and for the
 * same reason: the annotation does the work, so no controller needs try/catch to
 * turn a business outcome into a sensible status.
 *
 * NAMING: ResourceNotFoundException's javadoc suggests "DuplicateEmailException".
 * Part 4's class diagram names this EmailAlreadyExistsException, so that is what
 * it is called here - GlobalExceptionHandler.handleEmailExists must match.
 *
 * Thrown in the SERVICE (CustomerServiceImpl), not the controller, for the same
 * reason ResourceNotFoundException is: that is where "this email is taken" is
 * actually known. Note the distinction the impl has to keep straight - existsByEmail
 * answers "is this FREE?" for register, while an update needs findByEmail and has
 * to EXCLUDE the caller's own row, or a customer who keeps their own email is
 * rejected as a duplicate.
 *
 * Unchecked (RuntimeException) so the service need not declare `throws` on every
 * method, and so Spring's default rollback rules treat it as a business failure.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class EmailAlreadyExistsException extends RuntimeException {

    // Recommended for serializable classes. IDE advice rather than a JPA rule,
    // but harmless to satisfy - the value is arbitrary, conventionally 1L.
    private static final long serialVersionUID = 1L;

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
