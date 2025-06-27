package it.angrybear.exceptions;

import org.jetbrains.annotations.NotNull;

/**
 * An exception thrown when a given option results invalid.
 */
public class InvalidOptionException extends RuntimeException {

    /**
     * Instantiates a new Invalid option exception.
     *
     * @param optionName the option name
     * @param expected   the expected
     * @param option     the option
     */
    public InvalidOptionException(String optionName, @NotNull Class<?> expected, String option) {
        this(optionName, expected.getSimpleName(), option);
    }

    /**
     * Instantiates a new Invalid option exception.
     *
     * @param optionName the option name
     * @param expected   the expected
     * @param option     the option
     */
    public InvalidOptionException(String optionName, String expected, String option) {
        super(String.format("Could not validate option \"%s\": expected \"%s\", but got \"%s\"",
                optionName, expected, option));
    }

}