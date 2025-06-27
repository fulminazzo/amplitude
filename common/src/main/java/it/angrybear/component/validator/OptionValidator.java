package it.angrybear.component.validator;

import it.angrybear.exception.InvalidOptionException;

/**
 * A consumer that throws {@link InvalidOptionException}
 */
@FunctionalInterface
public interface OptionValidator {

    /**
     * Test if the given option corresponds to the desired optionName output.
     * If not, throw an invalid option exception.
     *
     * @param optionName the option name
     * @param option     the option
     * @throws InvalidOptionException the invalid option exception
     */
    void test(String optionName, String option) throws InvalidOptionException;

}