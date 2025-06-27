package it.fulminazzo.amplitude.component;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a custom {@link ContainerComponent} with associated tag.
 * <br>
 * WARNING: before serializing and deserializing, at least one instance must be created.
 *
 * @param <C> the type of this component (for method chaining)
 */
public abstract class CustomContainerComponent<C extends CustomContainerComponent<C>> extends ContainerComponent<C> {

    /**
     * Instantiates a new Custom component.
     *
     * @param tagName the tag name
     */
    public CustomContainerComponent(@NotNull String tagName) {
        this(null, tagName);
    }

    /**
     * Instantiates a new Custom component.
     *
     * @param rawText the raw text
     * @param tagName the tag name
     */
    public CustomContainerComponent(final @Nullable String rawText, final @NotNull String tagName) {
        super(rawText, tagName);
        CustomComponent.loadComponent(getClass(), tagName);
    }

    /**
     * Converts the current component to one of the known components in Minecraft.
     *
     * @return the component
     */
    public abstract Component toMinecraft();

}
