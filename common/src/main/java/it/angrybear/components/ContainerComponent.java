package it.angrybear.components;

import it.angrybear.exceptions.InvalidComponentException;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public abstract class ContainerComponent extends OptionComponent {
    public static final String OPTIONS_REGEX = "([^=\\n ]+)(?:=(\"((?:\\\\\"|[^\"])+)\"|'((?:\\\\'|[^'])+)'|[^ ]+))?";
    protected final String tagName;
    protected TextComponent child;

    public ContainerComponent(String tagName) {
        this(null, tagName);
    }

    public ContainerComponent(@Nullable String rawText, @NotNull String tagName) {
        this.tagName = tagName;

        setContent(rawText);
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

        setOptions(startMatcher.group(1));

        final String content = rawText.substring(startMatcher.end(), endMatcher.end() - endRegex.length());
        setChild(content);

        rawText = rawText.substring(endMatcher.end());
        if (rawText.trim().isEmpty()) return;
        setNext(rawText);
    }

    public void setChild(@Nullable String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) return;
        setChild(new TextComponent(rawText));
    }

    public void setChild(TextComponent child) {
        this.child = child;
        setSameOptions(child);
    }

    public static @NotNull Pattern getTagRegex(String tagName) {
        String regex = TextComponent.TAG_REGEX.toString();
        regex = regex.substring(2);
        regex = regex.substring(0, regex.length() - 2);
        return Pattern.compile("<" + tagName + "( (" + regex + ")*)?>");
    }
}
