package it.angrybear.interfaces;

import it.angrybear.components.TextComponent;

public interface IComponentSerializer {

    <T> T serializeComponent(TextComponent component);
}