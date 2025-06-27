package it.fulminazzo.amplitude.component;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown when a component results invalid.
 */
final class InvalidComponentException extends RuntimeException {

    /**
     * Instantiates a new Invalid component exception.
     *
     * @param message the message
     */
    public InvalidComponentException(final @NotNull String message) {
        super(message);
    }

}
