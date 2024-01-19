package it.angrybear.enums;

import it.angrybear.interfaces.ChatFormatter;
import lombok.Getter;

/**
 * An enum that represents all Minecraft styles.
 */
@Getter
public enum Style implements ChatFormatter {
    /**
     * Magic or obfuscated style (corresponds to &amp;k).
     * <p>
     * Tag: &#60;obfuscated&#62;
     */
    OBFUSCATED('k'),
    /**
     * Bold style (corresponds to &amp;l).
     * <p>
     * Tag: &#60;bold&#62;
     */
    BOLD('l'),
    /**
     * Strikethrough style (corresponds to &amp;m).
     * <p>
     * Tag: &#60;strikethrough&#62;
     */
    STRIKETHROUGH('m'),
    /**
     * Underline style (corresponds to &amp;n).
     * <p>
     * Tag: &#60;underline&#62;
     */
    UNDERLINE('n'),
    /**
     * Italic style (corresponds to &amp;o).
     * <p>
     * Tag: &#60;italic&#62;
     */
    ITALIC('o'),
    /**
     * Reset style (corresponds to &amp;r).
     * <p>
     * Tag: &#60;reset&#62;
     */
    RESET('r');

    private final char identifierChar;

    Style(char identifierChar) {
        this.identifierChar = identifierChar;
    }
}