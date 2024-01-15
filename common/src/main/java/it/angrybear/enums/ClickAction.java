package it.angrybear.enums;

import it.angrybear.exceptions.InvalidOptionException;
import it.angrybear.interfaces.IAction;
import it.angrybear.interfaces.validators.IntegerValidator;
import it.angrybear.interfaces.validators.OptionValidator;
import it.angrybear.interfaces.validators.URLValidator;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum ClickAction implements IAction {
    OPEN_URL("url", new URLValidator()),
    OPEN_FILE("file", (o, e) -> {
        throw new InvalidOptionException(o, File.class, "Cannot use OPEN_FILE action for security reasons. " +
                "It is present only for saving and displaying screenshots location.");
    }),
    RUN_COMMAND("command", null),
    SUGGEST_COMMAND("command", null),
    CHANGE_PAGE("page", new IntegerValidator()),
    COPY_TO_CLIPBOARD("text", null);

    private final @NotNull Map<String, OptionValidator> requiredOptions;

    ClickAction(String requiredOption, OptionValidator validator) {
        this.requiredOptions = new HashMap<>();
        this.requiredOptions.put(requiredOption, validator);
    }
}