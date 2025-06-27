package it.angrybear.interfaces;

import it.angrybear.components.validator.EnumValidator;
import it.angrybear.components.validator.OptionValidator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public interface IEventComponent {

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

    Class<? extends IAction> getActionClass();

    Map<String, String> getTagOptions();
}
