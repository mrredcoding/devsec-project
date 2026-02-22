package efrei.bankbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an authenticated user attempts to perform
 * an operation they are not authorized to execute.
 *
 * <p>This typically occurs when business or security rules deny access,
 * for example:
 * <ul>
 *   <li>a user exceeding the allowed transaction amount</li>
 *   <li>a user accessing another user's bank account</li>
 *   <li>missing required role or permission</li>
 * </ul>
 *
 * <p>Mapped to HTTP 403 (Forbidden).
 * The request is understood and the user is authenticated,
 * but does not have sufficient privileges to proceed.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenOperationException extends BaseException {

    /**
     * Constructs a new {@code AccessDeniedException}
     * with an authorization failure message.
     */
    public ForbiddenOperationException(String message) {
        super(message);
    }
}