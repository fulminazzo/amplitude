package it.fulminazzo.amplitude.component.validator;

import it.fulminazzo.amplitude.component.Font;
import it.fulminazzo.amplitude.exception.InvalidOptionException;
import org.jetbrains.annotations.NotNull;

public final class FontValidator implements OptionValidator {

    @Override
    public void test(final @NotNull String optionName, final @NotNull String option) throws InvalidOptionException {
        try {
            Font.valueOf(option.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOptionException(optionName, Font.class, option);
        }
    }

}
