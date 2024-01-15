package it.angrybear.serializers;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import it.angrybear.exceptions.InvalidOptionException;
import org.jetbrains.annotations.Nullable;

/**
 * An implementation of {@link ComponentSerializer} that mimics the Minecraft default behavior.
 * <p>
 * For example, using '<i>&</i>' as {@link #charCode} will result in the following conversion:
 * <p>
 * "&#60;red&#62;Hello &#60;gold&#62;friend!"
 * => "&cHello &6friend!"
 */
@SuppressWarnings("unchecked")
public class CharCodeSerializer extends ComponentSerializer {
    private final String charCode;

    /**
     * Instantiates a new Char code serializer.
     *
     * @param charCode the char code
     */
    public CharCodeSerializer(String charCode) {
        this.charCode = charCode;
    }

    @Override
    public @Nullable String serializeComponent(@Nullable TextComponent component) {
        if (component == null) return null;
        String output;
        if (component instanceof HoverComponent)
            output = serializeHoverComponent((HoverComponent) component);
        else if (component instanceof ClickComponent)
            output = serializeClickComponent((ClickComponent) component);
        else if (component instanceof HexComponent)
            output = serializeHexComponent((HexComponent) component);
        else {
            Color color = component.getColor();
            output = "";
            if (color != null) output += charCode + color.getIdentifierChar();
            for (Style style : component.getStyles())
                output += charCode + style.getIdentifierChar();
            output += component.getText();
        }

        if (component.getNext() != null)
            output += serializeComponent(component.getNext());
        return output;
    }

    @Override
    public @Nullable String serializeHoverComponent(@Nullable HoverComponent component) throws InvalidOptionException {
        return component == null ? null : serializeComponent(component.getChild());
    }

    @Override
    public @Nullable String serializeClickComponent(@Nullable ClickComponent component) throws InvalidOptionException {
        return component == null ? null : serializeComponent(component.getChild());
    }

    @Override
    public @Nullable String serializeHexComponent(@Nullable HexComponent component) throws InvalidOptionException {
        return component == null ? null : ("&" + String.join("&", component.getTagOption("color").toLowerCase().split("")) + component.getText());
    }
}
