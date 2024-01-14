package it.angrybear.components;

import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import it.angrybear.interfaces.ChatFormatter;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Getter
@Setter
public class TextComponent {
    public static final Pattern TAG_REGEX = Pattern.compile("<([^\n>]+)>");
    private TextComponent next;
    private Color color;
    private Boolean magic;
    private Boolean bold;
    private Boolean strikethrough;
    private Boolean underline;
    private Boolean italic;
    private Boolean reset;
    private String text;

    public TextComponent() {
        this(null);
    }

    public TextComponent(String rawText) {
        setContent(rawText);
    }

    public void setContent(String rawText) {
        if (rawText == null || rawText.isEmpty()) return;
        Matcher matcher = TAG_REGEX.matcher(rawText);
        if (matcher.find()) {
            String tag = matcher.group(1);
            String fullTag = matcher.group();
            rawText = rawText.substring(fullTag.length());

            ChatFormatter formatter = ChatFormatter.getChatFormatter(tag);
            this.text = rawText;

            if (formatter != null) {
                if (matcher.find())
                    this.text = this.text.substring(0, matcher.end() - matcher.group().length() - fullTag.length());
                rawText = rawText.substring(this.text.length());

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
                rawText = "";
            }
        }
        if (rawText.trim().isEmpty()) return;
        this.next = new TextComponent(rawText);

        TextComponent next = this.next;
        while (next != null)
            try {
                if (!next.getReset())
                    for (Field field : getOptions()) {
                        Object nextObject = field.get(next);
                        if (nextObject != null) continue;
                        field.set(next, field.get(this));
                    }
                next = next.getNext();
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
    }

    public void setReset(Boolean reset) {
        this.reset = reset;
        color = Color.EMPTY;
        bold = false;
        italic = false;
        strikethrough = false;
        underline = false;
    }

    public void reset() {
        try {
            for (Field field : getOptions()) field.set(this, null);
            reset = true;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Field[] getOptions() {
        return Arrays.stream(this.getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .filter(f -> !f.getName().equals("text"))
                .filter(f -> !f.getName().equals("next"))
                .peek(f -> f.setAccessible(true))
                .toArray(Field[]::new);
    }

    public Boolean getMagic() {
        return magic != null && magic;
    }

    public Boolean getBold() {
        return bold != null && bold;
    }

    public Boolean getStrikethrough() {
        return strikethrough != null && strikethrough;
    }

    public Boolean getUnderline() {
        return underline != null && underline;
    }

    public Boolean getItalic() {
        return italic != null && italic;
    }

    public Boolean getReset() {
        return reset != null && reset;
    }

    @Override
    public String toString() {
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