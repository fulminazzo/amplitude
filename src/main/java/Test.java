import it.fulminazzo.amplitude.component.Color;
import it.fulminazzo.amplitude.component.Component;
import it.fulminazzo.amplitude.component.*;

public class Test {

    public static void main(String[] args) {
        //TODO: Would be nice to support & and $ coloring
        HoverComponent component = new HoverComponent(
                "<hover " +
                        "action=\"SHOW_ACHIEVEMENT\" " +
                        // arguments
                        "id=\"achievement.mineWood\"" +
                        ">" +
                        "Show my achievement!" +
                        "</hover>"
        );
        // to edit
        component.setHoverAction(HoverAction.SHOW_ACHIEVEMENT, new Component("achievement.mineWood"));
        // or
        String serialized = "<hover action=\"SHOW_ACHIEVEMENT\" id=\"achievement.mineWood\">Show my achievement!</hover>";
        Component deserialized = Component.fromRaw(serialized);







    }

}
