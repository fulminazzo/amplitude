package it.fulminazzo.amplitude.component;

import it.fulminazzo.amplitude.util.StringUtils;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of {@link ContainerComponent} that allows creating translatable texts.
 * These are translated using Minecraft default translations (so they will not work for general phrases).
 * To pass any argument, you can specify the non-mandatory option "arguments" separated with '&amp;'.
 * <p>
 * Example: \"&#60;translatable arguments="Diamond Sword&amp;1&amp;\"Alex &amp; friends\""&#62;commands.give.success.single&#60;/translate&#62;\"
 * will create:
 * <ul>
 *     <li><i>Diamond Sword</i> as first argument</li>
 *     <li><i>1</i> as second argument</li>
 *     <li><i>Alex &amp; friends</i> as third argument</li>
 *     <li><b>commands.give.success.single</b> as content to be translated</li>
 * </ul>
 * The result in chat will be: "Gave Diamond Sword 1 to Alex &amp; friends".
 */
@Getter
public final class TranslatableComponent extends ContainerComponent {
    private List<TextComponent> arguments;

    /**
     * Instantiates a new Translatable component.
     */
    public TranslatableComponent() {
        this(null);
    }

    /**
     * Instantiates a new Translatable component.
     *
     * @param rawText the raw text
     */
    public TranslatableComponent(final @Nullable String rawText) {
        super(rawText, "translatable");
    }

    @Override
    public void setOptions(final @Nullable String rawText) {
        super.setOptions(rawText);

        final String rawArguments = getTagOption("arguments");
        if (rawArguments == null) return;
        if (this.arguments == null) this.arguments = new LinkedList<>();
        setArguments(StringUtils.splitQuoteSensitive(rawArguments, '&'));
    }

    @Override
    public Field @NotNull [] getOptionFields() {
        return Arrays.stream(super.getOptionFields()).filter(f -> !f.getName().equals("arguments")).toArray(Field[]::new);
    }

    /**
     * Add argument.
     *
     * @param rawArgument the raw argument
     */
    public void addArgument(final @Nullable String rawArgument) {
        if (rawArgument != null) this.arguments.add(TextComponent.fromRaw(rawArgument));
    }

    /**
     * Add argument.
     *
     * @param textComponent the text component
     */
    public void addArgument(final @Nullable TextComponent textComponent) {
        if (textComponent != null) this.arguments.add(textComponent);
    }

    /**
     * Remove argument.
     *
     * @param textComponent the text component
     */
    public void removeArgument(final @Nullable TextComponent textComponent) {
        if (textComponent != null) this.arguments.removeIf(t -> t.equals(textComponent));
    }

    /**
     * Sets arguments.
     *
     * @param rawArguments the raw arguments
     */
    public void setArguments(final String @Nullable ... rawArguments) {
        List<TextComponent> arguments = null;
        if (rawArguments != null) {
            arguments = new ArrayList<>();
            for (String arg : rawArguments) {
                if (arg.startsWith("\"") && arg.endsWith("\"") || arg.startsWith("'") && arg.endsWith("'"))
                    arg = arg.substring(1, arg.length() - 1);
                arguments.add(TextComponent.fromRaw(arg));
            }
        }
        setArguments(arguments);
    }

    /**
     * Sets arguments.
     *
     * @param arguments the arguments
     */
    public void setArguments(final @Nullable List<TextComponent> arguments) {
        this.arguments.clear();
        if (arguments != null) this.arguments.addAll(arguments);
    }

}
