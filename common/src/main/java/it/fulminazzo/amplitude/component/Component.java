package it.fulminazzo.amplitude.component;

import it.fulminazzo.amplitude.converter.ComponentConverter;
import it.fulminazzo.fulmicollection.utils.ClassUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
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
 *     <li>&#60;insertion&#62; which creates a new {@link InsertionComponent}</li>
 *     <li>&#60;translatable&#62; which creates a new {@link TranslatableComponent}</li>
 * </ul>
 */
public class Component {
    public static final Pattern TAG_REGEX = Pattern.compile("<((?:\"[^<>]*<|>[^<>]*\"|'[^<>]*<|>[^<>]*'|[^>])+(?:>\\\\\")?)>");
    static final Map<String, Function<String, Component>> CONTAINER_COMPONENTS = new HashMap<>();
    @Getter
    protected Component next;
    @Getter
    protected Color color;
    @Getter
    protected Font font;
    protected Boolean obfuscated;
    protected Boolean bold;
    protected Boolean strikethrough;
    protected Boolean underlined;
    protected Boolean italic;
    protected Boolean reset;
    @Getter
    protected @Nullable String text;

    static {
        Set<Class<?>> classes = ClassUtils.findClassesInPackage(Component.class.getPackage().getName(), Component.class);
        for (Class<?> clazz : classes) {
            if (!Component.class.isAssignableFrom(clazz)) continue;
            if (Modifier.isAbstract(clazz.getModifiers())) continue;
            if (clazz.equals(Component.class)) continue;
            try {
                Constructor<?> constructor = clazz.getConstructor(String.class);
                String className = clazz.getSimpleName().toLowerCase();
                if (className.endsWith("component"))
                    className = className.substring(0, className.length() - "component".length());
                CONTAINER_COMPONENTS.put(className, s -> {
                    try {
                        return (Component) constructor.newInstance(s);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        Throwable cause = e.getCause();
                        if (cause instanceof RuntimeException) throw (RuntimeException) cause;
                        else throw new RuntimeException(cause);
                    }
                });
            } catch (NoSuchMethodException ignored) {
            }
        }
    }

    /**
     * Instantiates a new Text component.
     */
    public Component() {
        this(null);
    }

    /**
     * Instantiates a new Text component.
     *
     * @param rawText the raw text
     */
    public Component(String rawText) {
        setContent(rawText);
    }

    /**
     * Sends the current component to the player.
     *
     * @param <P>    the player type
     * @param player the player
     */
    public <P> void send(final @NotNull P player) {
        ComponentConverter.converter().send(player, this);
    }

    /**
     * Sets content.
     *
     * @param rawText the raw text
     * @return this component
     */
    public @NotNull Component setContent(@Nullable String rawText) {
        setText("");
        setNext((String) null);
        if (rawText == null || rawText.isEmpty()) return this;
        final Matcher matcher = TAG_REGEX.matcher(rawText);

        if (matcher.find()) {
            if (matcher.start() != 0) {
                setText(rawText.substring(0, matcher.start()));
                String remainder = rawText.substring(matcher.start());
                if (getNext() != null) addNext(remainder);
                else setNext(remainder);
                return this;
            } else setText("");

            final String tag = matcher.group(1).split(" ")[0];
            final String fullTag = matcher.group();

            if (this.getClass().equals(Component.class))
                for (String key : CONTAINER_COMPONENTS.keySet())
                    if (tag.equals(key)) {
                        setNext(CONTAINER_COMPONENTS.get(key).apply(rawText));
                        return this;
                    }

            rawText = rawText.substring(fullTag.length());

            ChatFormatter formatter = ChatFormatter.getChatFormatter(tag);
            this.text = rawText;
            if (matcher.find())
                setText(this.text.substring(0, matcher.start() - fullTag.length()));

            rawText = rawText.substring(this.text.length());

            if (formatter != null) {
                if (formatter instanceof Color) this.color = (Color) formatter;
                else if (formatter.equals(Style.RESET)) reset(true);
                else {
                    try {
                        Field field = Component.class.getDeclaredField(formatter.getName());
                        field.setAccessible(true);
                        field.set(this, !tag.startsWith("!"));
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else if (!CONTAINER_COMPONENTS.containsKey(tag)) {
                setText(String.format("<%s>", tag) + this.text);
            }
        } else {
            setText(rawText);
            rawText = "";
        }

        if (rawText.trim().isEmpty()) return this;
        else return getNext() == null ? setNext(rawText) : addNext(rawText);
    }

    /**
     * Add next.
     *
     * @param rawText the raw text
     * @return this component
     */
    public @NotNull Component addNext(@Nullable String rawText) {
        return addNext(rawText == null ? null : Component.fromRaw(rawText));
    }

    /**
     * Add next.
     *
     * @param next the next
     * @return this component
     */
    public @NotNull Component addNext(@Nullable Component next) {
        if (next == null) return this;
        if (this.next != null) this.next.addNext(next);
        else setNext(next);
        return this;
    }

    /**
     * Set the next component (if {@link #isSimilar(Component)} merge it with the current component).
     * Then, apply {@link #setSameOptions(Component)} method.
     *
     * @param rawText the raw text
     * @return this component
     */
    public @NotNull Component setNext(@Nullable String rawText) {
        return setNext(rawText == null ? null : Component.fromRaw(rawText));
    }

    /**
     * Set the next component (if {@link #isSimilar(Component)} merge it with the current component).
     * Then, apply {@link #setSameOptions(Component)} method.
     *
     * @param next the next
     * @return this component
     */
    public @NotNull Component setNext(Component next) {
        this.next = next;

        setSameOptions(this.next);

        while (this.next != null && this.next.isSimilar(this)) {
            String nextText = this.next.text;
            if (nextText != null && !nextText.trim().isEmpty()) this.text += nextText;
            this.next = this.next.next;
        }
        return this;
    }

    /**
     * Checks the given component fields.
     * For any field not given (null) set it to the value of this component.
     *
     * @param component the text component
     * @return this component
     */
    @NotNull Component setSameOptions(@Nullable Component component) {
        if (component == null) return this;

        if (isReset() || component.isReset()) return this;

        try {
            for (Field field : getOptionFields()) {
                if (Component.class.isAssignableFrom(field.getType())) continue;
                if (Modifier.isFinal(field.getModifiers())) continue;
                if (field.getName().equals("reset")) continue;
                Object nextObject = field.get(component);
                if (nextObject != null) continue;
                field.set(component, field.get(this));
            }

            if (component instanceof ContainerComponent)
                component.setSameOptions(((ContainerComponent<?>) component).child);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        component.setSameOptions(component.getNext());
        return this;
    }

    /**
     * Get all the options as an object array.
     *
     * @return the objects
     */
    Object @NotNull [] getOptions() {
        List<Object> objects = new ArrayList<>();
        for (Field field : getOptionFields()) {
            try {
                field.setAccessible(true);
                objects.add(field.get(this));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return objects.toArray(new Object[0]);
    }

    /**
     * Get all the options as a field array.
     *
     * @return the fields
     */
    Field @NotNull [] getOptionFields() {
        List<Field> fields = new ArrayList<>();
        Class<?> clazz = this.getClass();
        // Ignore custom fields
        if (CustomComponent.class.isAssignableFrom(clazz))
            while (clazz != CustomComponent.class) clazz = clazz.getSuperclass();
        if (CustomContainerComponent.class.isAssignableFrom(clazz))
            while (clazz != CustomContainerComponent.class) clazz = clazz.getSuperclass();
        while (Component.class.isAssignableFrom(clazz)) {
            Arrays.stream(clazz.getDeclaredFields())
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    .filter(f -> !Modifier.isFinal(f.getModifiers()))
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
                    try {
                        Field field = Component.class.getDeclaredField(v.name().toLowerCase());
                        field.setAccessible(true);
                        return field.get(this) != null;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(Style[]::new);
    }

    /**
     * Sets text.
     *
     * @param text the text
     * @return this component
     */
    public @NotNull Component setText(final @Nullable String text) {
        String finalText = text;
        if (finalText != null) {
            for (Color color : Color.values())
                finalText = finalText.replaceAll("[§&]" + color.getIdentifierChar(), "<" + color.getName() + ">");
            for (Style style : Style.values())
                finalText = finalText.replaceAll("[§&]" + style.getIdentifierChar(), "<" + style.getName() + ">");
            if (!finalText.equals(text)) return setContent(finalText);
        }
        this.text = text;
        return this;
    }

    /**
     * Sets color.
     *
     * @param color the color
     * @return this component
     */
    public @NotNull Component setColor(Color color) {
        return setColor(color, true);
    }

    /**
     * Sets color.
     *
     * @param color     the color
     * @param propagate if true, use {@link #setSameOptions(Component)} to update the next component
     * @return this component
     */
    public @NotNull Component setColor(Color color, boolean propagate) {
        this.color = color;
        if (propagate) setSameOptions(next);
        return this;
    }

    /**
     * Sets font.
     *
     * @param font the font
     * @return this component
     */
    public @NotNull Component setFont(Font font) {
        return setFont(font, true);
    }

    /**
     * Sets font.
     *
     * @param font      the font
     * @param propagate if true, use {@link #setSameOptions(Component)} to update the next component
     * @return this component
     */
    public @NotNull Component setFont(Font font, boolean propagate) {
        this.font = font;
        if (propagate) setSameOptions(next);
        return this;
    }

    /**
     * Gets obfuscated.
     *
     * @return the obfuscated
     */
    public boolean isObfuscated() {
        return obfuscated != null && obfuscated;
    }

    /**
     * Sets obfuscated.
     *
     * @param obfuscated the obfuscated
     * @return this component
     */
    public @NotNull Component setObfuscated(Boolean obfuscated) {
        return setObfuscated(obfuscated, true);
    }

    /**
     * Sets obfuscated.
     *
     * @param obfuscated the obfuscated
     * @param propagate  if true, use {@link #setSameOptions(Component)} to update the next component
     * @return this component
     */
    public @NotNull Component setObfuscated(Boolean obfuscated, boolean propagate) {
        this.obfuscated = obfuscated;
        if (propagate) setSameOptions(next);
        return this;
    }

    /**
     * Gets bold.
     *
     * @return the bold
     */
    public boolean isBold() {
        return bold != null && bold;
    }

    /**
     * Sets bold.
     *
     * @param bold the bold
     * @return this component
     */
    public @NotNull Component setBold(Boolean bold) {
        return setBold(bold, true);
    }

    /**
     * Sets bold.
     *
     * @param bold      the bold
     * @param propagate if true, use {@link #setSameOptions(Component)} to update the next component
     * @return this component
     */
    public @NotNull Component setBold(Boolean bold, boolean propagate) {
        this.bold = bold;
        if (propagate) setSameOptions(next);
        return this;
    }

    /**
     * Gets strikethrough.
     *
     * @return the strikethrough
     */
    public boolean isStrikethrough() {
        return strikethrough != null && strikethrough;
    }

    /**
     * Sets strikethrough.
     *
     * @param strikethrough the strikethrough
     * @return this component
     */
    public @NotNull Component setStrikethrough(Boolean strikethrough) {
        return setStrikethrough(strikethrough, true);
    }

    /**
     * Sets strikethrough.
     *
     * @param strikethrough the strikethrough
     * @param propagate     if true, use {@link #setSameOptions(Component)} to update the next component
     * @return this component
     */
    public @NotNull Component setStrikethrough(Boolean strikethrough, boolean propagate) {
        this.strikethrough = strikethrough;
        if (propagate) setSameOptions(next);
        return this;
    }

    /**
     * Gets underlined.
     *
     * @return the underlined
     */
    public boolean isUnderlined() {
        return underlined != null && underlined;
    }

    /**
     * Sets underlined.
     *
     * @param underlined the underlined
     * @return this component
     */
    public @NotNull Component setUnderlined(Boolean underlined) {
        return setUnderlined(underlined, true);
    }

    /**
     * Sets underlined.
     *
     * @param underlined the underlined
     * @param propagate  if true, use {@link #setSameOptions(Component)} to update the next component
     * @return this component
     */
    public @NotNull Component setUnderlined(Boolean underlined, boolean propagate) {
        this.underlined = underlined;
        if (propagate) setSameOptions(next);
        return this;
    }

    /**
     * Gets italic.
     *
     * @return the italic
     */
    public boolean isItalic() {
        return italic != null && italic;
    }

    /**
     * Sets italic.
     *
     * @param italic the italic
     * @return this component
     */
    public @NotNull Component setItalic(Boolean italic) {
        return setItalic(italic, true);
    }

    /**
     * Sets italic.
     *
     * @param italic    the italic
     * @param propagate if true, use {@link #setSameOptions(Component)} to update the next component
     * @return this component
     */
    public @NotNull Component setItalic(Boolean italic, boolean propagate) {
        this.italic = italic;
        if (propagate) setSameOptions(next);
        return this;
    }

    /**
     * Gets reset.
     *
     * @return the reset
     */
    public boolean isReset() {
        return reset != null && reset;
    }

    /**
     * Sets reset.
     *
     * @param reset the reset
     * @return this component
     */
    public @NotNull Component reset(@NotNull Boolean reset) {
        return reset(reset, true);
    }

    /**
     * Sets reset.
     *
     * @param reset     the reset
     * @param propagate if true, use {@link #setSameOptions(Component)} to update the next component
     * @return this component
     */
    public @NotNull Component reset(@NotNull Boolean reset, boolean propagate) {
        this.reset = reset;
        if (reset) {
            setColor(Color.WHITE, false);
            for (Style style : Style.values())
                if (style != Style.RESET)
                    setStyle(style, false, false);
            setFont(Font.DEFAULT, false);
            if (propagate) setSameOptions(next);
        }
        return this;
    }

    /**
     * Gets style.
     *
     * @param style the style
     * @return the style
     */
    public Boolean getStyle(@Nullable Style style) {
        if (style == null) return false;
        try {
            Field field = Component.class.getDeclaredField(style.name().toLowerCase());
            field.setAccessible(true);
            return (Boolean) field.get(this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets style.
     *
     * @param style the style
     * @return this component
     */
    public @NotNull Component setStyle(@Nullable Style style) {
        return setStyle(style, true);
    }

    /**
     * Sets style.
     *
     * @param style the style
     * @param value the value
     * @return this component
     */
    public @NotNull Component setStyle(@Nullable Style style, Boolean value) {
        return setStyle(style, value, true);
    }

    /**
     * Sets style.
     *
     * @param style     the style
     * @param value     the value
     * @param propagate if true, use {@link #setSameOptions(Component)} to update the next component
     * @return this component
     */
    public @NotNull Component setStyle(@Nullable Style style, Boolean value, boolean propagate) {
        if (style == null) return this;
        String methodName = style.name();
        methodName = methodName.charAt(0) + methodName.substring(1).toLowerCase();
        if (style != Style.RESET) methodName = "set" + methodName;
        else methodName = methodName.toLowerCase();

        try {
            Method setMethod = Component.class.getDeclaredMethod(methodName, Boolean.class, boolean.class);
            setMethod.invoke(this, value, propagate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Replaces a section in the current text component.
     *
     * @param from the section to replace
     * @param to   the replacement
     * @return the text component
     */
    public @NotNull Component replace(final @NotNull Component from, final @NotNull Component to) {
        return replace(from, to, false);
    }

    /**
     * Replace text component.
     *
     * @param from the section to replace
     * @param to   the replacement
     * @return the text component
     */
    public @NotNull Component replace(final @NotNull String from, final @NotNull String to) {
        return replace(from, to, false);
    }

    /**
     * Replace text component.
     *
     * @param from          the section to replace
     * @param to            the replacement
     * @param maintainColor if true, colors preceding the replacement will be put next to it.
     * @return the text component
     */
    public @NotNull Component replace(final @NotNull Component from, final @NotNull Component to, final boolean maintainColor) {
        return replace(from.serialize(), to.serialize(), maintainColor);
    }

    /**
     * Replace text component.
     *
     * @param from          the section to replace
     * @param to            the replacement
     * @param maintainColor if true, colors preceding the replacement will be put next to it.
     * @return the text component
     */
    public @NotNull Component replace(final @NotNull String from, final @NotNull String to, final boolean maintainColor) {
        final HashSet<ChatFormatter> colors = new LinkedHashSet<>();
        final String serialized = serialize();
        String finalSerialized = "";
        String tmp = "";
        for (char c : serialized.toCharArray()) {
            tmp += c;
            if (tmp.endsWith(from)) {
                tmp = tmp.substring(0, tmp.length() - from.length());
                finalSerialized += tmp;
                finalSerialized += to;
                if (maintainColor) {
                    final Matcher tagMatcher = TAG_REGEX.matcher(tmp);
                    while (tagMatcher.find()) {
                        final ChatFormatter formatter = ChatFormatter.getChatFormatter(tagMatcher.group(1));
                        if (formatter == null) continue;
                        if (formatter == Style.RESET) colors.clear();
                        else if (formatter instanceof Color) colors.removeIf(s -> s instanceof Color);
                        colors.add(formatter);
                    }
                    for (final ChatFormatter color : colors)
                        finalSerialized += "<" + color.getName() + ">";
                }
                tmp = "";
            }
        }
        return Component.fromRaw(finalSerialized + tmp);
    }

    /**
     * Copy all the fields from the given text component to the current one.
     *
     * @param component the text component
     * @return this component modified
     */
    public @NotNull Component copyFrom(final @NotNull Component component) {
        for (Field field : Component.class.getDeclaredFields())
            try {
                if (Modifier.isStatic(field.getModifiers())) continue;
                field.setAccessible(true);
                field.set(this, field.get(component));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        return this;
    }

    /**
     * Recursively check if the given component is contained in the current one.
     *
     * @param component the text component
     * @return true only if one or more components match the criteria
     */
    public boolean contains(final @NotNull Component component) {
        if (strictlyContains(component)) return true;
        else return next != null && next.contains(component);
    }

    /**
     * Check if the given component is contained in the current one.
     * If not, return false instead of checking the next component.
     *
     * @param component the text component
     * @return true only if one or more components match the criteria
     */
    public boolean strictlyContains(final @NotNull Component component) {
        Component next = this.getNext();
        Component cNext = component.getNext();
        final String text = this.getText();
        final String cText = component.getText();
        // Check classes
        if (!this.getClass().equals(component.getClass())) return false;
        // Check options
        if (!component.compareOptions(this)) return false;
        // Check text
        if (text != null) {
            if (cText == null) return false;
            else if (text.startsWith(cText) && cNext == null) return true;
            else if (!text.endsWith(cText)) return false;
        } else if (cText != null) return false;
        // Check next
        Component n1, n2;
        for (n1 = next, n2 = cNext; n1 != null && n2 != null; n1 = n1.getNext(), n2 = n2.getNext())
            if (!n1.strictlyContains(n2)) return false;
        return n2 == null;
    }

    /**
     * Compares this component with the given one.
     *
     * @param component the text component
     * @return true only if all the styles, font and color applied to this component are also applied to the given one and vice versa.
     */
    public boolean allOptionsMatch(@NotNull Component component) {
        return compareOptions(component) && component.compareOptions(this);
    }

    /**
     * Compares this component with the given one.
     *
     * @param component the text component
     * @return true only if all the styles, font and color applied to this component are also applied to the given one
     */
    boolean compareOptions(@NotNull Component component) {
        if (color != null && !color.equals(component.color)) return false;
        if (font != null && !font.equals(component.font)) return false;
        if (obfuscated != null && !obfuscated.equals(component.obfuscated)) return false;
        if (bold != null && !bold.equals(component.bold)) return false;
        if (strikethrough != null && !strikethrough.equals(component.strikethrough)) return false;
        if (underlined != null && !underlined.equals(component.underlined)) return false;
        if (italic != null && !italic.equals(component.italic)) return false;
        return reset == null || reset.equals(component.reset);
    }

    /**
     * Serialize the current component to a raw text.
     *
     * @return the string
     */
    public @NotNull String serialize() {
        String output = serializeSingle();

        if (next != null) {
            final String color = this.color == null ? null : String.format("<%s>", this.color.getName());

            String tmp = next.serialize();

            if (color != null && tmp.startsWith(color)) tmp = tmp.substring(color.length());
            for (Style s : getStyles()) {
                String style = String.format("<%s>", s.getName());
                if (!getStyle(s)) style = "<!" + style.substring(1);
                if (tmp.startsWith(style)) tmp = tmp.substring(style.length());
            }

            output += tmp;
        }

        return output;
    }

    /**
     * Serialize the current component to a raw text ignoring the next components.
     *
     * @return the string
     */
    protected @NotNull String serializeSingle() {
        final String color = this.color == null || isReset() ? null : String.format("<%s>", this.color.getName());
        final List<String> styles = new ArrayList<>();
        if (isReset()) styles.add("<reset>");
        else for (Style style : getStyles()) {
            String s = String.format("<%s>", style.getName());
            if (!getStyle(style)) s = "<!" + s.substring(1);
            styles.add(s);
        }

        String output = "";
        if (color != null) output += color;
        for (String style : styles) output += style;
        if (text != null) output += text;

        return output;
    }

    /**
     * Check if the current text component has no styles, no color and no text applied.
     *
     * @return true if every parameter is null
     */
    public boolean isEmpty() {
        for (Object o : getOptions()) if (o != null) return false;
        return text == null || text.isEmpty();
    }

    /**
     * Clones the current component into another identical one.
     *
     * @param <T> the type of the component
     * @return the clone
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> @NotNull T copy() {
        final String serialize = this.serialize();
        Class<T> clazz = (Class<T>) this.getClass();
        try {
            Constructor<T> constructor = clazz.getConstructor(String.class);
            constructor.setAccessible(true);
            return constructor.newInstance(serialize);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                 InvocationTargetException e) {
            try {
                Constructor<T> constructor = clazz.getConstructor();
                constructor.setAccessible(true);
                T t = constructor.newInstance();
                t.setContent(serialize);
                return t;
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException |
                     InvocationTargetException ex) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Check if two text components are similar.
     * "Similar" means if all their options (from {@link #getOptionFields()}) except {@link #text} are equal.
     *
     * @param component the text component
     * @return true if they are similar
     */
    public boolean isSimilar(@Nullable Component component) {
        if (component == null) return false;
        if (!this.getClass().equals(component.getClass())) return false;
        for (Field option : getOptionFields()) {
            if (option.getName().equals("text")) continue;
            try {
                Object opt1 = option.get(this);
                Object opt2 = option.get(component);
                if (!Objects.equals(opt1, opt2)) return false;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    /**
     * Check if two text components are equal.
     * Uses {@link #isSimilar(Component)} and compares the raw texts.
     *
     * @param component the text component
     * @return true if they are equal
     */
    public boolean equals(@NotNull Component component) {
        return isSimilar(component) && Objects.equals(this.text, component.text);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Component) return equals((Component) o);
        return super.equals(o);
    }

    @Override
    public @NotNull String toString() {
        String output = "{";
        output += "next: " + next + ", ";
        for (Field field : getOptionFields())
            try {
                Object object = field.get(this);
                output += String.format("%s: %s, ", field.getName(), object);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        output += "text: " + text;
        return output + "}";
    }

    /**
     * Converts a raw text to a component.
     *
     * @param rawText the raw text
     * @return the text component
     */
    public static Component fromRaw(@Nullable String rawText) {
        if (rawText == null) return null;
        if (rawText.isEmpty()) return new Component();

        Component component = new Component(rawText);
        while (component.isEmpty()) component = component.getNext();
        return component;
    }

    /**
     * Converts a component to its raw text.
     *
     * @param component the text component
     * @return the string
     */
    public static String toRaw(@Nullable Component component) {
        if (component == null) return null;
        else return component.serialize();
    }

}