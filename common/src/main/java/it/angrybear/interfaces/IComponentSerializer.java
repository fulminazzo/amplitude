package it.angrybear.interfaces;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.exceptions.InvalidOptionException;

public interface IComponentSerializer {

    <T> T serializeComponent(TextComponent component);

    <T> T serializeHoverComponent(HoverComponent component) throws InvalidOptionException;

    <T> T serializeClickComponent(ClickComponent component) throws InvalidOptionException;
}