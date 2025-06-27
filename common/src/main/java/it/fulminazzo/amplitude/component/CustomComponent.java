package it.fulminazzo.amplitude.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Represents a custom {@link OptionComponent} with associated tag.
 * <br>
 * WARNING: before serializing and deserializing, at least one instance must be created.
 *
 * @param <C> the type of this component (for method chaining)
 */
public abstract class CustomComponent<C extends CustomComponent<C>> extends OptionComponent<C> {

    /**
     * Instantiates a new Custom component.
     *
     * @param tagName the tag name
     */
    public CustomComponent(@NotNull String tagName) {
        this(null, tagName);
    }

    /**
     * Instantiates a new Custom component.
     *
     * @param rawText the raw text
     * @param tagName the tag name
     */
    public CustomComponent(final @Nullable String rawText, final @NotNull String tagName) {
        super(rawText, tagName);
        loadComponent(getClass(), tagName);
    }

    /**
     * Converts the current component to one of the known components in Minecraft.
     *
     * @return the component
     */
    public abstract Component toMinecraft();

    /**
     * Loads this component in the {@link Component#CONTAINER_COMPONENTS} map.
     *
     * @param clazz   the class of the component
     * @param tagName the tag name
     */
    static void loadComponent(@NotNull Class<?> clazz, @NotNull String tagName) {
        Component.CONTAINER_COMPONENTS.put(tagName, s -> {
            try {
                Constructor<?> constructor = clazz.getDeclaredConstructor(String.class);
                return (Component) constructor.newInstance(s);
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(
                        String.format("Could not find constructor %s(String). A constructor with String as parameter is required",
                                clazz.getSimpleName())
                );
            }
        });
    }

}
