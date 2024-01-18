package it.angrybear.components;

import it.angrybear.exceptions.InvalidComponentException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of {@link TextComponent} that mimics XML tags.
 * This means that for any container component, opening and closing tags are required.
 * If those are not provided, a {@link InvalidComponentException} will be thrown.
 * <p>
 * Example: "&#60;component&#62;This is contained&#60;/component&#62; is a valid container component.
 */
@Getter
public abstract class ContainerComponent extends OptionComponent {
    public static final String OPTIONS_REGEX = "([^=\\n ]+)(?:=(\"((?:\\\\\"|[^\"])+)\"|'((?:\\\\'|[^'])+)'|[^ ]+))?";
    protected TextComponent child;

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
    public void setContent(@Nullable String rawText) {
        if (rawText == null) return;
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

        startMatcher = TextComponent.TAG_REGEX.matcher(startMatcher.group());
        if (startMatcher.find()) {
            String match = startMatcher.group(1);
            if (match != null)
                setOptions(match.substring(tagName.length()));
        }

        final String content = rawText.substring(startMatcher.end(), endMatcher.end() - endRegex.length());
        setChild(content);

        rawText = rawText.substring(endMatcher.end());
        if (rawText.trim().isEmpty()) return;
        setNext(rawText);
    }

    /**
     * Get the inner text from the children using {@link TextComponent#toRaw(TextComponent)}.
     *
     * @return the text
     */
    @Override
    public @Nullable String getText() {
        return child == null ? null : TextComponent.toRaw(child);
    }

    /**
     * Set the children as the given text using {@link TextComponent#fromRaw(String)}.
     *
     * @param text the text
     */
    @Override
    public void setText(@Nullable String text) {
        this.child = null;
        if (text == null) return;
        this.child = TextComponent.fromRaw(text);
    }

    /**
     * Sets the child component and applies {@link #setSameOptions(TextComponent)} method.
     *
     * @param rawText the raw text
     */
    public void setChild(@Nullable String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) return;
        setChild(new TextComponent(rawText));
    }

    /**
     * Sets the child component and applies {@link #setSameOptions(TextComponent)} method.
     *
     * @param child the child
     */
    public void setChild(TextComponent child) {
        this.child = child;
        setSameOptions(child);
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
