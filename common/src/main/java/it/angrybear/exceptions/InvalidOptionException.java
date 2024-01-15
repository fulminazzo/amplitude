package it.angrybear.exceptions;

public class InvalidOptionException extends RuntimeException {

    public InvalidOptionException(String option, Class<?> expected, String actual) {
        this(option, expected.getSimpleName(), actual);
    }

    public InvalidOptionException(String option, String expected, String actual) {
        super(String.format("Could not validate option \"%s\": expected \"%s\", but got \"%s\"",
                option, expected, actual));
    }
}