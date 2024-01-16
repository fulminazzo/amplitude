package it.angrybear.components;

import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class TextComponentTest {

    static Object[][] getTestFromRawParameters() {
        return new Object[][]{
                new Object[]{TextComponent.class, "<red>Hello world"},
                new Object[]{HexComponent.class, "<hex color=#FF00AA>Hello world"},
                new Object[]{ClickComponent.class, "<click action=COPY_TO_CLIPBOARD text=\"I hacked you\">Hello world</click>"},
                new Object[]{HoverComponent.class, "<hover action=SHOW_TEXT text=\"Can you see me!?\">Hello world</hover>"},
        };
    }

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

    @ParameterizedTest
    @MethodSource("getTestComponents")
    void testSerialize(String rawText) {
        TextComponent textComponent = new TextComponent(rawText);
        assertEquals(rawText, TextComponent.toRaw(textComponent));
    }

    @Test
    void testSerializeHex() {
        String rawText = "<red>Hello <hex color=\"#FF00AA\">world<bold>this is sick!";
        assertEquals(rawText, new TextComponent(rawText).serialize());
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

    @Test
    void testEmpty() {
        TextComponent textComponent = new TextComponent();
        assertTrue(textComponent.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "<red>",
            "<bold>",
            "<magic>",
            "<italic>",
            "<strikethrough>",
            "<underline>",
            "<reset>",
            "Text"
    })
    void testNotEmpty(String rawText) {
        TextComponent textComponent = new TextComponent(rawText);
        assertFalse(textComponent.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("it.angrybear.enums.Color#values")
    void testColorSetters(Color color) {
        TextComponent textComponent = new TextComponent("Simple test <bold>sounds great");

        textComponent.setColor(color, false);
        assertNotEquals(color, textComponent.getNext().getColor());

        textComponent.setColor(color);
        assertEquals(color, textComponent.getNext().getColor());
    }

    @ParameterizedTest
    @EnumSource(Style.class)
    void testStyleSetters(Style style) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        TextComponent textComponent = new TextComponent("Simple test <red>sounds great");

        String methodName = style.name();
        methodName = methodName.charAt(0) + methodName.substring(1).toLowerCase();

        Method getMethod = TextComponent.class.getDeclaredMethod("get" + methodName);

        // setStyle(Boolean, boolean)
        Method setMethod = TextComponent.class.getDeclaredMethod("set" + methodName, Boolean.class, boolean.class);
        setMethod.invoke(textComponent, true, false);
        assertFalse((Boolean) getMethod.invoke(textComponent.getNext()));

        // setStyle(Boolean)
        setMethod = TextComponent.class.getDeclaredMethod("set" + methodName, Boolean.class);
        setMethod.invoke(textComponent, true);
        assertTrue((Boolean) getMethod.invoke(textComponent.getNext()));
    }

    @ParameterizedTest
    @EnumSource(Style.class)
    void testSetStyle(Style style) {
        TextComponent textComponent = new TextComponent("Simple test <red>sounds great");

        textComponent.setStyle(style, false);
        assertFalse(textComponent.getNext().getStyle(style));

        textComponent.setStyle(style);
        assertTrue(textComponent.getNext().getStyle(style));
    }

    @ParameterizedTest
    @MethodSource("getTestFromRawParameters")
    void testFromRaw(Class<?> expected, String rawText) {
        assertEquals(expected, TextComponent.fromRaw(rawText).getClass());
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