package it.fulminazzo.amplitude.converter;

import it.fulminazzo.amplitude.component.*;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ComponentConverter} that supports the <a href="https://docs.advntr.dev/index.html">Adventure API</a>.
 * This means
 * that it can be used in every version of <a href="https://papermc.io/software/paper">PaperMC</a> from 1.16 and in <a href="https://papermc.io/software/velocity">Velocity</a>.
 */
@SuppressWarnings("unchecked")
public final class AdventureConverter extends ComponentConverter {

    @Override
    public @Nullable net.kyori.adventure.text.Component convertSimpleComponent(@Nullable Component component) {
        if (component == null) return null;
        String rawText = component.getText();
        if (rawText == null) return null;
        net.kyori.adventure.text.Component c = net.kyori.adventure.text.Component.text(rawText);
        if (component.isReset()) c = reset(c);
        else {
            Color color = component.getColor();
            if (color != null) c = applyColor(c, color);
            Font font = component.getFont();
            if (font != null) c = applyFont(c, font);
            for (Style style : component.getStyles())
                c = applyStyle(c, style, component.getStyle(style));
        }
        return c;
    }

    @Override
    public @Nullable net.kyori.adventure.text.Component convertHoverComponent(@Nullable HoverComponent component) {
        if (component == null) return null;
        net.kyori.adventure.text.Component c = convertComponent(component.getChild());
        if (c == null) c = net.kyori.adventure.text.Component.empty();

        HoverAction hoverAction = HoverAction.valueOf(component.getTagOption("action").toUpperCase());

        HoverEvent<?> hoverEvent;
        switch (hoverAction) {
            case SHOW_ITEM: {
                String id = component.getTagOption("id");
                String count = component.getTagOption("Count");
                count = count.substring(0, count.length() - 1);
                String rawTag = component.getTagOption("Tag");
                if (rawTag == null || rawTag.isEmpty()) rawTag = component.getTagOption("tag");
                if (rawTag == null || rawTag.isEmpty())
                    hoverEvent = HoverEvent.showItem(Key.key(id), Integer.parseInt(count));
                else hoverEvent = HoverEvent.showItem(Key.key(id), Integer.parseInt(count), BinaryTagHolder.binaryTagHolder(rawTag));
                break;
            }
            case SHOW_ENTITY: {
                String type = component.getTagOption("type");
                String id = component.getTagOption("id");
                String name = component.getTagOption("name");
                hoverEvent = HoverEvent.showEntity(Key.key(type), UUID.fromString(id), net.kyori.adventure.text.Component.text(name));
                break;
            }
            case SHOW_ACHIEVEMENT: {
                String id = component.getTagOption("id");
                if (!id.startsWith("achievement.")) id = "achievement." + id;
                hoverEvent = HoverEvent.showAchievement(id);
                break;
            }
            default: {
                hoverEvent = HoverEvent.showText(net.kyori.adventure.text.Component.text(component.getTagOption("text")));
            }
        }

        return c.hoverEvent(hoverEvent);
    }

    @Override
    public @Nullable net.kyori.adventure.text.Component convertClickComponent(@Nullable ClickComponent component) {
        if (component == null) return null;
        net.kyori.adventure.text.Component c = convertComponent(component.getChild());
        if (c == null) c = net.kyori.adventure.text.Component.empty();

        ClickAction clickAction = ClickAction.valueOf(component.getTagOption("action").toUpperCase());
        ClickEvent.Action action = ClickEvent.Action.valueOf(clickAction.name());

        String requiredOption = new ArrayList<>(clickAction.getRequiredOptions().keySet()).get(0);
        ClickEvent clickEvent = ClickEvent.clickEvent(action, component.getTagOption(requiredOption));

        return c.clickEvent(clickEvent);
    }

    @Override
    public @Nullable net.kyori.adventure.text.Component convertHexComponent(@Nullable HexComponent component) {
        if (component == null) return null;
        net.kyori.adventure.text.Component c = convertSimpleComponent(component);
        if (c == null) return null;
        return c.color(TextColor.fromHexString(component.getHexColor()));
    }

    @Override
    public @Nullable net.kyori.adventure.text.Component convertInsertionComponent(@Nullable InsertionComponent component) {
        if (component == null) return null;
        net.kyori.adventure.text.Component c = convertComponent(component.getChild());
        if (c == null) c = net.kyori.adventure.text.Component.empty();
        return c.insertion(component.getInsertionText());
    }

    @Override
    public @Nullable net.kyori.adventure.text.Component convertFontComponent(@Nullable FontComponent component) {
        if (component == null) return null;
        net.kyori.adventure.text.Component c = convertSimpleComponent(component);
        if (c == null) c = net.kyori.adventure.text.Component.empty();
        return c.font(Key.key(component.getFontID().toLowerCase()));
    }

    @Override
    public @Nullable net.kyori.adventure.text.Component convertTranslateComponent(TranslatableComponent component) {
        if (component == null) return null;
        final String rawText;
        final Component child = component.getChild();
        if (child == null) rawText = "";
        else rawText = child.serialize();

        net.kyori.adventure.text.TranslatableComponent c = net.kyori.adventure.text.Component.translatable(rawText);
        c = c.args(component.getArguments().stream()
                .map(this::convertComponent)
                .map(bc -> bc == null ? net.kyori.adventure.text.Component.empty() : bc)
                .map(bc -> (net.kyori.adventure.text.Component) bc)
                .collect(Collectors.toList())
        );

        if (component.isReset()) c = reset(c);
        else {
            Color color = component.getColor();
            if (color != null) c = applyColor(c, color);
            Font font = component.getFont();
            if (font != null) c = applyFont(c, font);
            for (Style style : component.getStyles())
                c = applyStyle(c, style, component.getStyle(style));
        }

        return c;
    }

    @Override
    public <T> @Nullable T sumTwoConvertedComponents(@Nullable T component1, @Nullable T component2) {
        net.kyori.adventure.text.Component tc1 = (net.kyori.adventure.text.Component) component1;
        net.kyori.adventure.text.Component tc2 = (net.kyori.adventure.text.Component) component2;
        if (tc1 == null) return null;
        if (tc1.equals(net.kyori.adventure.text.Component.empty())) return (T) tc2;
        if (tc2 != null) tc1 = tc1.append(tc2);
        return (T) tc1;
    }

    @Override
    public <T> @Nullable T applyColor(@Nullable T component, @NotNull Color color) {
        if (component == null) return null;
        net.kyori.adventure.text.Component c = (net.kyori.adventure.text.Component) component;
        return (T) c.color(getColor(color));
    }

    @Override
    public <T> @Nullable T applyStyle(@Nullable T component, @NotNull Style style, Boolean value) {
        if (component == null) return null;
        net.kyori.adventure.text.Component c = (net.kyori.adventure.text.Component) component;
        return (T) c.decoration(TextDecoration.valueOf(style.name()), value);
    }

    @Override
    public <T> @Nullable T applyFont(@Nullable T component, @NotNull Font font) {
        if (component == null) return null;
        net.kyori.adventure.text.Component c = (net.kyori.adventure.text.Component) component;
        return (T) c.font(Key.key(font.name().toLowerCase()));
    }

    @Override
    public <T, P> void send(@Nullable P player, @Nullable T component) {
        if (player == null) return;
        if (component == null) return;
        try {
            try {
                final Class<?> clazz = Class.forName("net.kyori.adventure.audience.Audience");
                if (!clazz.isAssignableFrom(player.getClass()))
                    throw new Exception(String.format("%s is not a %s", player, clazz.getCanonicalName()));
                Method sendMessage = player.getClass().getMethod("sendMessage", net.kyori.adventure.text.Component.class);
                sendMessage.setAccessible(true);
                sendMessage.invoke(player, component);
                return;
            } catch (ClassNotFoundException ignored) {}

            throw new Exception("Platform not recognized: this serializer works only on BungeeCord or Spigot.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private @Nullable TextColor getColor(@NotNull Color color) {
        for (Field field : NamedTextColor.class.getFields())
            if (field.getName().equals(color.name())) {
                try {
                    return (NamedTextColor) field.get(NamedTextColor.class);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        return TextColor.fromHexString(color.getCode());
    }

}