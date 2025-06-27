package it.fulminazzo.amplitude.component;

import it.fulminazzo.amplitude.component.validator.OptionValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * An interface used for identifying Minecraft events actions.
 * See {@link ClickAction} and {@link HoverAction} for the implementations.
 */
interface IAction {

    /**
     * Gets a map containing the required options.
     * The parameters are given in a String-{@link OptionValidator}
     * pair that allows to find and verify the validity of an option.
     *
     * @return the required options
     */
    @NotNull Map<String, OptionValidator> getRequiredOptions();

    /**
     * Gets the name.
     *
     * @return the name
     */
    @NotNull String name();

}