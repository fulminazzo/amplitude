package it.fulminazzo.amplitude.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * String utilities.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

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
        List<String> split = new ArrayList<>();

        String tmp = "";
        char startingQuote = 0;
        for (char c : string.toCharArray()) {
            if (startingQuote != 0) {
                if (c == startingQuote) startingQuote = 0;
            } else {
                Matcher matcher = Pattern.compile(".*(" + regex + ")", Pattern.DOTALL).matcher(tmp + c);
                if (matcher.matches()) {
                    tmp += c;
                    tmp = tmp.substring(0, tmp.length() - matcher.group(1).length());
                    split.add(tmp);
                    tmp = "";
                    continue;
                } else if (c == '"' | c == '\'') startingQuote = c;
            }
            tmp += c;
        }
        if (!tmp.isEmpty()) split.add(tmp);

        return split.toArray(new String[0]);
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