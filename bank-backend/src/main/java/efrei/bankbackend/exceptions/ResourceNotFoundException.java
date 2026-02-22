package efrei.bankbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a requested resource cannot be found in the system.
 *
 * <p>Typical examples include trying to retrieve a bank account, user, or transaction
 * that does not exist.</p>
 *
 * <p>This exception results in an HTTP 404 (Not Found) response being returned
 * to the client with a descriptive error message.</p>
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BaseException {

    /**
     * Creates a new {@code ResourceNotFoundException} with a detailed message
     * explaining which resource was not found.
     *
     * @param message a human-readable explanation of the missing resource
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}