package it.angrybear.components.validator;

import it.angrybear.exception.InvalidOptionException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for {@link Integer} objects.
 */
public class IntegerValidator implements OptionValidator {

    @Override
    public void test(final @NotNull String optionName, final @NotNull String option) throws InvalidOptionException {
        try {
            Integer.valueOf(option);
        } catch (NumberFormatException ex) {
            throw new InvalidOptionException(optionName, Integer.class, option);
        }
    }

}
