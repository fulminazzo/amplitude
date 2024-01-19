package it.angrybear.interfaces;

import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    default @NotNull String getName() {
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
    static ChatFormatter getChatFormatter(@Nullable String name) {
        if (name == null) return null;
        name = name.replace("dark_", "dark")
                .replace("light_", "light");
        if (name.equalsIgnoreCase("strike")) return Style.STRIKETHROUGH;

        for (ChatFormatter c : Color.values())
            if (c.getName().equalsIgnoreCase(name)) return c;

        for (ChatFormatter c : Style.values()) {
            String n = name;
            if (c != Style.RESET && n.startsWith("!")) n = n.substring(1);
            if (c.getName().equalsIgnoreCase(n)) return c;
        }

        if (name.length() == 1) return getChatFormatter(name.toLowerCase().charAt(0));
        else return null;
    }

    /**
     * Find a valid chat formatter from {@link #getChatFormatters()} using its identifier char.
     *
     * @param identifierChar the identifier char
     * @return the chat formatter
     */
    static @Nullable ChatFormatter getChatFormatter(char identifierChar) {
        identifierChar = Character.toLowerCase(identifierChar);
        ChatFormatter[] chatFormatters = getChatFormatters();
        for (ChatFormatter c : chatFormatters)
            if (c.getIdentifierChar() == identifierChar)
                return c;
            /*
                Allow compatibility for:
                - bold (<b>)
                - italic (<i>)
                - strikethrough (<s>)
                - underline (<u>)
             */
            else if (c instanceof Style && !c.equals(Style.OBFUSCATED))
                if (c.name().toLowerCase().charAt(0) == identifierChar)
                    return c;

        return null;
    }

    /**
     * Get an array containing all the chat formatters from {@link Style} and {@link Color}.
     *
     * @return the chat formatters
     */
    static ChatFormatter @NotNull [] getChatFormatters() {
        return Stream.concat(Arrays.stream(Style.values()), Arrays.stream(Color.values()))
                .toArray(ChatFormatter[]::new);
    }
}
