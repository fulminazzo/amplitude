package it.angrybear.serializers;

import it.angrybear.components.TextComponent;
import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import it.angrybear.interfaces.IComponentSerializer;

public class AmpersandSerializer implements IComponentSerializer {
    @SuppressWarnings("unchecked")
    @Override
    public String serializeComponent(TextComponent component) {
        String output = "";
        while (component != null) {
            Color color = component.getColor();
            if (color != null) output += "&" + color.getIdentifierChar();
            for (Style style : component.getStyles())
                output += "&" + style.getIdentifierChar();
            output += component.getText();

            component = component.getNext();
        }
        return output;
    }
}
