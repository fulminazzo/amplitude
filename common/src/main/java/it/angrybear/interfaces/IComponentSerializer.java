package it.angrybear.interfaces;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.exceptions.InvalidOptionException;
import org.jetbrains.annotations.Nullable;

public interface IComponentSerializer {

    <T> @Nullable T serializeComponent(@Nullable TextComponent component);

    <T> @Nullable T serializeHoverComponent(@Nullable HoverComponent component) throws InvalidOptionException;

    <T> @Nullable T serializeClickComponent(@Nullable ClickComponent component) throws InvalidOptionException;

    <T> @Nullable T serializeHexComponent(@Nullable HexComponent component) throws InvalidOptionException;
}