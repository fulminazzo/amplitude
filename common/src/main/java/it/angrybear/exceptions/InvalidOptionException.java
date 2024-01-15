package it.angrybear.exceptions;

public class InvalidOptionException extends RuntimeException {

    public InvalidOptionException(String option, Class<?> expected, String actual) {
        super(String.format("Could not validate option \"%s\": expected \"%s\", but got \"%s\"",
                option, expected.getSimpleName(), actual));
    }
}