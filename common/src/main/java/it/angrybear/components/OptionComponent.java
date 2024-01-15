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

@Getter
public abstract class OptionComponent extends TextComponent {
    public static final String OPTIONS_REGEX = "([^=\\n ]+)(?:=(\"((?:\\\\\"|[^\"])+)\"|'((?:\\\\'|[^'])+)'|[^ ]+))?";
    protected final String tagName;
    protected final @NotNull Map<String, String> tagOptions;

    public OptionComponent(@NotNull String rawText, String tagName) {
        this.tagName = tagName;
        this.tagOptions = new HashMap<>();

        setContent(rawText);
    }

    @Override
    public void setContent(@Nullable String rawText) {
        if (rawText == null) return;
        super.setContent(rawText);

        this.tagOptions.clear();

        Matcher startMatcher = TAG_REGEX.matcher(rawText);
        if (!startMatcher.find()) return;
        final String[] tmp = startMatcher.group(1).split(" ");
        if (tmp.length < 2) return;

        final String rawOptions = String.join(" ", Arrays.copyOfRange(tmp, 1, tmp.length));
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

        final Map<String, OptionValidator> requiredOptions = getRequiredOptions();
        for (String key : requiredOptions.keySet()) {
            String option = tagOptions.get(key);
            if (option == null) throw new MissingRequiredOptionException(key, tagOptions);
            else {
                OptionValidator validator = requiredOptions.get(key);
                if (validator != null) validator.test(key, option);
            }
        }
    }

    protected Map<String, OptionValidator> getRequiredOptions() {
        return new HashMap<>();
    }
}
