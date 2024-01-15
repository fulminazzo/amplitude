package it.angrybear.components;

import it.angrybear.interfaces.validators.HexColorValidator;
import it.angrybear.interfaces.validators.OptionValidator;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Represent a HEX component, an implementation of {@link OptionComponent} that supports HEX colors.
 * It requires "<b>color</b>" as an option, and it will be parsed using {@link HexColorValidator}.
 * <p>
 * Example: "&#60;hex color=#FF00AA&#62;Hello world!" will be parsed with:
 * <ul>
 *     <li>color: #ff00aa</li>
 *     <li>text: Hello world!</li>
 * </ul>
 */
public class HexComponent extends OptionComponent {

    /**
     * Instantiates a new Hex component.
     */
    public HexComponent() {
        this(null);
    }

    /**
     * Instantiates a new Hex component.
     *
     * @param rawText the raw text
     */
    public HexComponent(String rawText) {
        super(rawText);
    }

    @Override
    protected @NotNull Map<String, OptionValidator> getRequiredOptions() {
        Map<String, OptionValidator> options = new HashMap<>();
        options.put("color", new HexColorValidator());
        return options;
    }
}
