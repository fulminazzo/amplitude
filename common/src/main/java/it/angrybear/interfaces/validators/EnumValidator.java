package it.angrybear.interfaces.validators;

import it.angrybear.exceptions.InvalidOptionException;

public class EnumValidator<T extends Enum<T>> implements OptionValidator {
    private final Class<T> enumClass;

    public EnumValidator(Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void test(String optionName, String option) throws InvalidOptionException {
        try {
            Enum.valueOf(enumClass, option.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOptionException(optionName, enumClass, option);
        }
    }
}
