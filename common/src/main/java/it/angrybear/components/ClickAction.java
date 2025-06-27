package it.angrybear.components;

import it.angrybear.exceptions.InvalidOptionException;
import it.angrybear.interfaces.IAction;
import it.angrybear.validator.IntegerValidator;
import it.angrybear.validator.OptionValidator;
import it.angrybear.validator.URLValidator;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * An enum that represents all the possible action when a ClickEvent is triggered.
 */
@Getter
public enum ClickAction implements IAction {
    /**
     * Open url action.
     * Requires "url" as the url.
     * <p>
     * Example: &#60;click action="OPEN_URL" url="https://fulminazzo.it"&#62;Click event!&#60;/click&#62;
     */
    OPEN_URL("url", new URLValidator()),
    /**
     * Open file action.
     * Requires "file" as the file.
     * <p>
     * Example: &#60;click action="OPEN_FILE" file="~/Desktop/file.txt"&#62;Click event!&#60;/click&#62;
     *
     * @deprecated
     * This action is present just to match the Minecraft counterpart.
     * It is only used for displaying the location of screenshots on the clientside.
     * As such, it should not be used since it might cause errors.
     */
    @Deprecated
    OPEN_FILE("file", (o, e) -> {
        throw new InvalidOptionException(o, File.class, "Cannot use OPEN_FILE action for security reasons. " +
                "It is present only for saving and displaying screenshots location.");
    }),
    /**
     * Run command action.
     * Requires "command" as the command.
     * <p>
     * Example: &#60;click action="RUN_COMMAND" command="/say Hello world!"&#62;Click event!&#60;/click&#62;
     */
    RUN_COMMAND("command", null),
    /**
     * Suggest command action.
     * Requires "command" as the command.
     * <p>
     * Example: &#60;click action="SUGGEST_COMMAND" command="/say Hello world!"&#62;Click event!&#60;/click&#62;
     */
    SUGGEST_COMMAND("command", null),
    /**
     * Change page action.
     * Requires "page" as integer.
     * <p>
     * Example: &#60;click action="CHANGE_PAGE" page=1&#62;Click event!&#60;/click&#62;
     */
    CHANGE_PAGE("page", new IntegerValidator()),
    /**
     * Copy to clipboard action.
     * Requires "text" as the copied text.
     * <p>
     * Example: &#60;click action="COPY_TO_CLIPBOARD" text="I hacked you!"&#62;Click event!&#60;/click&#62;
     */
    COPY_TO_CLIPBOARD("text", null);

    private final @NotNull Map<String, OptionValidator> requiredOptions;

    ClickAction(String requiredOption, OptionValidator validator) {
        this.requiredOptions = new HashMap<>();
        this.requiredOptions.put(requiredOption, validator);
    }
}