package it.fulminazzo.amplitude.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class ComponentTest {

    private static Object[][] components() {
        return new Object[][]{
                new Object[]{"&f&l💬 Astra &8(<page>/<max_page>)", "<white><bold>💬 Astra <darkgray><bold>(<page>/<max_page>)"},
                new Object[]{
                        "<name>&8: &4<deaths> &cdeaths &8(&eSince last death: <time_since_death>&8)",
                        "<name><darkgray>: <darkred><deaths> <red>deaths <darkgray>(<yellow>Since last death: <time_since_death><darkgray>)"
                }
        };
    }

    @ParameterizedTest
    @MethodSource("components")
    void testGeneralComponents(String text, String expected) {
        Component component = Component.fromRaw(text);

        assertEquals(expected, component.serialize());
    }

    @Test
    void testReplace() {
        Component from = Component.fromRaw("<player>");
        Component to = Component.fromRaw(
                "<hover action=\"SHOW_TEXT\" text=\"<white>Name: <yellow>Bipolale\n" +
                "<white>First login: <green>07/18/2025 22:20\">Bipolale</hover>"
        );

        Component actual = from.replace(from, to);

        assertEquals(to, actual);
    }

    @Test
    void testFromRawSupportsEmptyString() {
        Component component = Component.fromRaw("");
        assertNotNull(component);
    }

    @Test
    void testFromRawSupportsAmpersandAndSectionSign() {
        String raw = "&dHello, §cworld!";

        Component component = Component.fromRaw(raw);
        Component expected = new Component("Hello, ");
        expected.setColor(Color.LIGHT_PURPLE);
        Component expected2 = new Component("world!");
        expected2.setColor(Color.RED);
        expected.addNext(expected2);

        assertEquals(expected, component);
    }

    static Object[][] getTestFromRawParameters() {
        return new Object[][]{
                new Object[]{Component.class, "<red>Hello world"},
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
                new Object[]{"<obfuscated>Hello world",
                        mockComponent(null,
                                null, true, null, null, null, null, null,
                                "Hello world")},
                new Object[]{"<bold>Hello world",
                        mockComponent(null,
                                null, null, true, null, null, null, null,
                                "Hello world")},
                new Object[]{"<!strikethrough>Hello world",
                        mockComponent(null,
                                null, null, null, false, null, null, null,
                                "Hello world")},
                new Object[]{"<underlined>Hello world",
                        mockComponent(null,
                                null, null, null, null, true, null, null,
                                "Hello world")},
                new Object[]{"<!italic>Hello world",
                        mockComponent(null,
                                null, null, null, null, null, false, null,
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
                                                        "WHITE", false, false, false, false, false, true,
                                                        "world").replace("font: null", "font: DEFAULT"),
                                                "RED", null, true, null, null, true, null,
                                                "Hello "),
                                        "RED", null, true, null, null, null, null,
                                        ""),
                                "RED", null, null, null, null, null, null,
                                "")},
        };
    }

    private static Component[] getTestCloneComponents() {
        return new Component[]{
                new Component("<red>Hello world"),
                new OptionComponentTest.MockContainer("<mock option=\"test\">Hello world"),
                new HexComponent("<hex color=\"#FF00AA\">Hello world"),
                new ContainerComponentTest.MockContainer("<mock option=\"test\">Hello world</mock>"),
                new ClickComponent("<click action=OPEN_URL url=https://www.google.com>Hello world</click>"),
                new HoverComponent("<hover action=SHOW_TEXT text=\"Simple text\">Hello world</hover>")
        };
    }

    @ParameterizedTest
    @MethodSource("getTestComponents")
    void testComponent(String rawText, String expected) {
        Component component = new Component(rawText);
        assertEquals(expected, component.toString());
    }

    @ParameterizedTest
    @MethodSource("getTestComponents")
    void testSerialize(String rawText) {
        Component component = new Component(rawText);
        assertEquals(rawText, Component.toRaw(component));
    }

    @ParameterizedTest
    @MethodSource("getTestCloneComponents")
    void testClone(Component component) {
        assertEquals(component.copy(), component);
    }

    @Test
    void testSetSameOptions() {
        Component component = new Component("<red><bold>Hello <reset>world<blue>what color am I?");
        Component c = component.getNext().getNext().getNext();
        assertEquals(Color.BLUE, c.getColor());
        assertNull(c.getStyle(Style.BOLD));
    }

    @Test
    void testFont() {
        Component first = new Component("First<bold>second<reset>third");
        Component second = first.getNext();
        Component third = second.getNext();
        first.setFont(Font.ILLAGERALT);
        assertEquals(Font.ILLAGERALT, second.getFont());
        assertEquals(Font.DEFAULT, third.getFont());
    }

    @Test
    void testIsSimilar() {
        Component t1 = new Component("<red>Hello world");
        Component t2 = new Component("<red>How are you");
        assertTrue(t1.isSimilar(t2));
    }

    @Test
    void testIsNotSimilar() {
        Component t1 = new Component("<red>Hello world");
        Component t2 = new Component("<blue>How are you");
        assertFalse(t1.isSimilar(t2));
    }

    @Test
    void testIsEqual() {
        Component t1 = new Component("<red>Hello world");
        Component t2 = new Component("<red>Hello world");
        assertTrue(t1.equals(t2));
    }

    @Test
    void testIsNotEqual() {
        Component t1 = new Component("<red>Hello world");
        Component t2 = new Component("<red>How are you");
        assertFalse(t1.equals(t2));
    }

    @Test
    void testEmpty() {
        Component component = new Component();
        assertTrue(component.isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "<red>",
            "<bold>",
            "<obfuscated>",
            "<italic>",
            "<strikethrough>",
            "<underlined>",
            "<reset>",
            "Text"
    })
    void testNotEmpty(String rawText) {
        Component component = new Component(rawText);
        assertFalse(component.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("it.fulminazzo.amplitude.component.Color#values")
    void testColorSetters(Color color) {
        Component component = new Component("Simple test <bold>sounds great");

        component.setColor(color, false);
        assertNotEquals(color, component.getNext().getColor());

        component.setColor(color);
        assertEquals(color, component.getNext().getColor());
    }

    @ParameterizedTest
    @EnumSource(Style.class)
    void testStyleSetters(Style style) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Component component = new Component("Simple test <red>sounds great");

        String methodName = style.name();
        methodName = methodName.charAt(0) + methodName.substring(1).toLowerCase();

        Method getMethod = Component.class.getDeclaredMethod("is" + methodName);

        if (style != Style.RESET) methodName = "set" + methodName;
        else methodName = methodName.toLowerCase();

        // setStyle(Boolean, boolean)
        Method setMethod = Component.class.getDeclaredMethod(methodName, Boolean.class, boolean.class);
        setMethod.invoke(component, true, false);
        assertFalse((Boolean) getMethod.invoke(component.getNext()));

        // setStyle(Boolean)
        setMethod = Component.class.getDeclaredMethod(methodName, Boolean.class);
        setMethod.invoke(component, true);
        assertEquals(style != Style.RESET, getMethod.invoke(component.getNext()));
    }

    @ParameterizedTest
    @EnumSource(Style.class)
    void testSetStyle(Style style) {
        Component component = new Component("Simple test <red>sounds great");

        component.setStyle(style, true, false);
        assertTrue(component.getStyle(style));
        assertNull(component.getNext().getStyle(style));

        component.setStyle(style, false, false);
        assertFalse(component.getStyle(style));
        assertNull(component.getNext().getStyle(style));

        component.setStyle(style);
        assertTrue(component.getStyle(style));
        assumeTrue(style != Style.RESET, "If style is RESET, it should not be propagated");
        assertTrue(component.getNext().getStyle(style));
    }

    @ParameterizedTest
    @MethodSource("getTestFromRawParameters")
    void testFromRaw(Class<?> expected, String rawText) {
        assertEquals(expected, Component.fromRaw(rawText).getClass());
    }

    static String mockComponent(String next, String color, Boolean obfuscated,
                                Boolean bold, Boolean strikethrough, Boolean underlined,
                                Boolean italic, Boolean reset, String text) {
        return String.format("{next: %s, ", next) +
                String.format("color: %s, ", color) +
                "font: null, " +
                String.format("obfuscated: %s, ", obfuscated) +
                String.format("bold: %s, ", bold) +
                String.format("strikethrough: %s, ", strikethrough) +
                String.format("underlined: %s, ", underlined) +
                String.format("italic: %s, ", italic) +
                String.format("reset: %s, ", reset) +
                String.format("text: %s}", text);
    }
}