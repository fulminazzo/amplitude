package it.angrybear.enums;

import it.angrybear.interfaces.ChatFormatter;
import lombok.Getter;

/**
 * An enum that represents all Minecraft styles.
 */
@Getter
public enum Style implements ChatFormatter {
    /**
     * Magic or obfuscated style (corresponds to &k).
     * Tag: &#60;magic&#62;
     */
    MAGIC('k'),
    /**
     * Bold style (corresponds to &l).
     * Tag: &#60;bold&#62;
     */
    BOLD('l'),
    /**
     * Strikethrough style (corresponds to &m).
     * Tag: &#60;strikethrough&#62;
     */
    STRIKETHROUGH('m'),
    /**
     * Underline style (corresponds to &n).
     * Tag: &#60;underline&#62;
     */
    UNDERLINE('n'),
    /**
     * Italic style (corresponds to &o).
     * Tag: &#60;italic&#62;
     */
    ITALIC('o'),
    /**
     * Reset style (corresponds to &r).
     * Tag: &#60;reset&#62;
     */
    RESET('r');

    private final char identifierChar;

    Style(char identifierChar) {
        this.identifierChar = identifierChar;
    }
}