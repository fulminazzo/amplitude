package it.angrybear.exceptions;

import it.angrybear.components.ClickAction;
import it.angrybear.components.HoverAction;

import java.util.Map;

/**
 * An exception thrown when a required option is not given.
 * Check {@link ClickAction} and {@link HoverAction} for more.
 */
public class MissingRequiredOptionException extends RuntimeException {

    /**
     * Instantiates a new Missing required option exception.
     *
     * @param optionName the option name
     * @param options    the options
     */
    public MissingRequiredOptionException(String optionName, Map<String, String> options) {
        super(String.format("Could not find option \"%s\" in: %s", optionName, options));
    }
}
