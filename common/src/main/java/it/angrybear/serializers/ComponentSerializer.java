package it.angrybear.serializers;

import it.angrybear.components.ClickComponent;
import it.angrybear.components.HexComponent;
import it.angrybear.components.HoverComponent;
import it.angrybear.components.TextComponent;
import it.fulminazzo.fulmicollection.utils.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Set;

/**
 * An abstract class that allows creating serializers for text components.
 */
public abstract class ComponentSerializer {

    /**
     * A method to get the first valid ComponentSerializer from the current package.
     * It tries to instantiate every class that is not {@link CharCodeSerializer},
     * {@link AmpersandSerializer} or {@link SectionSignSerializer}.
     * If it fails with every class, return a new {@link SectionSignSerializer};
     *
     * @return the component serializer
     */
    @SuppressWarnings("unchecked")
    public static @NotNull ComponentSerializer serializer() {
        @NotNull Set<Class<?>> classes = ClassUtils.findClassesInPackage(ComponentSerializer.class.getPackage().getName(),
                ComponentSerializer.class);
        for (Class<?> clazz : classes) {
            if (!ComponentSerializer.class.isAssignableFrom(clazz)) continue;
            if (Modifier.isAbstract(clazz.getModifiers())) continue;
            // Using getCanonicalName() to support Spigot reload.
            if (clazz.getCanonicalName().equals(CharCodeSerializer.class.getCanonicalName())) continue;
            if (clazz.getCanonicalName().equals(AmpersandSerializer.class.getCanonicalName())) continue;
            if (clazz.getCanonicalName().equals(SectionSignSerializer.class.getCanonicalName())) continue;
            try {
                Constructor<? extends ComponentSerializer> constructor = (Constructor<? extends ComponentSerializer>) clazz.getConstructor();
                return constructor.newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (Exception ignored) {
            }
        }
        return new SectionSignSerializer();
    }

    /**
     * Serialize a general {@link TextComponent} and its siblings.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the output
     */
    public <T> @Nullable T serializeComponent(@Nullable TextComponent component) {
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

    /**
     * Send to player.
     *
     * @param <P>       the type parameter
     * @param player    the player
     * @param component the component
     */
    public <P> void send(P player, TextComponent component) {
        if (player == null) return;
        if (component == null) return;
        send(player, serializeComponent(component));
    }

    /**
     * Send to player.
     *
     * @param <T>       the type parameter
     * @param <P>       the type parameter
     * @param player    the player
     * @param component the component
     */
    public abstract <T, P> void send(P player, T component);
}