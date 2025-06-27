package it.angrybear.components.validator;

import it.angrybear.exception.InvalidOptionException;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A validator for HEX colors.
 */
public final class HexColorValidator implements OptionValidator {

    @Override
    public void test(final @NotNull String optionName, final @NotNull String option) throws InvalidOptionException {
        try {
            Matcher matcher = Pattern.compile("#[A-Fa-f0-9]{6}").matcher(option);
            if (!matcher.matches()) throw new Exception();
        } catch (Exception e) {
            throw new InvalidOptionException(optionName, "#{A-F0-9}x6", option);
        }
    }

}
