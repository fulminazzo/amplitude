package it.fulminazzo.amplitude.serializer;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ComponentSerializerTest {

    @Test
    void testSerializerMethod() {
        assertEquals(SectionSignSerializer.class, ComponentSerializer.serializer().getClass());
    }
}