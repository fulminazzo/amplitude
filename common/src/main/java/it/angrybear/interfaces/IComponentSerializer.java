package it.angrybear.interfaces;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.exceptions.InvalidOptionException;
import org.jetbrains.annotations.Nullable;

/**
 * An interface that allows creating serializers for text components.
 */
public interface IComponentSerializer {

    /**
     * Serialize a general {@link TextComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the @ nullable t
     */
    <T> @Nullable T serializeComponent(@Nullable TextComponent component);

    /**
     * Serialize a {@link HoverComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the @ nullable t
     * @throws InvalidOptionException the invalid option exception
     */
    <T> @Nullable T serializeHoverComponent(@Nullable HoverComponent component) throws InvalidOptionException;

    /**
     * Serialize a {@link ClickComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the @ nullable t
     * @throws InvalidOptionException the invalid option exception
     */
    <T> @Nullable T serializeClickComponent(@Nullable ClickComponent component) throws InvalidOptionException;

    /**
     * Serialize a {@link HexComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the @ nullable t
     * @throws InvalidOptionException the invalid option exception
     */
    <T> @Nullable T serializeHexComponent(@Nullable HexComponent component) throws InvalidOptionException;
}