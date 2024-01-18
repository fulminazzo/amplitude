package it.angrybear.serializers;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.enums.ClickAction;
import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class AdventureSerializer extends ComponentSerializer {

    @Override
    public @Nullable Component serializeSimpleTextComponent(@Nullable TextComponent component) {
        if (component == null) return null;
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
    public <T> @Nullable T serializeHoverComponent(@Nullable HoverComponent component) {
        if (component == null) return null;
        return null;
    }

    @Override
    public @Nullable Component serializeClickComponent(@Nullable ClickComponent component) {
        if (component == null) return null;
        Component c = serializeComponent(component.getChild());
        if (c == null) c = Component.text("");

        ClickAction clickAction = ClickAction.valueOf(component.getTagOption("action").toUpperCase());
        ClickEvent.Action action = ClickEvent.Action.valueOf(clickAction.name());

        String requiredOption = new ArrayList<>(clickAction.getRequiredOptions().keySet()).get(0);
        ClickEvent clickEvent = ClickEvent.clickEvent(action, component.getTagOption(requiredOption));

        return c.clickEvent(clickEvent);
    }

    @Override
    public @Nullable Component serializeHexComponent(@Nullable HexComponent component) {
        if (component == null) return null;
        Component textComponent = serializeSimpleTextComponent(component);
        if (textComponent == null) return null;
        return textComponent.color(TextColor.fromHexString(component.getHexColor()));
    }

    @Override
    public <T> @Nullable T sumTwoSerializedComponents(@Nullable T component1, @Nullable T component2) {
        Component tc1 = (Component) component1;
        Component tc2 = (Component) component2;
        if (tc1 == null) return null;
        if (tc2 != null) tc1 = tc1.append(tc1);
        return (T) tc1;
    }

    @Override
    public <T, P> void send(@Nullable P player, @Nullable T component) {

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