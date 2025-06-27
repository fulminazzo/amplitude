package it.fulminazzo.amplitude.component;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An exception thrown when a required option is not given.
 * Check {@link ClickAction} and {@link HoverAction} for more.
 */
final class MissingRequiredOptionException extends RuntimeException {

    /**
     * Instantiates a new Missing required option exception.
     *
     * @param optionName the option name
     * @param options    the options
     */
    public MissingRequiredOptionException(final @NotNull String optionName,
                                          final @NotNull Map<String, String> options) {
        super(String.format("Could not find option \"%s\" in: %s", optionName, options));
    }

}
