package it.angrybear.enums;

import it.angrybear.interfaces.ChatFormatter;
import lombok.Getter;

/**
 * An enum that represents all Minecraft colors.
 */
@Getter
public enum Color implements ChatFormatter {
    /**
     * Black color (corresponds to &k).
     * Tag: &#60;black&#62;
     */
    BLACK('0'),
    /**
     * Dark blue color (corresponds to &1).
     * Tag: &#60;darkblue&#62 or &#60;dark_blue&#62;
     */
    DARK_BLUE('1'),
    /**
     * Dark green color (corresponds to &2).
     * Tag: &#60;darkgreen&#62 or &#60;dark_green&#62;
     */
    DARK_GREEN('2'),
    /**
     * Dark aqua color (corresponds to &3).
     * Tag: &#60;darkaqua&#62 or &#60;dark_aqua&#62;
     */
    DARK_AQUA('3'),
    /**
     * Dark red color (corresponds to &4).
     * Tag: &#60;darkred&#62 or &#60;dark_red&#62;
     */
    DARK_RED('4'),
    /**
     * Dark purple color (corresponds to &5).
     * Tag: &#60;dark_purple&#62 or &#60;dark_purple&#62;
     */
    DARK_PURPLE('5'),
    /**
     * Gold color (corresponds to &6).
     * Tag: &#60;gold&#62;
     */
    GOLD('6'),
    /**
     * Gray color (corresponds to &7).
     * Tag: &#60;gray&#62;
     */
    GRAY('7'),
    /**
     * Dark gray color (corresponds to &8).
     * Tag: &#60;darkgray&#62 or &#60;dark_gray&#62;
     */
    DARK_GRAY('8'),
    /**
     * Blue color (corresponds to &9).
     * Tag: &#60;blue&#62;
     */
    BLUE('9'),
    /**
     * Green color (corresponds to &a).
     * Tag: &#60;green&#62;
     */
    GREEN('a'),
    /**
     * Aqua color (corresponds to &b).
     * Tag: &#60;aqua&#62;
     */
    AQUA('b'),
    /**
     * Red color (corresponds to &c).
     * Tag: &#60;red&#62;
     */
    RED('c'),
    /**
     * Light purple color (corresponds to &d).
     * Tag: &#60;lightpurple&#62 or &#60;light_purple&#62;
     */
    LIGHT_PURPLE('d'),
    /**
     * Yellow color (corresponds to &e).
     * Tag: &#60;yellow&#62;
     */
    YELLOW('e'),
    /**
     * White color (corresponds to &f).
     * Tag: &#60;white&#62;
     */
    WHITE('f');

    private final char identifierChar;

    Color(char identifierChar) {
        this.identifierChar = identifierChar;
    }
}