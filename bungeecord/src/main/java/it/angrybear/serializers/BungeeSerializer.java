package it.angrybear.serializers;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import org.jetbrains.annotations.Nullable;

public class BungeeSerializer extends ComponentSerializer {

    @Override
    public BaseComponent serializeSimpleTextComponent(TextComponent component) {
        return null;
    }

    @Override
    public BaseComponent serializeHoverComponent(HoverComponent component) {
        return null;
    }

    @Override
    public BaseComponent serializeClickComponent(ClickComponent component) {
        return null;
    }

    @Override
    public BaseComponent serializeHexComponent(HexComponent component) {
        String rawText = new SectionSignSerializer().serializeHexComponent(component);
        if (rawText == null) return null;
        BaseComponent[] components = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(rawText);
        return null;
    }

    @Override
    public <T> @Nullable T sumTwoSerializedComponents(T component1, T component2) {
        BaseComponent bc1 = (BaseComponent) component1;
        BaseComponent bc2 = (BaseComponent) component2;
        if (bc1 == null) return null;
        if (bc2 != null) bc1.addExtra(bc2);
        return component1;
    }
}
