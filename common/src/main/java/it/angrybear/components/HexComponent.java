package it.angrybear.components;

import it.angrybear.interfaces.validators.HexColorValidator;
import it.angrybear.interfaces.validators.OptionValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a HEX component, an implementation of {@link OptionComponent} that supports HEX colors.
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
        super(rawText, "hex");
    }

    @Override
    public void setOptions(@Nullable String rawText) {
        super.setOptions(rawText);
        setColor(new Color(getHexColor()));
    }

    /**
     * Gets hex color.
     *
     * @return the hex color
     */
    public @NotNull String getHexColor() {
        return getTagOption("color").toUpperCase();
    }

    @Override
    protected @NotNull Map<String, OptionValidator> getRequiredOptions() {
        return new HashMap<String, OptionValidator>(){{
            put("color", new HexColorValidator());
        }};
    }
}
