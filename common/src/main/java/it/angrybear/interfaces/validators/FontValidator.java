package it.angrybear.interfaces.validators;

import it.angrybear.enums.Font;
import it.angrybear.exceptions.InvalidOptionException;

public class FontValidator implements OptionValidator {

    @Override
    public void test(String optionName, String option) throws InvalidOptionException {
        try {
            Font.valueOf(option.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOptionException(optionName, Font.class, option);
        }
    }
}
