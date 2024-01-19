package it.angrybear.components;

import it.angrybear.enums.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HexComponentTest {
    private static final String rawText = "<hex color=\"#FF00AA\">This text <bold>should propagate";

    @Test
    void testHexPropagation() {
        HexComponent textComponent = new HexComponent(rawText);
        assertEquals(new Color("#FF00AA"), textComponent.getNext().getColor());
    }

    @Test
    void testSerialize() {
        HexComponent textComponent = new HexComponent(rawText);
        assertEquals(rawText, textComponent.serialize());
    }

    @Test
    void testWrongSerialize() {
        HexComponent textComponent = new HexComponent(rawText);
        assertNotEquals("<hex color=\"#FF00AA\">This text <hex color=\"#FF00AA\"><bold>should propagate", textComponent.serialize());
    }
}