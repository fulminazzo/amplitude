package it.fulminazzo.amplitude.component;

import com.google.gson.Gson;
import it.fulminazzo.amplitude.component.validator.OptionValidator;
import it.fulminazzo.amplitude.util.StringUtils;
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
 *
 * @param <C> the type of this component
 */
@SuppressWarnings("unchecked")
@Getter
abstract class OptionComponent<C extends OptionComponent<C>> extends Component {
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
    public @NotNull C setContent(@Nullable String rawText) {
        if (rawText == null) return (C) this;
        final Matcher matcher = TAG_REGEX.matcher(rawText);
        if (matcher.find()) {
            String raw = StringUtils.splitQuoteSensitive(rawText, '>')[0].substring(1);
            final String tag = raw.split(" ")[0];
            if (tag.equals(tagName)) super.setContent(rawText.substring(raw.length() + 2));
            else setNext(rawText);
        } else setNext(rawText);
        setOptions(rawText);
        return (C) this;
    }

    /**
     * Set the options from a raw string.
     * After parsing every option, loops through every key from {@link #getRequiredOptions()}.
     * If the option is not given, throw a new {@link MissingRequiredOptionException}.
     * If the option is given, uses the associated {@link OptionValidator} to verify its validity.
     *
     * @param rawText the raw text
     */
    protected void setOptions(@Nullable String rawText) {
        this.tagOptions.clear();

        String rawOptions = null;
        if (rawText != null) {
            Matcher startMatcher = TAG_REGEX.matcher(rawText);
            if (startMatcher.find()) {
                String raw = StringUtils.splitQuoteSensitive(rawText, '>')[0];
                final String[] tmp = StringUtils.splitQuoteSensitive(raw, ' ');
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
     * @return this component
     */
    public @NotNull C setTagOption(String key, String value) {
        this.tagOptions.put(key, value);
        return (C) this;
    }

    /**
     * Gets tag options.
     *
     * @return the tag options
     */
    public @NotNull Map<String, String> getTagOptions() {
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
    public static @NotNull Pattern getTagRegex(final @NotNull String tagName) {
        String regex = "<" + tagName + " ?((?:(?!<" + tagName + ")(?!</" + tagName + ">).)*)";
        return Pattern.compile(regex, Pattern.DOTALL);
    }

    @Override
    public boolean contains(final @NotNull Component component) {
        if (this.getClass().equals(component.getClass())) {
            if (!super.contains(component)) return false;
            return this.getTagOptions().equals(((OptionComponent<?>) component).getTagOptions());
        } else return next != null && next.contains(component);
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
     * Check if the current component is empty using {@link Component#isEmpty()} and {@link #tagOptions}.
     *
     * @return true if both are empty.
     */
    @Override
    public boolean isEmpty() {
        return super.isEmpty() && tagOptions.isEmpty();
    }

    @Override
    public @NotNull C setText(@Nullable String text) {
        return (C) super.setText(text);
    }

    @Override
    public @NotNull C addNext(@Nullable String rawText) {
        return (C) super.addNext(rawText);
    }

    @Override
    public @NotNull C addNext(@Nullable Component next) {
        return (C) super.addNext(next);
    }

    @Override
    public @NotNull C setNext(@Nullable String rawText) {
        return (C) super.setNext(rawText);
    }

    @Override
    public @NotNull C setNext(Component next) {
        return (C) super.setNext(next);
    }

    @Override
    @NotNull C setSameOptions(@Nullable Component component) {
        return (C) super.setSameOptions(component);
    }

    @Override
    public @NotNull C setColor(Color color) {
        return (C) super.setColor(color);
    }

    @Override
    public @NotNull C setColor(Color color, boolean propagate) {
        return (C) super.setColor(color, propagate);
    }

    @Override
    public @NotNull C setFont(Font font) {
        return (C) super.setFont(font);
    }

    @Override
    public @NotNull C setFont(Font font, boolean propagate) {
        return (C) super.setFont(font, propagate);
    }

    @Override
    public @NotNull C setObfuscated(Boolean obfuscated) {
        return (C) super.setObfuscated(obfuscated);
    }

    @Override
    public @NotNull C setObfuscated(Boolean obfuscated, boolean propagate) {
        return (C) super.setObfuscated(obfuscated, propagate);
    }

    @Override
    public @NotNull C setBold(Boolean bold) {
        return (C) super.setBold(bold);
    }

    @Override
    public @NotNull C setBold(Boolean bold, boolean propagate) {
        return (C) super.setBold(bold, propagate);
    }

    @Override
    public @NotNull C setStrikethrough(Boolean strikethrough) {
        return (C) super.setStrikethrough(strikethrough);
    }

    @Override
    public @NotNull C setStrikethrough(Boolean strikethrough, boolean propagate) {
        return (C) super.setStrikethrough(strikethrough, propagate);
    }

    @Override
    public @NotNull C setUnderlined(Boolean underlined) {
        return (C) super.setUnderlined(underlined);
    }

    @Override
    public @NotNull C setUnderlined(Boolean underlined, boolean propagate) {
        return (C) super.setUnderlined(underlined, propagate);
    }

    @Override
    public @NotNull C setItalic(Boolean italic) {
        return (C) super.setItalic(italic);
    }

    @Override
    public @NotNull C setItalic(Boolean italic, boolean propagate) {
        return (C) super.setItalic(italic, propagate);
    }

    @Override
    public @NotNull C reset(@NotNull Boolean reset) {
        return (C) super.reset(reset);
    }

    @Override
    public @NotNull C reset(@NotNull Boolean reset, boolean propagate) {
        return (C) super.reset(reset, propagate);
    }

    @Override
    public @NotNull C setStyle(@Nullable Style style) {
        return (C) super.setStyle(style);
    }

    @Override
    public @NotNull C setStyle(@Nullable Style style, Boolean value) {
        return (C) super.setStyle(style, value);
    }

    @Override
    public @NotNull C setStyle(@Nullable Style style, Boolean value, boolean propagate) {
        return (C) super.setStyle(style, value, propagate);
    }

}
