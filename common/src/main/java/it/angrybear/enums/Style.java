package it.angrybear.enums;

import it.angrybear.interfaces.ChatFormatter;
import lombok.Getter;

@Getter
public enum Style implements ChatFormatter {
    MAGIC('k'),
    BOLD('l'),
    STRIKETHROUGH('m'),
    UNDERLINE('n'),
    ITALIC('o'),
    RESET('r');

    private final char identifierChar;

    Style(char identifierChar) {
        this.identifierChar = identifierChar;
    }
}