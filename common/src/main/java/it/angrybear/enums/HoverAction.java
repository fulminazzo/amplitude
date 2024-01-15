package it.angrybear.enums;

import it.angrybear.interfaces.IAction;
import it.angrybear.interfaces.validators.ByteValidator;
import it.angrybear.interfaces.validators.OptionValidator;
import it.angrybear.interfaces.validators.UUIDValidator;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum HoverAction implements IAction {
    SHOW_ACHIEVEMENT("id", null),
    // {name: "Zombie", type: "zombie", id: "3f8164bf-1ed-4bcb-96be-7033beed028c"}
    SHOW_ENTITY("name", null, "type", null, "id", new UUIDValidator()),
    // {Count: 1b, id: "netherite_sword", tag: {Damage:0}}
    SHOW_ITEM("Count", new ByteValidator(), "id", null),
    SHOW_TEXT("text", null)
    ;

    private final @NotNull Map<String, OptionValidator> requiredOptions;

    HoverAction(Object @NotNull ... requiredOptions) {
        this.requiredOptions = new HashMap<>();
        for (int i = 0; i < requiredOptions.length; i += 2)
            this.requiredOptions.put((String) requiredOptions[i], (OptionValidator) requiredOptions[i + 1]);
    }
}
