package efrei.bankbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when a user exceeds the allowed number of requests in a given time window.
 *
 * <p>This results in an HTTP 429 (Too Many Requests) response with a descriptive message.</p>
 */
@ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
public class TooManyRequestsException extends BaseException {

    /**
     * Creates a new {@code TooManyRequestsException} with a detailed message.
     */
    public TooManyRequestsException(long retryAfter) {
        super("Too many requests. Please try again after " + retryAfter + " seconds.");
    }
}