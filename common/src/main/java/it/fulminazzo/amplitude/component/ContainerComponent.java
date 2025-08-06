package it.fulminazzo.amplitude.component;

import it.fulminazzo.amplitude.util.StringUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of {@link Component} that mimics XML tags.
 * This means that for any container component, opening and closing tags are required.
 * If those are not provided, a {@link InvalidComponentException} will be thrown.
 * <p>
 * Example: "&#60;component&#62;This is contained&#60;/component&#62;" is a valid container component.
 *
 * @param <C> the type of this component
 */
@SuppressWarnings("unchecked")
@Getter
abstract class ContainerComponent<C extends OptionComponent<C>> extends OptionComponent<C> {
    protected @Nullable Component child;

    /**
     * Instantiates a new Container component.
     *
     * @param tagName the tag name
     */
    public ContainerComponent(@NotNull String tagName) {
        this(null, tagName);
    }

    /**
     * Instantiates a new Container component.
     *
     * @param rawText the raw text
     * @param tagName the tag name
     */
    public ContainerComponent(@Nullable String rawText, @NotNull String tagName) {
        super(rawText, tagName);
    }

    @Override
    public @NotNull C setContent(@Nullable String rawText) {
        if (rawText == null) return (C) this;
        this.tagOptions.clear();

        Matcher startMatcher = getTagRegex(tagName).matcher(rawText);
        if (!startMatcher.find())
            throw new InvalidComponentException(String.format("Could not find valid start <%s> for component %s",
                    tagName, this.getClass().getSimpleName()));

        final String endRegex = "</" + tagName + ">";
        Matcher endMatcher = Pattern.compile(endRegex).matcher(rawText);
        if (!endMatcher.find())
            throw new InvalidComponentException(String.format("Could not find valid end </%s> for component %s",
                    tagName, this.getClass().getSimpleName()));

        startMatcher = Component.TAG_REGEX.matcher(startMatcher.group());
        if (startMatcher.find()) {
            String match = startMatcher.group(1);
            if (match != null) {
                String split = StringUtils.splitQuoteSensitive(rawText, '>')[0].substring(1);
                setOptions(split.substring(tagName.length()));
            }
        }

        final String content = rawText.substring(startMatcher.end(), endMatcher.end() - endRegex.length());
        setChild(content);

        rawText = rawText.substring(endMatcher.end());
        if (rawText.trim().isEmpty()) return (C) this;
        else return setNext(rawText);
    }

    /**
     * Get the inner text from the children using {@link Component#toRaw(Component)}.
     *
     * @return the text
     */
    @Override
    public @Nullable String getText() {
        return child == null ? null : Component.toRaw(child);
    }

    /**
     * Set the children as the given text using {@link Component#fromRaw(String)}.
     *
     * @param text the text
     */
    @Override
    public @NotNull C setText(@Nullable String text) {
        this.child = null;
        if (text != null) this.child = Component.fromRaw(text);
        return (C) this;
    }

    /**
     * Sets the child component and applies {@link #setSameOptions(Component)} method.
     *
     * @param rawText the raw text
     * @return this component
     */
    public @NotNull C setChild(@Nullable String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) return (C) this;
        return setChild(Component.fromRaw(rawText));
    }

    /**
     * Sets the child component and applies {@link #setSameOptions(Component)} method.
     *
     * @param child the child
     * @return this component
     */
    public @NotNull C setChild(Component child) {
        this.child = child;
        return setSameOptions(child);
    }

    @Override
    public boolean contains(@NotNull Component component) {
        if (!super.contains(component)) return false;
        if (!this.getClass().equals(component.getClass())) return next != null && next.contains(component);
        ContainerComponent<?> containerComponent = (ContainerComponent<?>) component;
        Component c1 = getChild();
        Component c2 = containerComponent.getChild();
        return (c1 == null && c2 == null) || (c1 != null && c2 != null && c1.contains(c2));
    }

    @Override
    protected @NotNull String serializeSingle() {
        return super.serializeSingle() + "</" + tagName + ">";
    }

    /**
     * Check if the current component is empty using {@link OptionComponent#isEmpty()} and {@link #child}.
     *
     * @return true if both are empty.
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && (child == null || child.isEmpty());
    }

}
