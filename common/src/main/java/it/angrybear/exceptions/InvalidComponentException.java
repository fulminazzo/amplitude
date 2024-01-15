package it.angrybear.exceptions;

/**
 * An exception thrown when a {@link it.angrybear.components.ContainerComponent} results invalid.
 */
public class InvalidComponentException extends RuntimeException {

    /**
     * Instantiates a new Invalid component exception.
     *
     * @param message the message
     */
    public InvalidComponentException(String message) {
        super(message);
    }
}
