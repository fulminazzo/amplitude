package it.angrybear.interfaces;

import it.angrybear.exceptions.InvalidOptionException;

@FunctionalInterface
public interface OptionValidator<T> {

    void test(T var1) throws InvalidOptionException;

}