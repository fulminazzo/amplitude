package it.angrybear.serializers;

import it.angrybear.components.TextComponent;
import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import it.angrybear.interfaces.IComponentSerializer;

public class CharCodeSerializer implements IComponentSerializer {
    private final String charCode;

    public CharCodeSerializer(String charCode) {
        this.charCode = charCode;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String serializeComponent(TextComponent component) {
        String output = "";
        while (component != null) {
            Color color = component.getColor();
            if (color != null) output += charCode + color.getIdentifierChar();
            for (Style style : component.getStyles())
                output += charCode + style.getIdentifierChar();
            output += component.getText();

            component = component.getNext();
        }
        return output;
    }
}
