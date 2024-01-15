package it.angrybear.exceptions;

public class InvalidOptionException extends RuntimeException {

    public InvalidOptionException(String optionName, Class<?> expected, String option) {
        this(optionName, expected.getSimpleName(), option);
    }

    public InvalidOptionException(String optionName, String expected, String option) {
        super(String.format("Could not validate option \"%s\": expected \"%s\", but got \"%s\"",
                optionName, expected, option));
    }
}