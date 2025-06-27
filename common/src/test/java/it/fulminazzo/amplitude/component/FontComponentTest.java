package it.fulminazzo.amplitude.component;

import it.fulminazzo.amplitude.exception.InvalidOptionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FontComponentTest {
    private static final String rawText = "<font id=\"ILLAGERALT\">This text <bold>should propagate";

    @Test
    void testFontPropagation() {
        FontComponent component = new FontComponent(rawText);
        assertEquals(Font.ILLAGERALT, component.getNext().getFont());
    }

    @Test
    void testSerialize() {
        FontComponent component = new FontComponent(rawText);
        assertEquals(rawText, component.serialize());
    }

    @Test
    void testWrongSerialize() {
        FontComponent component = new FontComponent(rawText);
        assertNotEquals("<font id=\"ILLAGERALT\">This text <font id=\"ILLAGERALT\"><bold>should propagate", component.serialize());
    }

    @Test
    void testInvalidFont() {
        assertThrows(InvalidOptionException.class, () -> new FontComponent("<font id=\"INVALID\">Test"));
    }

}