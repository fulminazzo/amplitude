package it.angrybear.components.validator;

import it.angrybear.exception.InvalidOptionException;

/**
 * A validator for {@link Integer} objects.
 */
public class IntegerValidator implements OptionValidator {

    @Override
    public void test(String optionName, String option) throws InvalidOptionException {
        try {
            Integer.valueOf(option);
        } catch (NumberFormatException ex) {
            throw new InvalidOptionException(optionName, Integer.class, option);
        }
    }

}
