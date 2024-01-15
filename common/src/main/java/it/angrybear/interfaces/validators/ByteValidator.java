package it.angrybear.interfaces.validators;

import it.angrybear.exceptions.InvalidOptionException;

/**
 * A validator for {@link Byte} objects.
 */
public class ByteValidator extends IntegerValidator {

    @Override
    public void test(String optionName, String option) throws InvalidOptionException {
        try {
            if (!option.endsWith("b")) throw new Exception();
            super.test(optionName, option.substring(0, option.length() - 1));
        } catch (Exception e) {
            throw new InvalidOptionException(optionName, Byte.class, option);
        }
    }
}
