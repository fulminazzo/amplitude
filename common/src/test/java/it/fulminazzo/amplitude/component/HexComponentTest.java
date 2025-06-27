package it.fulminazzo.amplitude.component;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HexComponentTest {
    private static final String rawText = "<hex color=\"#FF00AA\">This text <bold>should propagate";

    @Test
    void testHexPropagation() {
        HexComponent component = new HexComponent(rawText);
        assertEquals(new Color("#FF00AA"), component.getNext().getColor());
    }

    @Test
    void testSerialize() {
        HexComponent component = new HexComponent(rawText);
        assertEquals(rawText, component.serialize());
    }

    @Test
    void testWrongSerialize() {
        HexComponent component = new HexComponent(rawText);
        assertNotEquals("<hex color=\"#FF00AA\">This text <hex color=\"#FF00AA\"><bold>should propagate", component.serialize());
    }
}