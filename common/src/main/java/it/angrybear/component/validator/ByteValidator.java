package it.angrybear.component.validator;

import it.angrybear.exception.InvalidOptionException;
import org.jetbrains.annotations.NotNull;

/**
 * A validator for {@link Byte} objects.
 */
public final class ByteValidator extends IntegerValidator {

    @Override
    public void test(final @NotNull String optionName, final @NotNull String option) throws InvalidOptionException {
        try {
            if (!option.endsWith("b")) throw new Exception();
            super.test(optionName, option.substring(0, option.length() - 1));
        } catch (Exception e) {
            throw new InvalidOptionException(optionName, Byte.class, option);
        }
    }

}
