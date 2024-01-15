package it.angrybear.enums;

import it.angrybear.interfaces.ChatFormatter;
import lombok.Getter;

/**
 * An enum that represents all Minecraft colors.
 */
@Getter
public enum Color implements ChatFormatter {
    /**
     * Black color (corresponds to &amp;0).
     * <p>
     * Tag: &#60;black&#62;
     */
    BLACK('0'),
    /**
     * Dark blue color (corresponds to &amp;1).
     * <p>
     * Tag: &#60;darkblue&#62; or &#60;dark_blue&#62;
     */
    DARK_BLUE('1'),
    /**
     * Dark green color (corresponds to &amp;2).
     * <p>
     * Tag: &#60;darkgreen&#62; or &#60;dark_green&#62;
     */
    DARK_GREEN('2'),
    /**
     * Dark aqua color (corresponds to &amp;3).
     * <p>
     * Tag: &#60;darkaqua&#62; or &#60;dark_aqua&#62;
     */
    DARK_AQUA('3'),
    /**
     * Dark red color (corresponds to &amp;4).
     * <p>
     * Tag: &#60;darkred&#62; or &#60;dark_red&#62;
     */
    DARK_RED('4'),
    /**
     * Dark purple color (corresponds to &amp;5).
     * <p>
     * Tag: &#60;dark_purple&#62; or &#60;dark_purple&#62;
     */
    DARK_PURPLE('5'),
    /**
     * Gold color (corresponds to &amp;6).
     * <p>
     * Tag: &#60;gold&#62;
     */
    GOLD('6'),
    /**
     * Gray color (corresponds to &amp;7).
     * <p>
     * Tag: &#60;gray&#62;
     */
    GRAY('7'),
    /**
     * Dark gray color (corresponds to &amp;8).
     * <p>
     * Tag: &#60;darkgray&#62; or &#60;dark_gray&#62;
     */
    DARK_GRAY('8'),
    /**
     * Blue color (corresponds to &amp;9).
     * <p>
     * Tag: &#60;blue&#62;
     */
    BLUE('9'),
    /**
     * Green color (corresponds to &amp;a).
     * <p>
     * Tag: &#60;green&#62;
     */
    GREEN('a'),
    /**
     * Aqua color (corresponds to &amp;b).
     * <p>
     * Tag: &#60;aqua&#62;
     */
    AQUA('b'),
    /**
     * Red color (corresponds to &amp;c).
     * <p>
     * Tag: &#60;red&#62;
     */
    RED('c'),
    /**
     * Light purple color (corresponds to &amp;d).
     * <p>
     * Tag: &#60;lightpurple&#62; or &#60;light_purple&#62;
     */
    LIGHT_PURPLE('d'),
    /**
     * Yellow color (corresponds to &amp;e).
     * <p>
     * Tag: &#60;yellow&#62;
     */
    YELLOW('e'),
    /**
     * White color (corresponds to &amp;f).
     * <p>
     * Tag: &#60;white&#62;
     */
    WHITE('f');

    private final char identifierChar;

    Color(char identifierChar) {
        this.identifierChar = identifierChar;
    }
}