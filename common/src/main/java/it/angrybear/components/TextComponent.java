package it.angrybear.components;

import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import it.angrybear.interfaces.ChatFormatter;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main component for working with messages.
 * This component is able to read and intercept used tags throughout a raw string using {@link #setContent(String)}.
 * Then, it parses them accordingly.
 * <p>
 * Supported values:
 * <ul>
 *     <li>&#60;<i>color</i>&#62; with colors taken from {@link Color}</li>
 *     <li>&#60;<i>style</i>&#62; with styles taken from {@link Style}</li>
 *     <li>&#60;hex&#62; which creates a new {@link HexComponent}</li>
 *     <li>&#60;click&#62; which creates a new {@link ClickComponent}</li>
 *     <li>&#60;hover&#62; which creates a new {@link HoverComponent}</li>
 * </ul>
 */
@Getter
public class TextComponent {
    public static final Map<String, Function<String, OptionComponent>> CONTAINER_COMPONENTS = new HashMap<>();
    public static final Pattern TAG_REGEX = Pattern.compile("<((?:\".*>.*\"|'.*>.*'|[^>])+)>");
    protected TextComponent next;
    protected Color color;
    protected Boolean magic;
    protected Boolean bold;
    protected Boolean strikethrough;
    protected Boolean underline;
    protected Boolean italic;
    protected Boolean reset;
    protected @Nullable String text;

    /**
     * Instantiates a new Text component.
     */
    public TextComponent() {
        this(null);
    }

    /**
     * Instantiates a new Text component.
     *
     * @param rawText the raw text
     */
    public TextComponent(String rawText) {
        setContent(rawText);
    }

    /**
     * Sets content.
     *
     * @param rawText the raw text
     */
    public void setContent(@Nullable String rawText) {
        if (CONTAINER_COMPONENTS.isEmpty()) {
            CONTAINER_COMPONENTS.put("click", ClickComponent::new);
            CONTAINER_COMPONENTS.put("hover", HoverComponent::new);
            CONTAINER_COMPONENTS.put("hex", HexComponent::new);
        }

        if (rawText == null || rawText.isEmpty()) return;
        this.text = null;
        final Matcher matcher = TAG_REGEX.matcher(rawText);

        if (matcher.find()) {
            if (matcher.start() != 0) {
                this.text = rawText.substring(0, matcher.start());
                setNext(rawText.substring(matcher.start()));
                return;
            } else this.text = "";

            final String tag = matcher.group(1).split(" ")[0];
            final String fullTag = matcher.group();

            if (this.getClass().equals(TextComponent.class))
                for (String key : CONTAINER_COMPONENTS.keySet())
                    if (tag.equals(key)) {
                        setNext(CONTAINER_COMPONENTS.get(key).apply(rawText));
                        return;
                    }

            rawText = rawText.substring(fullTag.length());

            ChatFormatter formatter = ChatFormatter.getChatFormatter(tag);
            this.text = rawText;
            if (matcher.find())
                this.text = this.text.substring(0, matcher.start() - fullTag.length());

            rawText = rawText.substring(this.text.length());

            if (formatter != null) {
                if (formatter instanceof Color) this.color = (Color) formatter;
                else if (formatter.equals(Style.RESET)) reset();
                else {
                    try {
                        Field field = this.getClass().getDeclaredField(formatter.getName());
                        field.setAccessible(true);
                        field.set(this, true);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }

            } else if (!CONTAINER_COMPONENTS.containsKey(tag)) {
                this.text = String.format("<%s>", tag) + this.text;
            }
        } else {
            this.text = rawText;
            rawText = "";
        }

        if (rawText.trim().isEmpty()) return;
        setNext(rawText);
    }

    /**
     * Set the next component (if {@link #isSimilar(TextComponent)} merge it with the current component).
     * Then, apply {@link #setSameOptions(TextComponent)} method.
     *
     * @param rawText the raw text
     */
    public void setNext(@Nullable String rawText) {
        setNext(rawText == null ? null : new TextComponent(rawText));
    }

    /**
     * Set the next component (if {@link #isSimilar(TextComponent)} merge it with the current component).
     * Then, apply {@link #setSameOptions(TextComponent)} method.
     *
     * @param next the next
     */
    public void setNext(TextComponent next) {
        this.next = next;

        setSameOptions(this.next);

        while (this.next != null && this.next.isSimilar(this)) {
            String nextText = this.next.text;
            if (nextText != null && !nextText.trim().isEmpty()) this.text += nextText;
            this.next = this.next.next;
        }
    }

    /**
     * Checks the given component fields.
     * For any field not given (null) set it to the value of this component.
     *
     * @param textComponent the text component
     */
    public void setSameOptions(TextComponent textComponent) {
        TextComponent tmp = textComponent;
        while (tmp != null)
            try {
                if (!tmp.getReset())
                    for (Field field : getOptions()) {
                        if (TextComponent.class.isAssignableFrom(field.getType())) continue;
                        if (Modifier.isFinal(field.getModifiers())) continue;
                        Object nextObject = field.get(tmp);
                        if (nextObject != null) continue;
                        field.set(tmp, field.get(this));
                    }

                if (tmp instanceof ContainerComponent)
                    tmp.setSameOptions(((ContainerComponent) tmp).child);

                tmp = tmp.getNext();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
    }

    /**
     * Reset the formatting using {@link Style#RESET}.
     */
    public void reset() {
        try {
            for (Field field : getOptions())
                if (!Modifier.isFinal(field.getModifiers()))
                    field.set(this, null);
            reset = true;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get all the options as a field array.
     *
     * @return the fields
     */
    public Field @NotNull [] getOptions() {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = this.getClass();
        while (TextComponent.class.isAssignableFrom(clazz)) {
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    .filter(f -> !f.getName().equals("this$0"))
                    .filter(f -> !f.getName().equals("text"))
                    .filter(f -> !f.getName().equals("next"))
                    .peek(f -> f.setAccessible(true))
                    .forEach(fields::add);
            clazz = clazz.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }

    /**
     * Get all the applied styles.
     *
     * @return the styles.
     */
    public Style @NotNull [] getStyles() {
        return Arrays.stream(Style.values())
                .filter(v -> {
                    String name = v.getName();
                    String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    try {
                        Method method = this.getClass().getMethod(methodName);
                        return method.invoke(this).equals(true);
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(Style[]::new);
    }

    /**
     * Sets color.
     *
     * @param color the color
     */
    public void setColor(Color color) {
        setColor(color, true);
    }

    /**
     * Sets color.
     *
     * @param color     the color
     * @param propagate if true, use {@link #setSameOptions(TextComponent)} to update the next component
     */
    public void setColor(Color color, boolean propagate) {
        this.color = color;
        if (propagate) setSameOptions(next);
    }

    /**
     * Gets magic.
     *
     * @return the magic
     */
    public boolean getMagic() {
        return magic != null && magic;
    }

    /**
     * Sets magic.
     *
     * @param magic the magic
     */
    public void setMagic(Boolean magic) {
        setMagic(magic, true);
    }

    /**
     * Sets magic.
     *
     * @param magic     the magic
     * @param propagate if true, use {@link #setSameOptions(TextComponent)} to update the next component
     */
    public void setMagic(Boolean magic, boolean propagate) {
        this.magic = magic;
        if (propagate) setSameOptions(next);
    }

    /**
     * Gets bold.
     *
     * @return the bold
     */
    public boolean getBold() {
        return bold != null && bold;
    }

    /**
     * Sets bold.
     *
     * @param bold the bold
     */
    public void setBold(Boolean bold) {
        setBold(bold, true);
    }

    /**
     * Sets bold.
     *
     * @param bold      the bold
     * @param propagate if true, use {@link #setSameOptions(TextComponent)} to update the next component
     */
    public void setBold(Boolean bold, boolean propagate) {
        this.bold = bold;
        if (propagate) setSameOptions(next);
    }

    /**
     * Gets strikethrough.
     *
     * @return the strikethrough
     */
    public boolean getStrikethrough() {
        return strikethrough != null && strikethrough;
    }

    /**
     * Sets strikethrough.
     *
     * @param strikethrough the strikethrough
     */
    public void setStrikethrough(Boolean strikethrough) {
        setStrikethrough(strikethrough, true);
    }

    /**
     * Sets strikethrough.
     *
     * @param strikethrough the strikethrough
     * @param propagate     if true, use {@link #setSameOptions(TextComponent)} to update the next component
     */
    public void setStrikethrough(Boolean strikethrough, boolean propagate) {
        this.strikethrough = strikethrough;
        if (propagate) setSameOptions(next);
    }

    /**
     * Gets underline.
     *
     * @return the underline
     */
    public boolean getUnderline() {
        return underline != null && underline;
    }

    /**
     * Sets underline.
     *
     * @param underline the underline
     */
    public void setUnderline(Boolean underline) {
        setUnderline(underline, true);
    }

    /**
     * Sets underline.
     *
     * @param underline the underline
     * @param propagate if true, use {@link #setSameOptions(TextComponent)} to update the next component
     */
    public void setUnderline(Boolean underline, boolean propagate) {
        this.underline = underline;
        if (propagate) setSameOptions(next);
    }

    /**
     * Gets italic.
     *
     * @return the italic
     */
    public boolean getItalic() {
        return italic != null && italic;
    }

    /**
     * Sets italic.
     *
     * @param italic the italic
     */
    public void setItalic(Boolean italic) {
        setItalic(italic, true);
    }

    /**
     * Sets italic.
     *
     * @param italic    the italic
     * @param propagate if true, use {@link #setSameOptions(TextComponent)} to update the next component
     */
    public void setItalic(Boolean italic, boolean propagate) {
        this.italic = italic;
        if (propagate) setSameOptions(next);
    }

    /**
     * Gets reset.
     *
     * @return the reset
     */
    public boolean getReset() {
        return reset != null && reset;
    }

    /**
     * Sets reset.
     *
     * @param reset the reset
     */
    public void setReset(Boolean reset) {
        setReset(reset, true);
    }

    /**
     * Sets reset.
     *
     * @param reset     the reset
     * @param propagate if true, use {@link #setSameOptions(TextComponent)} to update the next component
     */
    public void setReset(Boolean reset, boolean propagate) {
        this.reset = reset;
        color = Color.WHITE;
        bold = false;
        italic = false;
        strikethrough = false;
        underline = false;
        if (propagate) setSameOptions(next);
    }

    /**
     * Gets style.
     *
     * @param style the style
     * @return the style
     */
    public boolean getStyle(@Nullable Style style) {
        if (style == null) return false;
        String methodName = style.name();
        methodName = methodName.charAt(0) + methodName.substring(1).toLowerCase();

        try {
            Method getMethod = TextComponent.class.getDeclaredMethod("get" + methodName);
            return (boolean) getMethod.invoke(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets style.
     *
     * @param style the style
     */
    public void setStyle(Style style) {
        setStyle(style, true);
    }

    /**
     * Sets style.
     *
     * @param style     the style
     * @param propagate if true, use {@link #setSameOptions(TextComponent)} to update the next component
     */
    public void setStyle(@Nullable Style style, boolean propagate) {
        if (style == null) return;
        String methodName = style.name();
        methodName = methodName.charAt(0) + methodName.substring(1).toLowerCase();

        try {
            Method setMethod = TextComponent.class.getDeclaredMethod("set" + methodName, Boolean.class, boolean.class);
            setMethod.invoke(this, true, propagate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Check if two text components are similar.
     * "Similar" means if all their options (from {@link #getOptions()}) except {@link #text} are equal.
     *
     * @param textComponent the text component
     * @return true if they are similar
     */
    public boolean isSimilar(@Nullable TextComponent textComponent) {
        if (textComponent == null) return false;
        if (!this.getClass().equals(textComponent.getClass())) return false;
        for (Field option : getOptions()) {
            if (option.getName().equals("text")) continue;
            try {
                Object opt1 = option.get(this);
                Object opt2 = option.get(textComponent);
                if (!Objects.equals(opt1, opt2)) return false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    /**
     * Check if two text components are equal.
     * Uses {@link #isSimilar(TextComponent)} and compares the raw texts.
     *
     * @param textComponent the text component
     * @return true if they are equal
     */
    public boolean equals(@NotNull TextComponent textComponent) {
        return isSimilar(textComponent) && Objects.equals(this.text, textComponent.text);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TextComponent) return equals((TextComponent) o);
        return super.equals(o);
    }

    @Override
    public @NotNull String toString() {
        String output = "{";
        output += "next: " + next + ", ";
        for (Field field : getOptions())
            try {
                Object object = field.get(this);
                output += String.format("%s: %s, ", field.getName(), object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        output += "text: " + text;
        return output + "}";
    }
}