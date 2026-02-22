package efrei.bankbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when authentication fails due to invalid credentials.
 *
 * <p>This occurs when the provided email and/or password do not match
 * any registered user account. It represents an authentication failure,
 * not an authorization issue.
 *
 * <p>Mapped to HTTP 401 (Unauthorized), indicating that the request
 * requires valid authentication credentials.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidPasswordOrEmailException extends BaseException {

    /**
     * Constructs a new {@code InvalidPasswordOrEmailException}
     * with a default authentication failure message.
     */
    public InvalidPasswordOrEmailException() {
        super("Incorrect email or password.");
    }
}