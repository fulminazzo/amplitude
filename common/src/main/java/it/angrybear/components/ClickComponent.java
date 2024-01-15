package it.angrybear.components;

import it.angrybear.enums.ClickAction;
import it.angrybear.interfaces.IAction;
import it.angrybear.interfaces.IEventComponent;
import it.angrybear.interfaces.validators.OptionValidator;

import java.util.Map;

public class ClickComponent extends ContainerComponent implements IEventComponent {

    public ClickComponent(String rawText) {
        super(rawText, "click");
    }

    @Override
    public Map<String, OptionValidator> getRequiredOptions() {
        return IEventComponent.super.getRequiredOptions();
    }

    @Override
    public Class<? extends IAction> getActionClass() {
        return ClickAction.class;
    }
}
