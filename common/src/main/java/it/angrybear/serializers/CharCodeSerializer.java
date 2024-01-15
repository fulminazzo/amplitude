package it.angrybear.serializers;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import it.angrybear.exceptions.InvalidOptionException;
import it.angrybear.interfaces.IComponentSerializer;

@SuppressWarnings("ALL")
public class CharCodeSerializer implements IComponentSerializer {
    private final String charCode;

    public CharCodeSerializer(String charCode) {
        this.charCode = charCode;
    }

    @Override
    public String serializeComponent(TextComponent component) {
        if (component == null) return null;
        String output;
        if (component instanceof HoverComponent)
            output = serializeHoverComponent((HoverComponent) component);
        else if (component instanceof ClickComponent)
            output = serializeClickComponent((ClickComponent) component);
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
    public String serializeHoverComponent(HoverComponent component) throws InvalidOptionException {
        return component == null ? null : serializeComponent(component.getChild());
    }

    @Override
    public String serializeClickComponent(ClickComponent component) throws InvalidOptionException {
        return component == null ? null : serializeComponent(component.getChild());
    }
}
