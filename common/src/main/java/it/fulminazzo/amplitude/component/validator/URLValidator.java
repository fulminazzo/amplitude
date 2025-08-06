package it.fulminazzo.amplitude.component.validator;

import it.fulminazzo.amplitude.exception.InvalidOptionException;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * A validator for URL strings.
 */
public final class URLValidator implements OptionValidator {
    static final Pattern URL_REGEX = Pattern.compile("^((?:https?://)?(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b[-a-zA-Z0-9()@:%_+.~#?&/=]*)$");

    @Override
    public void test(final @NotNull String optionName, final @NotNull String option) throws InvalidOptionException {
        if (!URL_REGEX.matcher(option).find())
            throw new InvalidOptionException(optionName, "URL", option);
    }

}
