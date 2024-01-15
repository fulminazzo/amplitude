package it.angrybear.components;

import com.google.gson.Gson;
import it.angrybear.exceptions.InvalidComponentException;
import it.angrybear.exceptions.MissingRequiredOptionException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
public abstract class ContainerComponent extends TextComponent {
    public static final String OPTIONS_REGEX = "([^=\\n ]+)(?:=([A-Za-z0-9]+|\"((?:\\\\\"|[^\"])+)\"|'((?:\\\\'|[^'])+)'))?";
    protected final String tagName;
    protected TextComponent children;
    protected final HashMap<String, String> tagOptions;

    public ContainerComponent(String rawText, String tagName) {
        this.tagName = tagName;
        this.tagOptions = new HashMap<>();

        Matcher startMatcher = getTagRegex(tagName).matcher(rawText);
        if (!startMatcher.find())
            throw new InvalidComponentException(String.format("Could not find valid start <%s> for component %s",
                    tagName, this.getClass().getSimpleName()));

        final String rawOptions = startMatcher.group(2);
        if (rawOptions != null) {
            final Matcher optionsMatcher = Pattern.compile(OPTIONS_REGEX).matcher(rawOptions);
            while (optionsMatcher.find()) {
                String key = optionsMatcher.group(1);
                String value = optionsMatcher.group(4);
                if (value == null) value = optionsMatcher.group(3);
                if (value == null) value = optionsMatcher.group(2);
                if (value != null)
                    value = value
                            .replace("\\\"", "\"")
                            .replace("\\'", "'");
                if (value != null && key.equals("json")) {
                    Map<?, ?> json = new Gson().fromJson(value, Map.class);
                    json.forEach((k, v) -> tagOptions.put(k.toString(), v == null ? null : v.toString()));
                } else tagOptions.put(key, value);
            }
        }

        for (String o : getRequiredOptions())
            if (tagOptions.get(o) == null) throw new MissingRequiredOptionException(o, tagOptions);

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
        return Pattern.compile("<" + tagName + "( ([^\n>]+))?>");
    }

    protected String[] getRequiredOptions() {
        return new String[0];
    }
}
