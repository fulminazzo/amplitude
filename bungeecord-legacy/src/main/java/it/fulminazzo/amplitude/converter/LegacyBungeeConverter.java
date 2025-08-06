package it.fulminazzo.amplitude.converter;

import it.fulminazzo.amplitude.component.*;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Implementation of {@link ComponentConverter} for Minecraft 1.16 and below.
 * It supports every fork of <a href="https://www.spigotmc.org/wiki/bungeecord/">BungeeCord</a> and <a href="https://www.spigotmc.org/wiki/spigot/">Spigot</a>,
 * since it uses the BungeeCord API to create components.
 * <p>
 * Hex colors are <b>disabled</b>.
 */
@SuppressWarnings({"unchecked"})
@Getter
@Setter
public class LegacyBungeeConverter extends ComponentConverter {
    private boolean showingHex = false;

    @Override
    public @Nullable BaseComponent convertSimpleComponent(@Nullable Component component) {
        correctComponents(component);
        if (component == null) return null;
        BaseComponent c = new TextComponent(component.getText());
        if (component.isReset()) c = reset(c);
        else {
            Color color = component.getColor();
            if (color != null) c = applyColor(c, color);
            Font font = component.getFont();
            if (font != null) c = applyFont(c, font);
            for (Style style : component.getStyles()) c = applyStyle(c, style, component.getStyle(style));
        }
        return c;
    }

    @Override
    public @Nullable BaseComponent convertHoverComponent(@Nullable HoverComponent component) {
        correctComponents(component);
        if (component == null) return null;
        BaseComponent comp = convertComponent(component.getChild());
        if (comp == null) comp = new TextComponent();

        HoverAction hoverAction = HoverAction.valueOf(component.getTagOption("action").toUpperCase());
        HoverEvent.Action action = HoverEvent.Action.valueOf(hoverAction.name());

        BaseComponent content;
        switch (hoverAction) {
            case SHOW_ITEM: {
                String id = component.getTagOption("id");
                String count = component.getTagOption("Count");
                String rawTag = component.getTagOption("Tag");
                if (rawTag == null || rawTag.isEmpty()) rawTag = component.getTagOption("tag");
                if (rawTag == null) rawTag = "";
                else rawTag = ",tag:" + rawTag;
                content = new TextComponent(String.format("{Count:%s,id:\"%s\"%s}", count, id, rawTag));
                break;
            }
            case SHOW_ENTITY: {
                String type = component.getTagOption("type");
                String id = component.getTagOption("id");
                String name = component.getTagOption("name");
                content = new TextComponent(String.format("{type:%s,id:\"%s\",name:%s}", type, id, name));
                break;
            }
            case SHOW_ACHIEVEMENT: {
                String id = component.getTagOption("id");
                if (!id.startsWith("achievement.")) id = "achievement." + id;
                content = new TextComponent(id);
                break;
            }
            default: {
                String raw = component.getTagOption("text");
                raw = new SectionSignConverter().convertComponent(Component.fromRaw(raw));
                content = new TextComponent(raw);
            }
        }
        applyForAllComponents(comp, c -> c.setHoverEvent(new HoverEvent(action, new BaseComponent[]{content})));

        BaseComponent tmp = new TextComponent();
        tmp.addExtra(comp);
        return tmp;
    }

    @Override
    public @Nullable BaseComponent convertClickComponent(@Nullable ClickComponent component) {
        correctComponents(component);
        if (component == null) return null;
        BaseComponent comp = convertComponent(component.getChild());
        if (comp == null) comp = new TextComponent();

        ClickAction clickAction = ClickAction.valueOf(component.getTagOption("action").toUpperCase());
        ClickEvent.Action action = ClickEvent.Action.valueOf(clickAction.name());

        String requiredOption = new ArrayList<>(clickAction.getRequiredOptions().keySet()).get(0);
        ClickEvent clickEvent = new ClickEvent(action, component.getTagOption(requiredOption));
        applyForAllComponents(comp, c -> c.setClickEvent(clickEvent));

        BaseComponent tmp = new TextComponent();
        tmp.addExtra(comp);
        return tmp;
    }

    @Override
    public @Nullable BaseComponent convertHexComponent(@Nullable HexComponent component) {
        correctComponents(component);
        if (component == null) return null;
        String rawText = component.getText();
        if (rawText == null) rawText = "";
        if (showingHex) rawText = component.getHexColor() + rawText;
        return new TextComponent(rawText);
    }

    @Override
    public @Nullable BaseComponent convertInsertionComponent(@Nullable InsertionComponent component) {
        correctComponents(component);
        if (component == null) return null;
        BaseComponent comp = convertComponent(component.getChild());
        if (comp == null) comp = new TextComponent();
        comp.setInsertion(component.getInsertionText());
        return comp;
    }

    @Override
    public @Nullable BaseComponent convertFontComponent(@Nullable FontComponent component) {
        correctComponents(component);
        if (component == null) return null;
        BaseComponent c = convertSimpleComponent(component);
        return applyFont(c, Font.valueOf(component.getFontID()));
    }

    @Override
    public @Nullable BaseComponent convertTranslateComponent(@Nullable TranslatableComponent component) {
        correctComponents(component);
        if (component == null) return null;
        final String rawText;
        final Component child = component.getChild();
        if (child == null) rawText = "";
        else rawText = child.serialize();

        net.md_5.bungee.api.chat.TranslatableComponent c = new net.md_5.bungee.api.chat.TranslatableComponent(rawText);
        c.setWith(component.getArguments().stream()
                .map(this::convertComponent)
                .map(bc -> bc == null ? new Component() : bc)
                .filter(bc -> bc instanceof BaseComponent)
                .map(bc -> (BaseComponent) bc)
                .collect(Collectors.toList()));

        if (component.isReset()) c = reset(c);
        else {
            Color color = component.getColor();
            if (color != null) c = applyColor(c, color);
            Font font = component.getFont();
            if (font != null) c = applyFont(c, font);
            for (Style style : component.getStyles()) c = applyStyle(c, style, component.getStyle(style));
        }
        return c;
    }

    @Override
    public <T> @Nullable T sumTwoConvertedComponents(@Nullable T component1, @Nullable T component2) {
        BaseComponent bc1 = (BaseComponent) component1;
        BaseComponent bc2 = (BaseComponent) component2;
        if (bc1 == null) return null;
        if (bc1 instanceof TextComponent) {
            TextComponent tc1 = (TextComponent) bc1;
            if (tc1.getText() == null || tc1.getText().isEmpty()) tc1.setText("");
        }
        if (bc2 instanceof TextComponent) {
            TextComponent tc2 = (TextComponent) bc2;
            if (tc2.getText() == null) tc2.setText("");
        }
        if (bc2 != null) bc1.addExtra(bc2);
        return (T) bc1;
    }

    @Override
    public <T> @Nullable T applyColor(@Nullable T component, @NotNull Color color) {
        if (component == null) return null;
        BaseComponent c = (BaseComponent) component;
        ChatColor chatColor;
        if (color.isCustom()) return component;
        else chatColor = ChatColor.valueOf(color.name());
        c.setColor(chatColor);
        return component;
    }

    @Override
    public <T> @Nullable T applyStyle(@Nullable T component, @NotNull Style style, @Nullable Boolean value) {
        if (component == null) return null;
        BaseComponent c = (BaseComponent) component;
        setStyle(c, style, value != null && value);
        return component;
    }

    @Override
    public <T> @Nullable T applyFont(@Nullable T component, @NotNull Font font) {
        return component;
    }

    /**
     * Sets style.
     *
     * @param component the component
     * @param style     the style
     * @param value     the value
     */
    protected void setStyle(@NotNull BaseComponent component, @NotNull Style style, boolean value) {
        String methodName = style.getName();
        methodName = methodName.substring(0, 1).toUpperCase() + methodName.substring(1).toLowerCase();
        try {
            Method method = component.getClass().getMethod("set" + methodName, Boolean.class);
            method.invoke(component, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Apply for all components.
     *
     * @param component the component
     * @param function  the function
     */
    protected void applyForAllComponents(@Nullable BaseComponent component, @NotNull Consumer<BaseComponent> function) {
        if (component == null) return;
        function.accept(component);
        if (component.getExtra() == null) return;
        component.getExtra().forEach(e -> applyForAllComponents(e, function));
    }

    @Override
    public <T, P> void send(@Nullable P player, @Nullable T component) {
        if (player == null) return;
        if (component == null) return;
        try {
            // BungeeCord
            try {
                Class<?> clazz = Class.forName("net.md_5.bungee.api.console.ProxiedConsole");
                if (!clazz.isAssignableFrom(player.getClass())) {
                    clazz = Class.forName("net.md_5.bungee.api.connection.ProxiedPlayer");
                    if (!clazz.isAssignableFrom(player.getClass()))
                        throw new Exception(String.format("%s is not a %s", player, clazz.getCanonicalName()));
                    Method sendMessage = player.getClass().getMethod("sendMessage", BaseComponent.class);
                    sendMessage.invoke(player, component);
                } else {
                    Method sendMessage = player.getClass().getMethod("sendMessage", String.class);
                    sendMessage.setAccessible(true);
                    sendMessage.invoke(player, ((BaseComponent) component).toLegacyText());
                }
                return;
            } catch (ClassNotFoundException ignored) {
            }

            // Spigot
            try {
                Class<?> clazz = Class.forName("org.bukkit.command.ConsoleCommandSender");
                if (!clazz.isAssignableFrom(player.getClass())) {
                    clazz = Class.forName("org.bukkit.entity.Player");
                    if (!clazz.isAssignableFrom(player.getClass()))
                        throw new Exception(String.format("%s is not a %s", player, clazz.getCanonicalName()));
                    Method spigotMethod = player.getClass().getMethod("spigot");
                    Object spigot = spigotMethod.invoke(player);
                    Method sendMessage = spigot.getClass().getMethod("sendMessage", BaseComponent.class);
                    sendMessage.setAccessible(true);
                    sendMessage.invoke(spigot, component);
                } else {
                    Method sendMessage = player.getClass().getMethod("sendMessage", String.class);
                    sendMessage.setAccessible(true);
                    sendMessage.invoke(player, ((BaseComponent) component).toLegacyText());
                }
                return;
            } catch (ClassNotFoundException ignored) {
            }

            throw new Exception("Platform not recognized: this serializer works only on BungeeCord or Spigot.");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Corrects the current component by removing any Hex color assignation from {@link HexComponent}.
     * This passage is mandatory and executed for every serialization method as in 1.15 and below Hex colors were NOT supported.
     *
     * @param component the component to correct
     */
    protected void correctComponents(@Nullable Component component) {
        if (component == null) return;
        Component c1 = component.getNext();
        if (!(c1 instanceof HexComponent)) return;
        Component c2 = c1.getNext();
        if (c2 == null) return;
        if (!Objects.equals(c2.getColor(), c1.getColor())) return;
        c2.setColor(component.getColor());
    }

}
