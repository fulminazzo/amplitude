package it.angrybear.components;

import com.google.gson.Gson;
import it.angrybear.exception.MissingRequiredOptionException;
import it.angrybear.components.validator.OptionValidator;
import it.angrybear.utils.StringUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A component that supports options.
 * Options are values specified after the first tag name.
 * <p>
 * For example, in "&#60;tag option1="Hello" option2=world&#62;Some text",
 * the options are:
 * <ul>
 *     <li>option1: Hello</li>
 *     <li>option2: world</li>
 * </ul>
 */
@Getter
abstract class OptionComponent extends TextComponent {
    public static final String OPTIONS_REGEX = "([^=\\n ]+)(?:=(\"((?:\\\\\"|[^\"])+)\"|'((?:\\\\'|[^'])+)'|[^ ]+))?";
    protected final @NotNull String tagName;
    protected final @NotNull Map<String, String> tagOptions;

    /**
     * Instantiates a new Option component.
     *
     * @param tagName the tag name
     */
    public OptionComponent(@NotNull String tagName) {
        this(null, tagName);
    }

    /**
     * Instantiates a new Option component.
     *
     * @param rawText the raw text
     * @param tagName the tag name
     */
    public OptionComponent(@Nullable String rawText, @NotNull String tagName) {
        this.tagName = tagName;
        this.tagOptions = new HashMap<>();
        setContent(rawText);
    }

    @Override
    public void setContent(@Nullable String rawText) {
        if (rawText == null) return;
        final Matcher matcher = TAG_REGEX.matcher(rawText);
        if (matcher.find()) {
            final String tag = matcher.group(1).split(" ")[0];
            if (tag.equals(tagName)) super.setContent(rawText.substring(matcher.group().length()));
            else setNext(rawText);
        } else setNext(rawText);
        setOptions(rawText);
    }

    /**
     * Set the options from a raw string.
     * After parsing every option, loops through every key from {@link #getRequiredOptions()}.
     * If the option is not given, throw a new {@link MissingRequiredOptionException}.
     * If the option is given, uses the associated {@link OptionValidator} to verify its validity.
     *
     * @param rawText the raw text
     */
    public void setOptions(@Nullable String rawText) {
        this.tagOptions.clear();

        String rawOptions = rawText;
        if (rawText != null) {
            Matcher startMatcher = TAG_REGEX.matcher(rawText);
            if (startMatcher.find()) {
                final String[] tmp = startMatcher.group(1).split(" ");
                if (tmp.length > 1)
                    rawOptions = String.join(" ", Arrays.copyOfRange(tmp, 1, tmp.length));
            }
        }

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
                    Gson gson = new Gson();
                    Map<?, ?> json = gson.fromJson(value, Map.class);
                    json.forEach((k, v) -> {
                        String jKey = k.toString();
                        if (v == null) tagOptions.put(jKey, null);
                        else try {
                            tagOptions.put(jKey, StringUtils.stripQuotes(gson.toJson(v)));
                        } catch (Exception e) {
                            tagOptions.put(jKey, v.toString());
                        }
                    });
                } else tagOptions.put(key, value);
            }
        }

        checkOptions();
    }

    /**
     * Check options.
     */
    protected void checkOptions() {
        final Map<String, OptionValidator> requiredOptions = this.getRequiredOptions();
        for (String key : requiredOptions.keySet()) {
            String option = tagOptions.get(key);
            if (option == null) throw new MissingRequiredOptionException(key, tagOptions);
            else {
                OptionValidator validator = requiredOptions.get(key);
                if (validator != null) validator.test(key, option);
            }
        }
    }

    /**
     * Gets a tag option from its name.
     *
     * @param key the name
     * @return the tag option
     */
    public String getTagOption(String key) {
        return this.tagOptions.get(key);
    }

    /**
     * Sets tag option.
     *
     * @param key   the key
     * @param value the value
     */
    public void setTagOption(String key, String value)  {
        this.tagOptions.put(key, value);
    }

    /**
     * Gets tag options.
     *
     * @return the tag options
     */
    public Map<String, String> getTagOptions() {
        return new LinkedHashMap<>(this.tagOptions);
    }

    /**
     * Gets a map with the required options associated with an {@link OptionValidator}.
     *
     * @return the required options
     */
    protected Map<String, OptionValidator> getRequiredOptions() {
        return new HashMap<>();
    }

    /**
     * Gets tag regex from the tag name.
     *
     * @param tagName the tag name
     * @return the tag regex
     */
    public static @NotNull Pattern getTagRegex(String tagName) {
        String regex = "<" + tagName + " ?((?:(?!<" + tagName + ")(?!</" + tagName + ">).)*)";
        return Pattern.compile(regex);
    }

    @Override
    public boolean contains(@NotNull TextComponent textComponent) {
        if (this.getClass().equals(textComponent.getClass())) {
            if (!super.contains(textComponent)) return false;
            return this.getTagOptions().equals(((OptionComponent) textComponent).getTagOptions());
        } else return next != null && next.contains(textComponent);
    }

    @Override
    protected @NotNull String serializeSingle() {
        String options = this.tagOptions.entrySet().stream()
                .map(e -> String.format("%s=\"%s\"", e.getKey(), e.getValue()))
                .collect(Collectors.joining(" "));
        String tag = "<" + tagName;
        if (!options.isEmpty()) tag += " " + options;
        return tag + String.format(">%s", getText());
    }

    /**
     * Check if the current component is empty using {@link TextComponent#isEmpty()} and {@link #tagOptions}.
     *
     * @return true if both are empty.
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && tagOptions.isEmpty();
    }

}
