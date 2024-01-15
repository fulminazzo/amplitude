package it.angrybear.interfaces.validators;

import it.angrybear.exceptions.InvalidOptionException;

@FunctionalInterface
public interface OptionValidator {

    void test(String optionName, String option) throws InvalidOptionException;

}