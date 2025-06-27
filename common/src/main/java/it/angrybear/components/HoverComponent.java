package it.angrybear.components;

import it.angrybear.components.validator.OptionValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an implementation of {@link ContainerComponent} that supports click actions.
 * All actions are defined in {@link HoverAction} with detailed comments for the required options.
 * <p>
 * Example: &#60;hover action="SHOW_TEXT" text="Secret message!"&#62;Hover event!&#60;/hover&#62;
 */
public final class HoverComponent extends ContainerComponent implements IEventComponent {

    /**
     * Instantiates a new Hover component.
     */
    public HoverComponent() {
        this(null);
    }

    /**
     * Instantiates a new Hover component.
     *
     * @param rawText the raw text
     */
    public HoverComponent(final @Nullable String rawText) {
        super(rawText, "hover");
    }

    /**
     * Sets hover action.
     *
     * @param action    the action
     * @param component the component
     */
    public void setHoverAction(final HoverAction action, final TextComponent component) {
        setTagOption("action", action.name());
        final Map<String, OptionValidator> requiredOptions = action.getRequiredOptions();
        for (String key : requiredOptions.keySet())
            setTagOption(key, component.serialize());
        checkOptions();
    }

    @Override
    public Map<String, OptionValidator> getRequiredOptions() {
        return IEventComponent.super.getRequiredOptions();
    }

    @Override
    public @NotNull Class<? extends IAction> getActionClass() {
        return HoverAction.class;
    }

}
