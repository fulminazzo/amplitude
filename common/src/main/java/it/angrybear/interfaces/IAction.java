package it.angrybear.interfaces;

import it.angrybear.components.ClickAction;
import it.angrybear.components.HoverAction;
import it.angrybear.components.validator.OptionValidator;

import java.util.Map;

/**
 * An interface used for identifying Minecraft events actions.
 * See {@link ClickAction} and {@link HoverAction} for the implementations.
 */
public interface IAction {

    /**
     * Gets a map containing the required options.
     * The parameters are given in a String-{@link OptionValidator}
     * pair that allows to find and verify the validity of an option.
     *
     * @return the required options
     */
    Map<String, OptionValidator> getRequiredOptions();

    /**
     * Gets the name.
     *
     * @return the name
     */
    String name();
}