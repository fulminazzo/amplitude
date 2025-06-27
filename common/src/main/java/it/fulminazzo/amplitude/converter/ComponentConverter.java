package it.fulminazzo.amplitude.converter;

import it.fulminazzo.amplitude.component.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An abstract class that allows creating serializers for text components.
 */
public abstract class ComponentConverter {

    /**
     * A method to get the first valid ComponentConverter from the current package.
     * It tries to instantiate every class that is not {@link CharCodeConverter},
     * {@link AmpersandConverter} or {@link SectionSignConverter}.
     * If it fails with every class, return a new {@link SectionSignConverter};
     *
     * @return the component serializer
     */
    @SuppressWarnings("unchecked")
    public static @NotNull ComponentConverter converter() {
        Set<String> classes = getClassesInPackage();
        for (String className : classes) {
            try {
                Class<?> clazz = Class.forName(className);
                Constructor<? extends ComponentConverter> constructor = (Constructor<? extends ComponentConverter>) clazz.getConstructor();
                return constructor.newInstance();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (Exception ignored) {
            }
        }
        return new SectionSignConverter();
    }

    /**
     * Gets all the available serializers of the library.
     *
     * @return the serializers names to prevent load errors
     */
    static @NotNull Set<String> getClassesInPackage() {
        return Stream.of("Adventure", "Bungee", "LegacyBungee")
                .map(c -> ComponentConverter.class.getPackage().getName() + "." + c + "Converter")
                .collect(Collectors.toSet());
    }

    /**
     * Get a new section sign serializer.
     *
     * @return the section sign serializer
     */
    public static @NotNull SectionSignConverter sectionSign() {
        return new SectionSignConverter();
    }

    /**
     * Get a new ampersand serializer.
     *
     * @return the ampersand serializer
     */
    public static @NotNull AmpersandConverter ampersand() {
        return new AmpersandConverter();
    }

    /**
     * Convert a general {@link Component} and its siblings.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @return the output
     */
    @SuppressWarnings("unchecked")
    public <T> @Nullable T convertComponent(@Nullable Component component) {
        if (component == null) return null;

        Method method = findConvertMethod(component);
        T output;
        try {
            output = (T) method.invoke(this, component);
        } catch (IllegalAccessException | InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) throw (RuntimeException) cause;
            else throw new RuntimeException(cause);
        }

        if (component.getNext() != null)
            output = sumTwoConvertedComponents(output, convertComponent(component.getNext()));

        return output;
    }

    /**
     * Convert a {@link Component}.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T convertSimpleComponent(Component component);

    /**
     * Convert a {@link HoverComponent}.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T convertHoverComponent(HoverComponent component);

    /**
     * Convert a {@link ClickComponent}.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T convertClickComponent(ClickComponent component);

    /**
     * Convert a {@link HexComponent}.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T convertHexComponent(HexComponent component);

    /**
     * Convert a {@link InsertionComponent}.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T convertInsertionComponent(InsertionComponent component);

    /**
     * Convert a {@link FontComponent}.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T convertFontComponent(FontComponent component);

    /**
     * Convert a {@link TranslatableComponent}.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @return the output
     */
    public abstract <T> @Nullable T convertTranslateComponent(TranslatableComponent component);

    /**
     * Convert a {@link CustomComponent}.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @return the t
     */
    public <T> @Nullable T convertCustomComponent(CustomComponent<?> component) {
        return convertComponent(component.toMinecraft());
    }

    /**
     * Sum two serialized components.
     *
     * @param <T>        the type of the component
     * @param component1 the first component
     * @param component2 the second component
     * @return the result component
     */
    public abstract <T> @Nullable T sumTwoConvertedComponents(T component1, T component2);

    /**
     * Apply the specified color to the component.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @param color     the color
     * @return the result component
     */
    public abstract <T> @Nullable T applyColor(T component, Color color);

    /**
     * According to the value, apply or remove the specified style to the component.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @param style     the style
     * @param value     the value
     * @return the result component
     */
    public abstract <T> @Nullable T applyStyle(T component, Style style, Boolean value);

    /**
     * Apply the specified font to the component.
     *
     * @param <T>       the type of the component
     * @param component the component
     * @param font      the font
     * @return the result component
     */
    public abstract <T> @Nullable T applyFont(T component, Font font);

    /**
     * Reset the component style and colors.
     *
     * @param <T>       the type of the component
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
     * @param <P>       the type of the component
     * @param player    the player
     * @param component the component
     */
    public <P> void send(@Nullable P player, @Nullable Component component) {
        if (player == null) return;
        if (component == null) return;
        Object object = convertComponent(component);
        send(player, object);
    }

    /**
     * Send to player.
     *
     * @param <T>       the type of the component
     * @param <P>       the type of the component
     * @param player    the player
     * @param component the component
     */
    public abstract <T, P> void send(P player, T component);

    private @NotNull Method findConvertMethod(@NotNull Component component) {
        Class<?> tmp = this.getClass();
        while (tmp != null) {
            Method method = Stream.concat(Arrays.stream(tmp.getDeclaredMethods()), Arrays.stream(tmp.getMethods()))
                    .filter(m -> m.getName().startsWith("convert"))
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
            return ComponentConverter.class.getMethod("convertSimpleComponent", Component.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

}