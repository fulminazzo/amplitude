package it.angrybear.interfaces;

import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.stream.Stream;

public interface ChatFormatter {

    default @NotNull String getName() {
        return name().toLowerCase().replace("_", "");
    }

    char getIdentifierChar();

    String name();

    static ChatFormatter getChatFormatter(@Nullable String name) {
        if (name == null) return null;
        ChatFormatter[] chatFormatters = getChatFormatters();
        for (ChatFormatter c : chatFormatters)
            if (c.getName().equalsIgnoreCase(name.replace("_", ""))) return c;
        return null;
    }

    static @Nullable ChatFormatter getChatFormatter(char identifierChar) {
        ChatFormatter[] chatFormatters = getChatFormatters();
        for (ChatFormatter c : chatFormatters)
            if (c.getIdentifierChar() == identifierChar)
                return c;
        return null;
    }

    static ChatFormatter @NotNull [] getChatFormatters() {
        return Stream.concat(Arrays.stream(Color.values()), Arrays.stream(Style.values()))
                .map(c -> (ChatFormatter) c)
                .toArray(ChatFormatter[]::new);
    }
}
