package it.angrybear.serializers;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.enums.ClickAction;
import it.angrybear.enums.Color;
import it.angrybear.enums.HoverAction;
import it.angrybear.enums.Style;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked"})
@Getter
@Setter
public class LegacyBungeeSerializer extends ComponentSerializer {
    private boolean showingHex = false;

    @Override
    public BaseComponent serializeSimpleTextComponent(@Nullable TextComponent component) {
        correctComponents(component);
        if (component == null) return null;
        BaseComponent c = new net.md_5.bungee.api.chat.TextComponent(component.getText());
        if (component.getReset()) c = reset(c);
        else {
            Color color = component.getColor();
            if (color != null) c = applyColor(c, color);
            for (Style style : component.getStyles()) c = applyStyle(c, style, component.getStyle(style));
        }
        return c;
    }

    @Override
    public BaseComponent serializeHoverComponent(@Nullable HoverComponent component) {
        correctComponents(component);
        if (component == null) return null;
        BaseComponent comp = serializeComponent(component.getChild());

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
                content = new net.md_5.bungee.api.chat.TextComponent(String.format("{Count:%s,id:\"%s\"%s}", count, id, rawTag));
                break;
            }
            case SHOW_ENTITY: {
                String type = component.getTagOption("type");
                String id = component.getTagOption("id");
                String name = component.getTagOption("name");
                content = new net.md_5.bungee.api.chat.TextComponent(String.format("{type:%s,id:\"%s\",name:%s}", type, id, name));
                break;
            }
            case SHOW_ACHIEVEMENT: {
                String id = component.getTagOption("id");
                if (!id.startsWith("achievement.")) id = "achievement." + id;
                content = new net.md_5.bungee.api.chat.TextComponent(id);
                break;
            }
            default: {
                content = new net.md_5.bungee.api.chat.TextComponent(component.getTagOption("text"));
            }
        }
        applyForAllComponents(comp, c -> c.setHoverEvent(new HoverEvent(action, new BaseComponent[]{content})));

        BaseComponent tmp = new net.md_5.bungee.api.chat.TextComponent();
        tmp.addExtra(comp);
        return tmp;
    }

    @Override
    public BaseComponent serializeClickComponent(@Nullable ClickComponent component) {
        correctComponents(component);
        if (component == null) return null;
        BaseComponent comp = serializeComponent(component.getChild());

        ClickAction clickAction = ClickAction.valueOf(component.getTagOption("action").toUpperCase());
        ClickEvent.Action action = ClickEvent.Action.valueOf(clickAction.name());

        String requiredOption = new ArrayList<>(clickAction.getRequiredOptions().keySet()).get(0);
        ClickEvent clickEvent = new ClickEvent(action, component.getTagOption(requiredOption));
        applyForAllComponents(comp, c -> c.setClickEvent(clickEvent));

        BaseComponent tmp = new net.md_5.bungee.api.chat.TextComponent();
        tmp.addExtra(comp);
        return tmp;
    }

    @Override
    public BaseComponent serializeHexComponent(@Nullable HexComponent component) {
        correctComponents(component);
        if (component == null) return null;
        String rawText = component.getText();
        if (rawText == null) rawText = "";
        if (showingHex) rawText = component.getHexColor() + rawText;
        return new net.md_5.bungee.api.chat.TextComponent(rawText);
    }

    @Override
    public <T> @Nullable T sumTwoSerializedComponents(@Nullable T component1, @Nullable T component2) {
        BaseComponent bc1 = (BaseComponent) component1;
        BaseComponent bc2 = (BaseComponent) component2;
        if (bc1 == null) return null;
        if (bc1 instanceof net.md_5.bungee.api.chat.TextComponent) {
            net.md_5.bungee.api.chat.TextComponent tc1 = (net.md_5.bungee.api.chat.TextComponent) bc1;
            if (tc1.getText() == null) tc1.setText("");
        }
        if (bc2 instanceof net.md_5.bungee.api.chat.TextComponent) {
            net.md_5.bungee.api.chat.TextComponent tc2 = (net.md_5.bungee.api.chat.TextComponent) bc2;
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

    @Override
    public <T> @Nullable T reset(@Nullable T component) {
        if (component == null) return null;
        BaseComponent c = (BaseComponent) component;
        c.setColor(ChatColor.WHITE);
        c.setBold(false);
        c.setStrikethrough(false);
        c.setItalic(false);
        c.setObfuscated(false);
        c.setUnderlined(false);
        return component;
    }

    protected void applyForAllComponents(BaseComponent component, Consumer<BaseComponent> function) {
        if (component == null) return;
        function.accept(component);
        if (component.getExtra() == null) return;
        component.getExtra().forEach(e -> applyForAllComponents(e, function));
    }

    @Override
    public <T, P> void send(P player, T component) {
        if (player == null) return;
        if (component == null) return;
        try {
            // BungeeCord
            try {
                final Class<?> clazz = Class.forName("net.md_5.bungee.api.connection.ProxiedPlayer");
                if (!clazz.isAssignableFrom(player.getClass()))
                    throw new Exception(String.format("%s is not a %s", player, clazz.getCanonicalName()));
                Method sendMessage = player.getClass().getMethod("sendMessage", BaseComponent.class);
                sendMessage.invoke(player, component);
                return;
            } catch (ClassNotFoundException ignored) {}

            // Spigot
            try {
                final Class<?> clazz = Class.forName("org.bukkit.entity.Player");
                if (!clazz.isAssignableFrom(player.getClass()))
                    throw new Exception(String.format("%s is not a %s", player, clazz.getCanonicalName()));
                Method spigotMethod = player.getClass().getMethod("spigot");
                Object spigot = spigotMethod.invoke(player);
                Method sendMessage = spigot.getClass().getMethod("sendMessage", BaseComponent.class);
                sendMessage.setAccessible(true);
                sendMessage.invoke(spigot, component);
                return;
            } catch (ClassNotFoundException ignored) {}

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
    protected void correctComponents(TextComponent component) {
        if (component == null) return;
        TextComponent c1 = component.getNext();
        if (!(c1 instanceof HexComponent)) return;
        TextComponent c2 = c1.getNext();
        if (c2 == null) return;
        if (!Objects.equals(c2.getColor(), c1.getColor())) return;
        c2.setColor(component.getColor());
    }
}
