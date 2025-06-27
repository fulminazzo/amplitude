package it.fulminazzo.amplitude.component;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class TranslatableComponentTest {

    @Test
    void testTranslatableComponent() {
        String arg1 = "Diamond Sword";
        String arg2 = "1";
        String arg3 = "Alex & friends";
        Component content = new Component("commands.give.success.single");
        String rawText = String.format("<translatable arguments=\"%s&%s&%s\">%s</translatable>",
                arg1, arg2, "\\\"" + arg3 + "\\\"", content.serialize());
        TranslatableComponent component = new TranslatableComponent(rawText);
        assertEquals(content, component.getChild(), rawText);
        assertIterableEquals(Arrays.asList(new Component(arg1),
                        new Component(arg2), new Component(arg3)),
                component.getArguments(), rawText);
    }
}