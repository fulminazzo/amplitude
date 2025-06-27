package it.angrybear.components.validator;

import it.angrybear.exception.InvalidOptionException;

/**
 * A validator for a given enum class
 *
 * @param <T> the enum type
 */
public class EnumValidator<T extends Enum<T>> implements OptionValidator {
    private final Class<T> enumClass;

    /**
     * Instantiates a new Enum validator.
     *
     * @param enumClass the enum class
     */
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
