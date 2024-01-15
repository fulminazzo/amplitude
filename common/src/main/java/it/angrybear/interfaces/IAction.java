package it.angrybear.interfaces;

import it.angrybear.interfaces.validators.OptionValidator;

import java.util.HashMap;
import java.util.Map;

public interface IAction {

    Map<String, OptionValidator> getRequiredOptions();

    String name();
}