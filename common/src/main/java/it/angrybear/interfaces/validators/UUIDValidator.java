package it.angrybear.interfaces.validators;

import it.angrybear.exceptions.InvalidOptionException;

import java.util.UUID;

public class UUIDValidator implements OptionValidator {

    @Override
    public void test(String optionName, String option) throws InvalidOptionException {
        try {
            UUID.fromString(option);
        } catch (Exception e) {
            throw new InvalidOptionException(optionName, UUIDValidator.class, option);
        }
    }
}
