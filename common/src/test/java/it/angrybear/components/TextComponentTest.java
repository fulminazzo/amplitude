package it.angrybear.components;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class TextComponentTest {

    static Object[][] getTestComponents() {
        return new Object[][]{
                new Object[]{"<red>Hello world",
                        mockComponent(null,
                                "RED", null, null, null, null, null, null,
                                "Hello world")},
                new Object[]{"Hello <green>world",
                        mockComponent(mockComponent(null,
                                        "GREEN", null, null, null, null, null, null,
                                        "world"),
                                null, null, null, null, null, null, null,
                                "Hello ")},
                new Object[]{"<reed>Hello world<test>How are you?",
                        mockComponent(null,
                                null, null, null, null, null, null, null,
                                "<reed>Hello world<test>How are you?")},
                new Object[]{"<magic>Hello world",
                        mockComponent(null,
                                null, true, null, null, null, null, null,
                                "Hello world")},
                new Object[]{"<bold>Hello world",
                        mockComponent(null,
                                null, null, true, null, null, null, null,
                                "Hello world")},
                new Object[]{"<strikethrough>Hello world",
                        mockComponent(null,
                                null, null, null, true, null, null, null,
                                "Hello world")},
                new Object[]{"<underline>Hello world",
                        mockComponent(null,
                                null, null, null, null, true, null, null,
                                "Hello world")},
                new Object[]{"<italic>Hello world",
                        mockComponent(null,
                                null, null, null, null, null, true, null,
                                "Hello world")},
                new Object[]{"<red><bold>Hello world",
                        mockComponent(mockComponent(null,
                                        "RED", null, true, null, null, null, null,
                                        "Hello world"),
                                "RED", null, null, null, null, null, null,
                                "")},
                new Object[]{"<red><bold><italic>Hello world",
                        mockComponent(mockComponent(mockComponent(null,
                                                "RED", null, true, null, null, true, null,
                                                "Hello world"),
                                        "RED", null, true, null, null, null, null,
                                        ""),
                                "RED", null, null, null, null, null, null,
                                "")},
                new Object[]{"<red><bold><italic>Hello <reset>world",
                        mockComponent(mockComponent(mockComponent(mockComponent(null,
                                                        null, null, null, null, null, null, true,
                                                        "world"),
                                                "RED", null, true, null, null, true, null,
                                                "Hello "),
                                        "RED", null, true, null, null, null, null,
                                        ""),
                                "RED", null, null, null, null, null, null,
                                "")},
        };
    }

    @ParameterizedTest
    @MethodSource("getTestComponents")
    void testComponent(String rawText, String expected) {
        TextComponent textComponent = new TextComponent(rawText);
        assertEquals(expected, textComponent.toString());
    }

    @Test
    void testIsSimilar() {
        TextComponent t1 = new TextComponent("<red>Hello world");
        TextComponent t2 = new TextComponent("<red>How are you");
        assertTrue(t1.isSimilar(t2));
    }

    @Test
    void testIsNotSimilar() {
        TextComponent t1 = new TextComponent("<red>Hello world");
        TextComponent t2 = new TextComponent("<blue>How are you");
        assertFalse(t1.isSimilar(t2));
    }

    @Test
    void testIsEqual() {
        TextComponent t1 = new TextComponent("<red>Hello world");
        TextComponent t2 = new TextComponent("<red>Hello world");
        assertTrue(t1.equals(t2));
    }

    @Test
    void testIsNotEqual() {
        TextComponent t1 = new TextComponent("<red>Hello world");
        TextComponent t2 = new TextComponent("<red>How are you");
        assertFalse(t1.equals(t2));
    }

    static String mockComponent(String next, String color, Boolean magic,
                                        Boolean bold, Boolean strikethrough, Boolean underline,
                                        Boolean italic, Boolean reset, String text) {
        return String.format("{next: %s, ", next) +
                String.format("color: %s, ", color) +
                String.format("magic: %s, ", magic) +
                String.format("bold: %s, ", bold) +
                String.format("strikethrough: %s, ", strikethrough) +
                String.format("underline: %s, ", underline) +
                String.format("italic: %s, ", italic) +
                String.format("reset: %s, ", reset) +
                String.format("text: %s}", text);
    }
}