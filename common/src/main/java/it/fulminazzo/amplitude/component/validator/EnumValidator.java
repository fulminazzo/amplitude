package it.fulminazzo.amplitude.component.validator;

import it.fulminazzo.amplitude.exception.InvalidOptionException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for a given enum class
 *
 * @param <T> the enum type
 */
public final class EnumValidator<T extends Enum<T>> implements OptionValidator {
    private final Class<T> enumClass;

    /**
     * Instantiates a new Enum validator.
     *
     * @param enumClass the enum class
     */
    public EnumValidator(final @NotNull Class<T> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public void test(final @NotNull String optionName, final @NotNull String option) throws InvalidOptionException {
        try {
            Enum.valueOf(enumClass, option.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOptionException(optionName, enumClass, option);
        }
    }

}
