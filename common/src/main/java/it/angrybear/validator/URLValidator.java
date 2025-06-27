package it.angrybear.validator;

import it.angrybear.exceptions.InvalidOptionException;

import java.util.regex.Pattern;

/**
 * A validator for URL strings.
 */
public class URLValidator implements OptionValidator {
    public static final String URL_REGEX = "^((?:https?://)?(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b[-a-zA-Z0-9()@:%_+.~#?&/=]*)$";

    @Override
    public void test(String optionName, String option) throws InvalidOptionException {
        if (!Pattern.compile(URL_REGEX).matcher(option).find())
            throw new InvalidOptionException(optionName, "URL", option);
    }
}
