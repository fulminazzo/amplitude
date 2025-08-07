package it.fulminazzo.amplitude.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utilities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

    public static final Pattern QUOTE_PATTERN = Pattern.compile("(\"[^\"]*\")|('([^'])*')", Pattern.DOTALL);

    /**
     * Split a string using the given separator, ignoring it if it is contained inside quotes.
     *
     * @param string    the string
     * @param separator the separator
     * @return the result of the split
     */
    public static @Nullable String[] splitQuoteSensitive(@Nullable String string, char separator) {
        return splitQuoteSensitive(string, separator + "");
    }

    /**
     * Split a string using the given separator, ignoring it if it is contained inside quotes.
     *
     * @param string the string
     * @param regex  the regular expression to use to split
     * @return the result of the split
     */
    public static @Nullable String[] splitQuoteSensitive(@Nullable String string, @NotNull String regex) {
        if (string == null) return null;
        final String placeholder = String.valueOf((char) 22);

        LinkedList<String> quoted = new LinkedList<>();
        Matcher matcher = QUOTE_PATTERN.matcher(string);
        while (matcher.find()) {
            quoted.add(matcher.group());
            string = string.substring(0, matcher.start()) + placeholder + string.substring(matcher.end());
            matcher = QUOTE_PATTERN.matcher(string);
        }

        final String[] split = string.split(regex);
        final Pattern placeholderPattern = Pattern.compile(placeholder, Pattern.DOTALL);
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            matcher = placeholderPattern.matcher(s);
            while (matcher.find()) {
                s = s.substring(0, matcher.start()) + quoted.pop() + s.substring(matcher.end());
                matcher = placeholderPattern.matcher(s);
            }
            split[i] = s;
        }

        return split;
    }

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