package it.angrybear.components;

import it.angrybear.enums.HoverAction;
import it.angrybear.interfaces.IAction;
import it.angrybear.interfaces.IEventComponent;
import it.angrybear.interfaces.validators.OptionValidator;

import java.util.Map;

public class HoverComponent extends ContainerComponent implements IEventComponent {

    public HoverComponent(String rawText) {
        super(rawText, "hover");
    }

    @Override
    public Map<String, OptionValidator> getRequiredOptions() {
        return IEventComponent.super.getRequiredOptions();
    }

    @Override
    public Class<? extends IAction> getActionClass() {
        return HoverAction.class;
    }
}
