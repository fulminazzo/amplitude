package it.angrybear.serializers;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.exceptions.InvalidOptionException;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract class that allows creating serializers for text components.
 */
public abstract class ComponentSerializer {
    static ComponentSerializer DEFAULT_SERIALIZER;

    public static ComponentSerializer getSerializer() {
        if (DEFAULT_SERIALIZER == null) return new SectionSignSerializer();
        else return DEFAULT_SERIALIZER;
    }

    /**
     * Serialize a general {@link TextComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the @ nullable t
     */
    public abstract <T> @Nullable T serializeComponent(TextComponent component);

    /**
     * Serialize a {@link HoverComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the @ nullable t
     * @throws InvalidOptionException the invalid option exception
     */
    public abstract <T> @Nullable T serializeHoverComponent(HoverComponent component) throws InvalidOptionException;

    /**
     * Serialize a {@link ClickComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the @ nullable t
     * @throws InvalidOptionException the invalid option exception
     */
    public abstract <T> @Nullable T serializeClickComponent(ClickComponent component) throws InvalidOptionException;

    /**
     * Serialize a {@link HexComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the @ nullable t
     * @throws InvalidOptionException the invalid option exception
     */
    public abstract <T> @Nullable T serializeHexComponent(HexComponent component) throws InvalidOptionException;
}