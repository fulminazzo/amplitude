package it.angrybear.serializers;

import it.angrybear.component.HexComponent;
import it.angrybear.component.HoverComponent;
import it.angrybear.component.TextComponent;
import it.angrybear.component.Color;
import it.angrybear.component.Font;
import it.angrybear.component.HoverAction;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Implementation of {@link ComponentSerializer} for Minecraft 1.16 and above.
 * It uses {@link LegacyBungeeSerializer} for most of its methods, but rewrites the following:
 * <ul>
 *     <li>implements Hex colors in {@link #serializeHexComponent(HexComponent)}</li>
 *     <li>uses new {@link Content} objects to create {@link HoverEvent}</li>
 *     <li>removes {@link #correctComponents(TextComponent)} function to allow for Hex colors</li>
 * </ul>
 */
public class BungeeSerializer extends LegacyBungeeSerializer {

    @Override
    public BaseComponent serializeHoverComponent(@Nullable HoverComponent component) {
        correctComponents(component);
        if (component == null) return null;
        BaseComponent comp = serializeComponent(component.getChild());

        HoverAction hoverAction = HoverAction.valueOf(component.getTagOption("action").toUpperCase());
        HoverEvent.Action action = HoverEvent.Action.valueOf(hoverAction.name());

        Content content;
        switch (hoverAction) {
            case SHOW_ITEM: {
                String id = component.getTagOption("id");
                String count = component.getTagOption("Count");
                count = count.substring(0, count.length() - 1);
                String rawTag = component.getTagOption("Tag");
                if (rawTag == null || rawTag.isEmpty()) rawTag = component.getTagOption("tag");
                ItemTag tag = null;
                if (rawTag != null) tag = ItemTag.ofNbt(rawTag);
                content = new Item(id, Integer.parseInt(count), tag);
                break;
            }
            case SHOW_ENTITY: {
                String type = component.getTagOption("type");
                String id = component.getTagOption("id");
                String name = component.getTagOption("name");
                content = new Entity(type, id, new net.md_5.bungee.api.chat.TextComponent(name));
                break;
            }
            case SHOW_ACHIEVEMENT: {
                String id = component.getTagOption("id");
                if (!id.startsWith("achievement.")) id = "achievement." + id;
                content = new Text(id);
                break;
            }
            default: {
                content = new Text(component.getTagOption("text"));
            }
        }
        applyForAllComponents(comp, c -> c.setHoverEvent(new HoverEvent(action, content)));

        BaseComponent tmp = new net.md_5.bungee.api.chat.TextComponent();
        tmp.addExtra(comp);
        return tmp;
    }

    @Override
    public BaseComponent serializeHexComponent(@Nullable HexComponent component) {
        if (component == null) return null;
        BaseComponent comp = new net.md_5.bungee.api.chat.TextComponent(component.getText());
        return applyColor(comp, component.getColor());
    }

    @Override
    public <T> @Nullable T applyColor(@Nullable T component, @NotNull Color color) {
        if (component == null) return null;
        BaseComponent c = (BaseComponent) component;
        ChatColor chatColor = ChatColor.of(color.isCustom() ? color.getCode() : color.getName());
        c.setColor(chatColor);
        return component;
    }

    @Override
    public <T> @Nullable T applyFont(@Nullable T component, @NotNull Font font) {
        if (component == null) return null;
        BaseComponent c = (BaseComponent) component;
        c.setFont(font.name().toLowerCase());
        return component;
    }

    @Override
    protected void correctComponents(TextComponent component) {

    }
}
