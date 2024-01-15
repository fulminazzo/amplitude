package it.angrybear.components;

import com.google.gson.Gson;
import it.angrybear.exceptions.MissingRequiredOptionException;
import it.angrybear.interfaces.validators.OptionValidator;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public abstract class OptionComponent extends TextComponent {
    public static final String OPTIONS_REGEX = "([^=\\n ]+)(?:=(\"((?:\\\\\"|[^\"])+)\"|'((?:\\\\'|[^'])+)'|[^ ]+))?";
    protected final @NotNull Map<String, String> tagOptions;

    /**
     * Instantiates a new Option component.
     */
    public OptionComponent() {
        this(null);
    }

    /**
     * Instantiates a new Option component.
     *
     * @param rawText the raw text
     */
    public OptionComponent(@Nullable String rawText) {
        this.tagOptions = new HashMap<>();
        setContent(rawText);
    }

    @Override
    public void setContent(@Nullable String rawText) {
        if (rawText == null) return;
        super.setContent(rawText);
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
    public void setOptions(String rawText) {
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
                    Map<?, ?> json = new Gson().fromJson(value, Map.class);
                    json.forEach((k, v) -> tagOptions.put(k.toString(), v == null ? null : v.toString()));
                } else tagOptions.put(key, value);
            }
        }

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
     * Gets a map with the required options associated with an {@link OptionValidator}.
     *
     * @return the required options
     */
    protected Map<String, OptionValidator> getRequiredOptions() {
        return new HashMap<>();
    }
}
