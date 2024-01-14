package it.angrybear.components;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TextComponentTest {

    static Object[][] getTestComponents() {
        return new Object[][]{
                new Object[]{"<red>Hello world",
                        mockComponent(null,
                                "RED", null, null, null, null, null, null,
                                "Hello world")},
                new Object[]{"<reed>Hello world",
                        mockComponent(null,
                                null, null, null, null, null, null, null,
                                "<reed>Hello world")},
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