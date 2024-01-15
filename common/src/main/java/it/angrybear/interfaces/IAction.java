package it.angrybear.interfaces;

import it.angrybear.interfaces.validators.OptionValidator;

public interface IAction {

    String getRequiredOption();

    OptionValidator getValidator();

    String name();
}