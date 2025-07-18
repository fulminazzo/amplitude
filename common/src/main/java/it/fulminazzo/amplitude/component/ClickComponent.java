package it.fulminazzo.amplitude.component;

import it.fulminazzo.amplitude.component.validator.OptionValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Represents an implementation of {@link ContainerComponent} that supports click actions.
 * All actions are defined in {@link ClickAction} with detailed comments for the required options.
 * <p>
 * Example: &#60;click action="OPEN_URL" url="https://fulminazzo.it"&#62;Click event!&#60;/click&#62;
 */
public final class ClickComponent extends ContainerComponent<ClickComponent> implements IEventComponent {

    /**
     * Instantiates a new Click component.
     */
    public ClickComponent() {
        this(null);
    }

    /**
     * Instantiates a new Click component.
     *
     * @param rawText the raw text
     */
    public ClickComponent(final @Nullable String rawText) {
        super(rawText, "click");
    }

    @Override
    public Map<String, OptionValidator> getRequiredOptions() {
        return IEventComponent.super.getRequiredOptions();
    }

    @Override
    public @NotNull Class<ClickAction> getActionClass() {
        return ClickAction.class;
    }

}
