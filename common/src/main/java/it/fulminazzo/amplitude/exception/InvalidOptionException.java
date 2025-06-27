package it.fulminazzo.amplitude.exception;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown when a given option results invalid.
 */
public final class InvalidOptionException extends RuntimeException {

    /**
     * Instantiates a new Invalid option exception.
     *
     * @param optionName the option name
     * @param expected   the expected
     * @param option     the option
     */
    public InvalidOptionException(final @NotNull String optionName,
                                  final @NotNull Class<?> expected,
                                  final @NotNull String option) {
        this(optionName, expected.getSimpleName(), option);
    }

    /**
     * Instantiates a new Invalid option exception.
     *
     * @param optionName the option name
     * @param expected   the expected
     * @param option     the option
     */
    public InvalidOptionException(final @NotNull String optionName,
                                  final @NotNull String expected,
                                  final @NotNull String option) {
        super(String.format("Could not validate option \"%s\": expected \"%s\", but got \"%s\"",
                optionName, expected, option));
    }

}