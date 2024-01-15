package it.angrybear.components;

import it.angrybear.interfaces.validators.HexColorValidator;
import it.angrybear.interfaces.validators.OptionValidator;

import java.util.HashMap;
import java.util.Map;

public class HexComponent extends OptionComponent {

    @Override
    protected Map<String, OptionValidator> getRequiredOptions() {
        Map<String, OptionValidator> options = new HashMap<>();
        options.put("color", new HexColorValidator());
        return options;
    }
}
