package it.angrybear.interfaces.validators;

import it.angrybear.exceptions.InvalidOptionException;

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
