package it.angrybear.components;

import it.angrybear.interfaces.validators.OptionValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link ContainerComponent} that allows adding insertion to the current text.
 * Insertions are texts that are written in the player chat box upon pressing SHIFT + LEFT CLICK.
 * A text option is required.
 * <p>
 * Example: "&#60;insertion text="Amazing!"&#62;Hello world&#60;/insertion&#62;"
 */
public class InsertionComponent extends ContainerComponent {

    public InsertionComponent() {
        this(null);
    }

    public InsertionComponent(@Nullable String rawText) {
        super(rawText, "insertion");
    }

    public String getInsertionText() {
        return getTagOption("text");
    }

    @Override
    protected @NotNull Map<String, OptionValidator> getRequiredOptions() {
        return new HashMap<String, OptionValidator>(){{
            put("text", null);
        }};
    }
}
