package it.angrybear.exceptions;

import java.util.Map;

public class MissingRequiredOptionException extends RuntimeException {

    public MissingRequiredOptionException(String optionName, Map<String, String> options) {
        super(String.format("Could not find option \"%s\" in: %s", optionName, options));
    }
}
