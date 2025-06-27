package it.fulminazzo.amplitude.component;

import it.fulminazzo.amplitude.component.validator.FontValidator;
import it.fulminazzo.amplitude.component.validator.OptionValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a font component, an implementation of {@link OptionComponent} to specify the font of the text.
 * It requires "<b>id</b>" as an option, and it will be parsed using {@link FontValidator}.
 * <p>
 * Example: "&#60;font id=ALT&#62;Hello world!" will be parsed with:
 * <ul>
 *     <li>id: ALT</li>
 *     <li>text: Hello world!</li>
 * </ul>
 */
public final class FontComponent extends OptionComponent {

    public FontComponent() {
        this(null);
    }

    public FontComponent(final String rawText) {
        super(rawText, "font");
    }

    @Override
    protected void setOptions(final @Nullable String rawText) {
        super.setOptions(rawText);
        setFont(Font.valueOf(getFontID()));
    }

    public @NotNull String getFontID() {
        return getTagOption("id").toUpperCase();
    }

    @Override
    protected @NotNull Map<String, OptionValidator> getRequiredOptions() {
        return new HashMap<String, OptionValidator>(){{
            put("id", new FontValidator());
        }};
    }

}