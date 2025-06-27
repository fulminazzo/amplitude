package it.angrybear.components.validator;

import it.angrybear.exception.InvalidOptionException;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * A validator for {@link UUID} objects.
 */
public class UUIDValidator implements OptionValidator {

    @Override
    public void test(@NotNull String optionName, @NotNull String option) throws InvalidOptionException {
        try {
            UUID.fromString(option);
        } catch (Exception e) {
            throw new InvalidOptionException(optionName, UUIDValidator.class, option);
        }
    }

}
