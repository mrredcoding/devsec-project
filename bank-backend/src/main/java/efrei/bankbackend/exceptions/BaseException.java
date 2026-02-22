package efrei.bankbackend.exceptions;

import lombok.Getter;

/**
 * Abstract base class for application-specific exceptions.
 * Centralizes message handling and enforces a consistent exception structure.
 */
@Getter
public abstract class BaseException extends Exception {

    /**
     * Constructs a new BaseException with a detailed error message.
     *
     * @param message the error message to associate with this exception.
     */
    protected BaseException(String message) {
        super(message);
    }
}