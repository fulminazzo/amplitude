package it.fulminazzo.amplitude;

import it.fulminazzo.amplitude.component.Component;
import it.fulminazzo.amplitude.component.CustomComponent;
import it.fulminazzo.amplitude.component.HexComponent;

public class RoseFluoComponent extends CustomComponent<RoseFluoComponent> {

    public RoseFluoComponent() {
        super("rose");
    }

    public RoseFluoComponent(String rawText) {
        super(rawText, "rose");
    }

    @Override
    public Component toMinecraft() {
        HexComponent component = new HexComponent("<hex color=#FF00AA>" + getText());
        component.addNext(getNext());
        return component;
    }

}
