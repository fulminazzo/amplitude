package it.angrybear.utils;

import org.jetbrains.annotations.Nullable;

/**
 * String utilities.
 */
public class StringUtils {

    /**
     * A function that removes quotes from strings.
     * For example, <i>"text"</i> will be converted to <i>text</i>.
     *
     * @param string the string
     * @return the string
     */
    public static @Nullable String stripQuotes(@Nullable String string) {
        if (string == null) return null;
        if (string.matches("\".*\"") || string.matches("'.*'"))
            return string.substring(1, string.length() - 1);
        return string;
    }
}