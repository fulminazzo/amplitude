package it.angrybear.component;

import it.angrybear.component.validator.EnumValidator;
import it.angrybear.component.validator.OptionValidator;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a component bound to an event.
 */
interface IEventComponent {

    /**
     * Gets the required options of the component.
     *
     * @param <T> the type of the {@link #getActionClass()}
     * @return the required options
     */
    @SuppressWarnings("unchecked")
    default <T extends Enum<T>> Map<String, OptionValidator> getRequiredOptions() {
        HashMap<String, OptionValidator> requiredOptions = new HashMap<>();
        Class<T> actionClass = (Class<T>) getActionClass();
        requiredOptions.put("action", new EnumValidator<>(actionClass));
        IAction[] values;
        try {
            Method getValues = actionClass.getDeclaredMethod("values");
            values = (IAction[]) getValues.invoke(actionClass);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        for (IAction action : values) {
            String option = getTagOptions().get("action");
            if (action.name().equals(option)) {
                requiredOptions.putAll(action.getRequiredOptions());
                break;
            }
        }
        return requiredOptions;
    }

    /**
     * Gets the associated action class.
     *
     * @return the action class
     */
    @NotNull Class<? extends IAction> getActionClass();

    /**
     * Gets tag options.
     *
     * @return the tag options
     */
    @NotNull Map<String, String> getTagOptions();

}
