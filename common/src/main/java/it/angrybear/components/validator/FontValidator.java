package it.angrybear.components.validator;

import it.angrybear.components.Font;
import it.angrybear.exception.InvalidOptionException;

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
