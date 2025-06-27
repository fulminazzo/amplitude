package it.angrybear.components.validator;

import it.angrybear.components.Font;
import it.angrybear.exception.InvalidOptionException;
import org.jetbrains.annotations.NotNull;

public class FontValidator implements OptionValidator {

    @Override
    public void test(@NotNull String optionName, @NotNull String option) throws InvalidOptionException {
        try {
            Font.valueOf(option.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOptionException(optionName, Font.class, option);
        }
    }

}
