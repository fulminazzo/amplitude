package it.angrybear.serializers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.enums.ClickAction;
import it.angrybear.enums.HoverAction;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.ItemTag;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Entity;
import net.md_5.bungee.api.chat.hover.content.Item;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.function.Consumer;

@SuppressWarnings({"unchecked"})
public class BungeeSerializer extends ComponentSerializer {

    @Override
    public BaseComponent serializeSimpleTextComponent(@Nullable TextComponent component) {
        String rawText = getBaseSerializer().serializeSimpleTextComponent(component);
        if (rawText == null) return null;
        return new net.md_5.bungee.api.chat.TextComponent(rawText);
    }

    @Override
    public BaseComponent serializeHoverComponent(@Nullable HoverComponent component) {
        if (component == null) return null;
        BaseComponent comp = serializeSimpleTextComponent(component.getChild());

        HoverAction hoverAction = HoverAction.valueOf(component.getTagOption("action").toUpperCase());
        HoverEvent.Action action = HoverEvent.Action.valueOf(hoverAction.name());

        Content content;
        switch (hoverAction) {
            case SHOW_ITEM: {
                String id = component.getTagOption("id");
                String count = component.getTagOption("Count");
                count = count.substring(0, count.length() - 1);
                String rawTag = component.getTagOption("Tag");
                ItemTag tag = null;
                if (rawTag != null)
                    tag = new ItemTag.Serializer().deserialize(
                           new Gson().fromJson(rawTag, JsonObject.class),null, null);
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
                content = new Text(component.getTagOption("id"));
                break;
            }
            default: {
                content = new Text(component.getTagOption("text"));
            }
        }
        applyForAllComponents(comp, c -> c.setHoverEvent(new HoverEvent(action, content)));

        return comp;
    }

    @Override
    public BaseComponent serializeClickComponent(@Nullable ClickComponent component) {
        if (component == null) return null;
        BaseComponent comp = serializeSimpleTextComponent(component.getChild());

        ClickAction clickAction = ClickAction.valueOf(component.getTagOption("action").toUpperCase());
        ClickEvent.Action action = ClickEvent.Action.valueOf(clickAction.name());

        String requiredOption = new ArrayList<>(clickAction.getRequiredOptions().keySet()).get(0);
        ClickEvent clickEvent = new ClickEvent(action, component.getTagOption(requiredOption));
        applyForAllComponents(comp, c -> c.setClickEvent(clickEvent));

        return comp;
    }

    @Override
    public BaseComponent serializeHexComponent(@Nullable HexComponent component) {
        String rawText = getBaseSerializer().serializeHexComponent(component);
        if (rawText == null) return null;
        BaseComponent[] components = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(rawText);
        BaseComponent comp = components[0];
        for (int i = 1; i < components.length; i++)
            comp.addExtra(components[i]);
        return comp;
    }

    @Override
    public <T> @Nullable T sumTwoSerializedComponents(@Nullable T component1, @Nullable T component2) {
        BaseComponent bc1 = (BaseComponent) component1;
        BaseComponent bc2 = (BaseComponent) component2;
        if (bc1 == null) return null;
        if (bc1.getHoverEvent() != null || bc1.getClickEvent() != null) {
            BaseComponent bc3 = new net.md_5.bungee.api.chat.TextComponent(bc1);
            bc1 = new net.md_5.bungee.api.chat.TextComponent("");
            bc1.addExtra(bc3);
        }
        if (bc2 != null) bc1.addExtra(bc2);
        return (T) bc1;
    }

    private void applyForAllComponents(BaseComponent component, Consumer<BaseComponent> function) {
        if (component == null) return;
        function.accept(component);
        if (component.getExtra() == null) return;
        component.getExtra().forEach(e -> applyForAllComponents(e, function));
    }

    private ComponentSerializer getBaseSerializer() {
        return new SectionSignSerializer();
    }
}
