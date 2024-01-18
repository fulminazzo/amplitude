package it.angrybear.serializers;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

@SuppressWarnings("unchecked")
public class AdventureSerializer extends ComponentSerializer {

    @Override
    public @Nullable Component serializeSimpleTextComponent(TextComponent component) {
        String rawText = component.getText();
        if (rawText == null) return null;
        Component textComponent = Component.text(rawText);
        Color color = component.getColor();
        if (color != null) textComponent = textComponent.color(getColor(color));
        for (Style style : component.getStyles())
            if (!style.equals(Style.RESET))
                textComponent.decoration(TextDecoration.valueOf(style.name()));
        return textComponent;
    }

    @Override
    public <T> @Nullable T serializeHoverComponent(HoverComponent component) {
        return null;
    }

    @Override
    public <T> @Nullable T serializeClickComponent(ClickComponent component) {
        return null;
    }

    @Override
    public @Nullable Component serializeHexComponent(HexComponent component) {
        Component textComponent = serializeSimpleTextComponent(component);
        if (textComponent == null) return null;
        return textComponent.color(TextColor.fromHexString(component.getHexColor()));
    }

    @Override
    public <T> @Nullable T sumTwoSerializedComponents(T component1, T component2) {
        Component tc1 = (Component) component1;
        Component tc2 = (Component) component2;
        if (tc1 == null) return null;
        if (tc2 != null) tc1 = tc1.append(tc1);
        return (T) tc1;
    }

    @Override
    public <T, P> void send(P player, T component) {

    }

    private NamedTextColor getColor(Color color) {
        for (Field field : NamedTextColor.class.getFields())
            if (field.getName().equals(color.name())) {
                try {
                    return (NamedTextColor) field.get(NamedTextColor.class);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        return null;
    }
}