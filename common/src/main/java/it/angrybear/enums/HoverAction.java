package it.angrybear.enums;

import it.angrybear.interfaces.IAction;
import it.angrybear.interfaces.validators.ByteValidator;
import it.angrybear.interfaces.validators.OptionValidator;
import it.angrybear.interfaces.validators.UUIDValidator;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * An enum that represents all the possible action when a HoverEvent is triggered.
 */
@Getter
public enum HoverAction implements IAction {
    /**
     * Show achievement action.
     * Requires "id" as the achievement id.
     * <p>
     * Example: &#60;hover action="SHOW_ACHIEVEMENT" id="achievement.mineWood"&#62;Hover event!&#60;/hover&#62;
     * <p>
     * or
     * <p>
     * Example: &#60;hover action="SHOW_ACHIEVEMENT" json="{ id: \"achievement.mineWood\" }"&#62;Hover event!&#60;/hover&#62;
     * <p>
     * Removed in Minecraft 1.12!
     */
    SHOW_ACHIEVEMENT("id", null),
    /**
     * Show entity action.
     * Requires "name" as the mob name, "type" as the mob type and "id" as {@link java.util.UUID}.
     * <p>
     * Example: &#60;hover action="SHOW_ENTITY" name="Zombie" type="zombie" id="3f8164bf-1ed-4bcb-96be-7033beed028c"&#62;Hover event!&#60;/hover&#62;
     * <p>
     * or
     * <p>
     * Example: &#60;hover action="SHOW_ENTITY" json="{ name: \"Zombie\", type: \"zombie\", id: \"3f8164bf-1ed-4bcb-96be-7033beed028c\" }"&#62;Hover event!&#60;/hover&#62;
     */
    SHOW_ENTITY("name", null, "type", null, "id", new UUIDValidator()),
    /**
     * Show item action.
     * Requires "Count" as byte and "id" as the item type.
     * <p>
     * Example: &#60;hover action="SHOW_ITEM" Count=1b id="minecraft:stone_sword"&#62;Hover event!&#60;/hover&#62;
     * <p>
     * or
     * <p>
     * Example: &#60;hover action="SHOW_ITEM" json="{ Count: 1b, id: \"minecraft:stone_sword\" }"&#62;Hover event!&#60;/hover&#62;
     */
    SHOW_ITEM("Count", new ByteValidator(), "id", null),
    /**
     * Show text action.
     * Requires "text" as option.
     * <p>
     * Example: &#60;hover action="SHOW_TEXT" text="Secret message!"&#62;Hover event!&#60;/hover&#62;
     * <p>
     * or
     * <p>
     * Example: &#60;hover action="SHOW_TEXT" json="{ text: \"Secret message!\" }"&#62;Hover event!&#60;/hover&#62;
     */
    SHOW_TEXT("text", null);

    private final @NotNull Map<String, OptionValidator> requiredOptions;

    HoverAction(Object @NotNull ... requiredOptions) {
        this.requiredOptions = new HashMap<>();
        for (int i = 0; i < requiredOptions.length; i += 2)
            this.requiredOptions.put((String) requiredOptions[i], (OptionValidator) requiredOptions[i + 1]);
    }
}
