package it.angrybear.components;

import it.angrybear.exceptions.InvalidComponentException;
import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public abstract class ContainerComponent extends TextComponent {
    protected final String tagName;
    protected TextComponent children;

    public ContainerComponent(String rawText, String tagName) {
        this.tagName = tagName;
        Matcher startMatcher = getTagRegex(tagName).matcher(rawText);
        if (!startMatcher.find())
            throw new InvalidComponentException(String.format("Could not find valid start <TAG> for component %s",
                    this.getClass().getSimpleName()));

        final String endRegex = "</" + tagName + ">";
        Matcher endMatcher = Pattern.compile(endRegex).matcher(rawText);
        if (!endMatcher.find())
            throw new InvalidComponentException(String.format("Could not find valid end </%s> for component %s",
                    tagName, this.getClass().getSimpleName()));

        final String content = rawText.substring(startMatcher.end(), endMatcher.end() - endRegex.length());
        if (!content.trim().isEmpty()) children = new TextComponent(content);

        rawText = rawText.substring(endMatcher.end());
        if (rawText.trim().isEmpty()) return;
        setNext(rawText);
    }

    public static Pattern getTagRegex(String tagName) {
        return Pattern.compile("<" + tagName + "( [^\n>]+)?>");
    }
}
