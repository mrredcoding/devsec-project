package efrei.bankbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user's token is invalid due to expiration or black-listing.
 *
 * <p>This results in an HTTP 401 (Unauthorized) response with a descriptive message.</p>
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenInvalidException extends BaseException {

    /**
     * Creates a new {@code TokenInvalidException} with a detailed message.
     */
    public TokenInvalidException() {
        super("Your token is either expired or black-listed.");
    }
}