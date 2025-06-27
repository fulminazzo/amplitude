package it.angrybear.components;

import it.angrybear.exception.MissingRequiredOptionException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InsertionComponentTest {

    @Test
    void testInsertion() {
        String rawText = "<insertion text=\"Amazing\">Hello world</insertion>";
        InsertionComponent component = new InsertionComponent(rawText);
        assertEquals("Amazing", component.getInsertionText());
    }

    @Test
    void testInvalidInsertion() {
        String rawText = "<insertion>Hello world</insertion>";
        assertThrows(MissingRequiredOptionException.class, () -> new InsertionComponent(rawText));
    }
}