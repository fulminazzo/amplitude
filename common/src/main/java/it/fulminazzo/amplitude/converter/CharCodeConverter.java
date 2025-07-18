package it.fulminazzo.amplitude.converter;

import it.fulminazzo.amplitude.component.*;
import it.fulminazzo.amplitude.exception.InvalidOptionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link ComponentConverter} that mimics the Minecraft default behavior.
 * <p>
 * For example, using '<i>&amp;</i>' as {@link #charCode} will result in the following conversion:
 * <p>
 * "&#60;red&#62;Hello &#60;gold&#62;friend!"
 * =&#62; "&amp;cHello &amp;6friend!"
 */
@SuppressWarnings("unchecked")
public class CharCodeConverter extends ComponentConverter {
    private final String charCode;

    /**
     * Instantiates a new Char code serializer.
     *
     * @param charCode the char code
     */
    public CharCodeConverter(String charCode) {
        this.charCode = charCode;
    }

    @Override
    public @Nullable String convertSimpleComponent(@Nullable Component component) {
        if (component == null) return null;
        String output = "";
        if (component.isReset()) output = reset(output);
        else {
            Color color = component.getColor();
            if (color != null) output += applyColor(output, color);
            Font font = component.getFont();
            if (font != null) output += applyFont(output, font);
            for (Style style : component.getStyles()) output += applyStyle(output, style, component.getStyle(style));
        }
        output += component.getText();
        return output;
    }

    @Override
    public @Nullable String convertHoverComponent(@Nullable HoverComponent component) throws InvalidOptionException {
        return component == null ? null : convertComponent(component.getChild());
    }

    @Override
    public @Nullable String convertClickComponent(@Nullable ClickComponent component) throws InvalidOptionException {
        return component == null ? null : convertComponent(component.getChild());
    }

    @Override
    public @Nullable String convertHexComponent(@Nullable HexComponent component) throws InvalidOptionException {
        if (component == null) return null;
        String color = component.getHexColor();
        color = color.substring(1);
        color = charCode + "x" + charCode + String.join(charCode, color.split(""));
        return color + component.getText();
    }

    @Override
    public @Nullable String convertInsertionComponent(@Nullable InsertionComponent component) {
        if (component == null) return null;
        return convertComponent(component.getChild());
    }

    @Override
    public @Nullable String convertFontComponent(@Nullable FontComponent component) {
        if (component == null) return null;
        return convertSimpleComponent(component);
    }

    @Override
    public @Nullable String convertTranslateComponent(@Nullable TranslatableComponent component) {
        if (component == null) return null;
        return convertSimpleComponent(component.getChild());
    }

    @Override
    public <T> @Nullable T sumTwoConvertedComponents(@NotNull T component1, @NotNull T component2) {
        return (T) (component1 + component2.toString());
    }

    @Override
    public <T> @Nullable T applyColor(@Nullable T component, @NotNull Color color) {
        if (component == null) return null;
        final char idChar = color.getIdentifierChar();
        if (idChar == '?') {
            String code = color.getCode().toUpperCase()
                    .replace("#", "x")
                    .replace("", charCode);
            if (code.endsWith(charCode)) code = code.substring(0, code.length() - 1);
            return (T) code;
        } else return (T) (charCode + idChar);
    }

    @Override
    public <T> @Nullable T applyStyle(@Nullable T component, @NotNull Style style, @Nullable Boolean value) {
        if (component == null) return null;
        if (value == null || !value) return (T) "";
        return (T) (charCode + style.getIdentifierChar());
    }

    @Override
    public <T> @Nullable T applyFont(T component, Font font) {
        return component;
    }

    @Override
    public <T> @Nullable T reset(@Nullable T component) {
        if (component == null) return null;
        return (T) (charCode + "r" + component);
    }

    @Override
    public <T, P> void send(P player, T component) {
        throw new RuntimeException("Not implemented.");
    }

}
