package it.angrybear.serializer;

import it.angrybear.component.*;
import it.angrybear.component.Color;
import it.angrybear.component.Font;
import it.angrybear.component.Style;
import it.fulminazzo.fulmicollection.utils.ClassUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Stream;

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
        @NotNull Set<Class<?>> classes = ClassUtils.findClassesInPackage(ComponentSerializer.class.getPackage().getName());
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
     * Get a new section sign serializer.
     *
     * @return the section sign serializer
     */
    public static @NotNull SectionSignSerializer sectionSign() {
        return new SectionSignSerializer();
    }

    /**
     * Get a new ampersand serializer.
     *
     * @return the ampersand serializer
     */
    public static @NotNull AmpersandSerializer ampersand() {
        return new AmpersandSerializer();
    }

    /**
     * Serialize a general {@link TextComponent} and its siblings.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the output
     */
    @SuppressWarnings("unchecked")
    public <T> @Nullable T serializeComponent(@Nullable TextComponent component) {
        if (component == null) return null;

        Method method = findSerializeMethod(component);
        T output;
        try {
            output = (T) method.invoke(this, component);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            else throw new RuntimeException(cause);
        }

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
     * Serialize a {@link InsertionComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T serializeInsertionComponent(InsertionComponent component);

    /**
     * Serialize a {@link FontComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T serializeFontComponent(FontComponent component);

    /**
     * Serialize a {@link TranslatableComponent}.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T serializeTranslateComponent(TranslatableComponent component);

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
     * Apply the specified color to the component.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @param color     the color
     * @return the result component
     */
    public abstract <T> @Nullable T applyColor(T component, Color color);

    /**
     * According to the value, apply or remove the specified style to the component.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @param style     the style
     * @param value     the value
     * @return the result component
     */
    public abstract <T> @Nullable T applyStyle(T component, Style style, Boolean value);

    /**
     * Apply the specified font to the component.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @param font      the font
     * @return the result component
     */
    public abstract <T> @Nullable T applyFont(T component, Font font);

    /**
     * Reset the component style and colors.
     *
     * @param <T>       the type parameter
     * @param component the component
     * @return the result component
     */
    public <T> @Nullable T reset(@Nullable T component) {
        if (component == null) return null;
        T c = component;
        c = applyColor(c, Color.WHITE);
        c = applyFont(c, Font.DEFAULT);
        for (Style style : Style.values())
            if (style != Style.RESET) c = applyStyle(c, style, false);
        return c;
    }

    /**
     * Send to player.
     *
     * @param <P>       the type parameter
     * @param player    the player
     * @param component the component
     */
    public <P> void send(@Nullable P player, @Nullable TextComponent component) {
        if (player == null) return;
        if (component == null) return;
        Object object = serializeComponent(component);
        send(player, object);
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

    private @NotNull Method findSerializeMethod(@NotNull TextComponent component) {
        Class<?> tmp = this.getClass();
        while (tmp != null) {
            Method method = Stream.concat(Arrays.stream(tmp.getDeclaredMethods()), Arrays.stream(tmp.getMethods()))
                    .filter(m -> m.getName().startsWith("serialize"))
                    .filter(m -> m.getParameterCount() == 1)
                    .filter(m -> m.getParameterTypes()[0].equals(component.getClass()))
                    .findFirst().orElse(null);
            if (method != null) {
                method.setAccessible(true);
                return method;
            }
            tmp = tmp.getSuperclass();
        }
        try {
            return ComponentSerializer.class.getMethod("serializeSimpleTextComponent", TextComponent.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}