package it.angrybear.components;

import it.angrybear.enums.Color;
import it.angrybear.exceptions.InvalidComponentException;
import it.angrybear.exceptions.InvalidOptionException;
import it.angrybear.exceptions.MissingRequiredOptionException;
import it.angrybear.interfaces.validators.OptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;

import static it.angrybear.components.TextComponentTest.mockComponent;
import static org.junit.jupiter.api.Assertions.*;

class ContainerComponentTest {

    private static Object[][] getTestChildren() {
        Object[][] objects = TextComponentTest.getTestComponents();
        for (Object[] object : objects) {
            object[0] = String.format("<mock>%s</mock>", object[0]);
            object[1] = mockContainerComponent(null, object[1].toString());
        }
        return objects;
    }

    private static Object[][] getTestNext() {
        Object[][] objects = TextComponentTest.getTestComponents();
        for (Object[] object : objects) {
            object[0] = String.format("<mock>%s</mock>%s", object[0], object[0]);
            object[1] = mockContainerComponent(object[1].toString(), object[1].toString());
        }
        return objects;
    }

    private static Object[][] getTestEmptyNext() {
        Object[][] objects = TextComponentTest.getTestComponents();
        for (Object[] object : objects) {
            object[0] = String.format("<mock></mock>%s", object[0]);
            object[1] = mockContainerComponent(object[1].toString(), null);
        }
        return objects;
    }

    @Test
    void testTagOptions() {
        String rawText = "<mock key10='this value is <also> good'>Hello world</mock>";
        HashMap<String, String> expected = new HashMap<>();
        expected.put("key10", "this value is <also> good");
        MockContainer mockContainer = new MockContainer(rawText);
        assertEquals(expected, mockContainer.getTagOptions());
    }

    @Test
    void testMissingRequiredOptionFound() {
        assertDoesNotThrow(() ->
                new MockRequiredContainer("<mock name=\"Alex\" age=10>Hello world</mock>"));
    }

    @Test
    void testMissingRequiredOptionInvalid() {
        assertThrowsExactly(InvalidOptionException.class, () ->
                new MockRequiredContainer("<mock name=\"Alex\" age=Pumpkin>Hello world</mock>"));
    }

    @Test
    void testMissingRequiredOptionNotFound() {
        assertThrowsExactly(MissingRequiredOptionException.class, () ->
                new MockRequiredContainer("<mock surname=\"Not Alex\" age=10>Hello world</mock>"));
    }

    @Test
    void testMissingStartTag() {
        Executable executable = () -> new MockContainer("Invalid text");
        assertThrowsExactly(InvalidComponentException.class, executable);
        try {
            executable.execute();
        } catch (Throwable e) {
            assertTrue(e.getMessage().contains("<mock>"));
        }
    }

    @Test
    void testMissingEndTag() {
        Executable executable = () -> new MockContainer("<mock>Invalid text");
        assertThrowsExactly(InvalidComponentException.class, executable);
        try {
            executable.execute();
        } catch (Throwable e) {
            assertTrue(e.getMessage().contains("</mock>"));
        }
    }

    @Test
    void testEmpty() {
        assertTrue(new MockContainer("<mock></mock>").isEmpty());
    }

    @Test
    void testNotEmpty() {
        assertFalse(new MockContainer("<mock><red>Inner content</mock>").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("getTestChildren")
    void testChildrenInContainerComponent(String rawText, String expected) {
        MockContainer mockContainer = new MockContainer(rawText);
        assertEquals(expected, mockContainer.toString());
    }

    @ParameterizedTest
    @MethodSource("getTestNext")
    void testNextInContainerComponent(String rawText, String expected) {
        MockContainer mockContainer = new MockContainer(rawText);
        assertEquals(expected, mockContainer.toString());
    }

    @ParameterizedTest
    @MethodSource("getTestEmptyNext")
    void testEmptyNextInContainerComponent(String rawText, String expected) {
        MockContainer mockContainer = new MockContainer(rawText);
        assertEquals(expected, mockContainer.toString());
    }

    @Test
    void testSetSameOptions() {
        MockContainer mockContainer = new MockContainer("<mock>Hello world</mock>");
        mockContainer.setColor(Color.RED);
        mockContainer.setSameOptions(mockContainer.getChild());
        assertEquals(Color.RED, mockContainer.getChild().getColor());
    }

    @Test
    void testSerialize() {
        String rawText = "<mock color=\"RED\">Hello world</mock>";
        MockContainer mockContainer = new MockContainer(rawText);
        assertEquals(rawText, mockContainer.serialize());
    }

    private static String mockContainerComponent(String next, String children) {
        String mock = mockComponent(null, null, null, null, null, null, null, null, null);
        mock = mock.replace("color:", String.format("child: %s, color:", children));
        mock = mock.replaceAll("^\\{next: null","{next: " + next);
        return mock;
    }


    static class MockContainer extends ContainerComponent {

        public MockContainer(String rawText) {
            super(rawText, "mock");
        }
    }

    static class MockRequiredContainer extends ContainerComponent {

        public MockRequiredContainer(String rawText) {
            super(rawText, "mock");
        }

        @Override
        protected Map<String, OptionValidator> getRequiredOptions() {
            HashMap<String, OptionValidator> required = new HashMap<>();
            required.put("name", null);
            required.put("age", (o, e) -> {
                try {
                    Integer.valueOf(e);
                } catch (NumberFormatException ex) {
                    throw new InvalidOptionException(o, Integer.class, e);
                }
            });
            return required;
        }
    }
}