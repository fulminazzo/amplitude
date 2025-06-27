package it.fulminazzo.amplitude.converter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentConverterTest {

    @Test
    void testConverterMethod() {
        assertEquals(SectionSignConverter.class, ComponentConverter.converter().getClass());
    }

}