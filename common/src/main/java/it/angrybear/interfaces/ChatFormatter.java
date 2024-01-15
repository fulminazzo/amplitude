package it.angrybear.interfaces;

import it.angrybear.enums.Color;
import it.angrybear.enums.Style;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * An interface used for identifying Minecraft chat formatters.
 * See {@link Color} and {@link Style} for the implementations.
 */
public interface ChatFormatter {

    /**
     * Gets the lowercase name.
     *
     * @return the name
     */
    default String getName() {
        return name().toLowerCase().replace("_", "");
    }

    /**
     * Gets identifier char.
     *
     * @return the identifier char
     */
    char getIdentifierChar();

    /**
     * Gets the name.
     *
     * @return the name
     */
    String name();

    /**
     * Find a valid chat formatter from {@link #getChatFormatters()} using its name.
     *
     * @param name the name
     * @return the chat formatter
     */
    static ChatFormatter getChatFormatter(String name) {
        if (name == null) return null;
        name = name.replace("dark_", "dark")
                .replace("light_", "light");
        ChatFormatter[] chatFormatters = getChatFormatters();
        for (ChatFormatter c : chatFormatters) {
            if (c.getName().equalsIgnoreCase(name)) return c;
        }
        return null;
    }

    /**
     * Find a valid chat formatter from {@link #getChatFormatters()} using its identifier char.
     *
     * @param identifierChar the identifier char
     * @return the chat formatter
     */
    static ChatFormatter getChatFormatter(char identifierChar) {
        ChatFormatter[] chatFormatters = getChatFormatters();
        for (ChatFormatter c : chatFormatters)
            if (c.getIdentifierChar() == identifierChar)
                return c;
        return null;
    }

    /**
     * Get an array containing all the chat formatters from {@link Color} and {@link Style}.
     *
     * @return the chat formatters
     */
    static ChatFormatter [] getChatFormatters() {
        return Stream.concat(Arrays.stream(Color.values()), Arrays.stream(Style.values()))
                .map(c -> (ChatFormatter) c)
                .toArray(ChatFormatter[]::new);
    }
}
