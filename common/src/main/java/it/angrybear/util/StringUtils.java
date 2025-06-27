package it.angrybear.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

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
        if (string == null) return null;
        List<String> split = new ArrayList<>();

        String tmp = "";
        char startingQuote = 0;
        for (char c : string.toCharArray()) {
            if (startingQuote != 0) {
                if (c == startingQuote) startingQuote = 0;
            } else {
                if (c == separator) {
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