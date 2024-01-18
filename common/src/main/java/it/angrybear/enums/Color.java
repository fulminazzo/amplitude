package it.angrybear.enums;

import it.angrybear.interfaces.ChatFormatter;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * An enum that represents all Minecraft colors.
 */
@Getter
public class Color implements ChatFormatter {
    /**
     * Black color (corresponds to &amp;0).
     * <p>
     * Tag: &#60;black&#62;
     */
    public static final Color BLACK = new Color('0');
    /**
     * Dark blue color (corresponds to &amp;1).
     * <p>
     * Tag: &#60;darkblue&#62; or &#60;dark_blue&#62;
     */
    public static final Color DARK_BLUE = new Color('1');
    /**
     * Dark green color (corresponds to &amp;2).
     * <p>
     * Tag: &#60;darkgreen&#62; or &#60;dark_green&#62;
     */
    public static final Color DARK_GREEN = new Color('2');
    /**
     * Dark aqua color (corresponds to &amp;3).
     * <p>
     * Tag: &#60;darkaqua&#62; or &#60;dark_aqua&#62;
     */
    public static final Color DARK_AQUA = new Color('3');
    /**
     * Dark red color (corresponds to &amp;4).
     * <p>
     * Tag: &#60;darkred&#62; or &#60;dark_red&#62;
     */
    public static final Color DARK_RED = new Color('4');
    /**
     * Dark purple color (corresponds to &amp;5).
     * <p>
     * Tag: &#60;dark_purple&#62; or &#60;dark_purple&#62;
     */
    public static final Color DARK_PURPLE = new Color('5');
    /**
     * Gold color (corresponds to &amp;6).
     * <p>
     * Tag: &#60;gold&#62;
     */
    public static final Color GOLD = new Color('6');
    /**
     * Gray color (corresponds to &amp;7).
     * <p>
     * Tag: &#60;gray&#62;
     */
    public static final Color GRAY = new Color('7');
    /**
     * Dark gray color (corresponds to &amp;8).
     * <p>
     * Tag: &#60;darkgray&#62; or &#60;dark_gray&#62;
     */
    public static final Color DARK_GRAY = new Color('8');
    /**
     * Blue color (corresponds to &amp;9).
     * <p>
     * Tag: &#60;blue&#62;
     */
    public static final Color BLUE = new Color('9');
    /**
     * Green color (corresponds to &amp;a).
     * <p>
     * Tag: &#60;green&#62;
     */
    public static final Color GREEN = new Color('a');
    /**
     * Aqua color (corresponds to &amp;b).
     * <p>
     * Tag: &#60;aqua&#62;
     */
    public static final Color AQUA = new Color('b');
    /**
     * Red color (corresponds to &amp;c).
     * <p>
     * Tag: &#60;red&#62;
     */
    public static final Color RED = new Color('c');
    /**
     * Light purple color (corresponds to &amp;d).
     * <p>
     * Tag: &#60;lightpurple&#62; or &#60;light_purple&#62;
     */
    public static final Color LIGHT_PURPLE = new Color('d');
    /**
     * Yellow color (corresponds to &amp;e).
     * <p>
     * Tag: &#60;yellow&#62;
     */
    public static final Color YELLOW = new Color('e');
    /**
     * White color (corresponds to &amp;f).
     * <p>
     * Tag: &#60;white&#62;
     */
    public static final Color WHITE = new Color('f');

    private final char identifierChar;
    private String code;

    /**
     * Instantiates a new Color.
     *
     * @param identifierChar the identifier char
     */
    Color(char identifierChar) {
        this.identifierChar = identifierChar;
    }

    /**
     * Used to initiate custom colors.
     *
     * @param code the identifier code
     */
    public Color(@NotNull String code) {
        this.identifierChar = '?';
        this.code = code;
    }

    @Override
    public String name() {
        Field[] fields = Color.class.getDeclaredFields();
        for (Field field : fields)
            try {
                if (Modifier.isStatic(field.getModifiers()) && field.get(Color.class).equals(this))
                    return field.getName();
            } catch (ClassCastException ignored) {
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        return "CUSTOM";
    }

    /**
     * Get all the values of this class.
     *
     * @return the values
     */
    public static Color[] values() {
        List<Color> values = new ArrayList<>();
        for (Field field : Color.class.getDeclaredFields())
            try {
                if (Modifier.isStatic(field.getModifiers()))
                    values.add((Color) field.get(Color.class));
            } catch (ClassCastException ignored) {
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        return values.toArray(new Color[0]);
    }

    /**
     * Is custom.
     *
     * @return the boolean
     */
    public boolean isCustom() {
        return identifierChar == '?';
    }

    /**
     * Check if two colors equal.
     *4
     * @param color the color
     * @return the boolean
     */
    public boolean equals(Color color) {
        if (color == null) return false;
        if (identifierChar != '?') return identifierChar == color.identifierChar;
        else return code.equals(color.code);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Color) return equals((Color) o);
        return super.equals(o);
    }

    @Override
    public String toString() {
        return name();
    }
}