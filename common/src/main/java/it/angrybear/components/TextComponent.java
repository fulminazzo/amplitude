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

    public TextComponent() {
        this(null);
    }

    public TextComponent(String rawText) {
        setContent(rawText);
    }

    public void setContent(@Nullable String rawText) {
        if (CONTAINER_COMPONENTS.isEmpty()) {
            CONTAINER_COMPONENTS.put("click", ClickComponent::new);
            CONTAINER_COMPONENTS.put("hover", HoverComponent::new);
            CONTAINER_COMPONENTS.put("hex", s -> new ClickComponent());
        }

        if (rawText == null || rawText.isEmpty()) return;
        this.text = null;
        final Matcher matcher = TAG_REGEX.matcher(rawText);

        if (matcher.find()) {
            if (matcher.start() != 0) {
                this.text = rawText.substring(0, matcher.start());
                setNext(rawText.substring(matcher.start()));
                return;
            }

            final String tag = matcher.group(1).split(" ")[0];
            final String fullTag = matcher.group();

            for (String key : CONTAINER_COMPONENTS.keySet())
                if (tag.equals(key)) {
                    setNext(CONTAINER_COMPONENTS.get(key).apply(rawText));
                    return;
                }

            rawText = rawText.substring(fullTag.length());

            ChatFormatter formatter = ChatFormatter.getChatFormatter(tag);
            this.text = rawText;
            if (matcher.find()) {
                this.text = this.text.substring(0, matcher.start() - fullTag.length());
                matcher.reset();
            }
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

            } else {
                this.text = String.format("<%s>", tag) + this.text;
            }
        } else {
            this.text = rawText;
            rawText = "";
        }

        if (rawText.trim().isEmpty()) return;
        setNext(rawText);
    }

    public void setNext(String rawText) {
        setNext(new TextComponent(rawText));
    }

    public void setNext(TextComponent next) {
        this.next = next;

        setSameOptions(this.next);

        while (this.next != null && this.next.isSimilar(this)) {
            String nextText = this.next.text;
            if (nextText != null && !nextText.trim().isEmpty()) this.text += nextText;
            this.next = this.next.next;
        }
    }

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

    public void setColor(Color color) {
        setColor(color, true);
    }

    public void setColor(Color color, boolean propagate) {
        this.color = color;
        if (propagate) setSameOptions(next);
    }

    public boolean getMagic() {
        return magic != null && magic;
    }

    public void setMagic(Boolean magic) {
        setMagic(magic, true);
    }

    public void setMagic(Boolean magic, boolean propagate) {
        this.magic = magic;
        if (propagate) setSameOptions(next);
    }

    public boolean getBold() {
        return bold != null && bold;
    }

    public void setBold(Boolean bold) {
        setBold(bold, true);
    }

    public void setBold(Boolean bold, boolean propagate) {
        this.bold = bold;
        if (propagate) setSameOptions(next);
    }

    public boolean getStrikethrough() {
        return strikethrough != null && strikethrough;
    }

    public void setStrikethrough(Boolean strikethrough) {
        setStrikethrough(strikethrough, true);
    }

    public void setStrikethrough(Boolean strikethrough, boolean propagate) {
        this.strikethrough = strikethrough;
        if (propagate) setSameOptions(next);
    }

    public boolean getUnderline() {
        return underline != null && underline;
    }

    public void setUnderline(Boolean underline) {
        setUnderline(underline, true);
    }

    public void setUnderline(Boolean underline, boolean propagate) {
        this.underline = underline;
        if (propagate) setSameOptions(next);
    }

    public boolean getItalic() {
        return italic != null && italic;
    }

    public void setItalic(Boolean italic) {
        setItalic(italic, true);
    }

    public void setItalic(Boolean italic, boolean propagate) {
        this.italic = italic;
        if (propagate) setSameOptions(next);
    }

    public boolean getReset() {
        return reset != null && reset;
    }

    public void setReset(Boolean reset) {
        setReset(reset, true);
    }

    public void setReset(Boolean reset, boolean propagate) {
        this.reset = reset;
        color = Color.WHITE;
        bold = false;
        italic = false;
        strikethrough = false;
        underline = false;
        if (propagate) setSameOptions(next);
    }

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

    public void setStyle(Style style) {
        setStyle(style, true);
    }

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

    public boolean equals(@NotNull TextComponent textComponent) {
        return isSimilar(textComponent) && Objects.equals(this.text, textComponent.text);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TextComponent) return equals((TextComponent) o);
        return super.equals(o);
    }
}