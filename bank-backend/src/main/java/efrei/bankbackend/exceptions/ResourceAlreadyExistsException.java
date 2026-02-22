package efrei.bankbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a client attempts to create or register a resource
 * that already exists in the system.
 *
 * <p>Typical examples include creating an account with an email that is already
 * registered or adding a duplicate entity with a unique constraint.</p>
 *
 * <p>This exception results in an HTTP 400 (Bad Request) response being returned
 * to the client with a descriptive error message.</p>
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceAlreadyExistsException extends BaseException {

    /**
     * Creates a new {@code ResourceAlreadyExistsException} with a detailed message
     * explaining which resource already exists and why the request failed.
     *
     * @param message a human-readable explanation of the conflict
     */
    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}