package it.angrybear.enums;

import it.angrybear.interfaces.ChatFormatter;
import lombok.Getter;

@Getter
public enum Color implements ChatFormatter {
    BLACK('0'),
    DARK_BLUE('1'),
    DARK_GREEN('2'),
    DARK_AQUA('3'),
    DARK_RED('4'),
    DARK_PURPLE('5'),
    GOLD('6'),
    GRAY('7'),
    DARK_GRAY('8'),
    BLUE('9'),
    GREEN('a'),
    AQUA('b'),
    RED('c'),
    LIGHT_PURPLE('d'),
    YELLOW('e'),
    WHITE('f');

    private final char identifierChar;

    Color(char identifierChar) {
        this.identifierChar = identifierChar;
    }
}