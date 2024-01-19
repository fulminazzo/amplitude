package it.angrybear.components;

import it.angrybear.enums.Font;
import it.angrybear.exceptions.InvalidOptionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FontComponentTest {
    private static final String rawText = "<font id=\"ILLAGERALT\">This text <bold>should propagate";

    @Test
    void testFontPropagation() {
        FontComponent textComponent = new FontComponent(rawText);
        assertEquals(Font.ILLAGERALT, textComponent.getNext().getFont());
    }

    @Test
    void testSerialize() {
        FontComponent textComponent = new FontComponent(rawText);
        assertEquals(rawText, textComponent.serialize());
    }

    @Test
    void testWrongSerialize() {
        FontComponent textComponent = new FontComponent(rawText);
        assertNotEquals("<font id=\"ILLAGERALT\">This text <font id=\"ILLAGERALT\"><bold>should propagate", textComponent.serialize());
    }

    @Test
    void testInvalidFont() {
        assertThrows(InvalidOptionException.class, () -> new FontComponent("<font id=\"INVALID\">Test"));
    }

}