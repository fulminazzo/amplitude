package it.angrybear.serializers;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract class that allows creating serializers for text components.
 */
public abstract class ComponentSerializer {

    /**
     * Serialize a general {@link TextComponent} and its siblings.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the output
     */
    public <T> @Nullable T serializeComponent(TextComponent component) {
        if (component == null) return null;
        T output;
        if (component instanceof HoverComponent)
            output = serializeHoverComponent((HoverComponent) component);
        else if (component instanceof ClickComponent)
            output = serializeClickComponent((ClickComponent) component);
        else if (component instanceof HexComponent)
            output = serializeHexComponent((HexComponent) component);
        else output = serializeSimpleTextComponent(component);

        if (component.getNext() != null)
            output = sumTwoSerializedComponents(output, serializeComponent(component.getNext()));

        return output;
    }

    /**
     * Serialize a {@link TextComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T serializeSimpleTextComponent(TextComponent component);

    /**
     * Serialize a {@link HoverComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T serializeHoverComponent(HoverComponent component);

    /**
     * Serialize a {@link ClickComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T serializeClickComponent(ClickComponent component);

    /**
     * Serialize a {@link HexComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T serializeHexComponent(HexComponent component);

    /**
     * Sum two serialized components.
     *
     * @param <T>        the type parameter
     * @param component1 the first component
     * @param component2 the second component
     * @return the result component
     */
    public abstract <T> @Nullable T sumTwoSerializedComponents(T component1, T component2);
}