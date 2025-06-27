package it.angrybear.component;

import it.angrybear.exception.InvalidOptionException;
import it.angrybear.exception.MissingRequiredOptionException;
import it.angrybear.component.validator.OptionValidator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class OptionComponentTest {

    @Test
    void testTagOptions() {
        String rawText = "<mock " +
                "key1=value " +
                "key2 " +
                "key3=\"super value\" " +
                "key4=\"awesome value\" " +
                "key5=\"this \\\"value\\\" should be taken\" " +
                "key6='super value' " +
                "key7='awesome value' " +
                "key8='this value should be OVERWRITTEN' " +
                "key8='this \\'value\\' should be taken' " +
                "key9='this value is good' " +
                "key10='this value is <also> good'>Hello world";
        HashMap<String, String> expected = new HashMap<>();
        expected.put("key1", "value");
        expected.put("key2", null);
        expected.put("key3", "super value");
        expected.put("key4", "awesome value");
        expected.put("key5", "this \"value\" should be taken");
        expected.put("key6", "super value");
        expected.put("key7", "awesome value");
        expected.put("key8", "this 'value' should be taken");
        expected.put("key9", "this value is good");
        expected.put("key10", "this value is <also> good");
        MockContainer mockContainer = new MockContainer(rawText);
        assertEquals(expected, mockContainer.getTagOptions());
    }

    @Test
    void testTagOptionLink() {
        String rawText = "<mock url=https://www.google.com>Hello world";
        HashMap<String, String> expected = new HashMap<>();
        expected.put("url", "https://www.google.com");
        MockContainer mockContainer = new MockContainer(rawText);
        assertEquals(expected, mockContainer.getTagOptions());
    }

    @Test
    void testJsonOptions() {
        String rawText = "<mock json=\"{name: \\\"Alex\\\", age: 10, title: \\\"Json is amazing\\\"}\" title=OVERWRITTEN>Hello world";
        HashMap<String, String> expected = new HashMap<>();
        expected.put("name", "Alex");
        expected.put("age", "10.0");
        expected.put("title", "OVERWRITTEN");
        MockContainer mockContainer = new MockContainer(rawText);
        assertEquals(expected, mockContainer.getTagOptions());
    }

    @Test
    void testMissingRequiredOptionFound() {
        assertDoesNotThrow(() ->
                new MockRequiredContainer("<mock name=\"Alex\" age=10>Hello world"));
    }

    @Test
    void testMissingRequiredOptionInvalid() {
        assertThrowsExactly(InvalidOptionException.class, () ->
                new MockRequiredContainer("<mock name=\"Alex\" age=Pumpkin>Hello world"));
    }

    @Test
    void testMissingRequiredOptionNotFound() {
        assertThrowsExactly(MissingRequiredOptionException.class, () ->
                new MockRequiredContainer("<mock surname=\"Not Alex\" age=10>Hello world"));
    }

    @Test
    void testEmpty() {
        assertTrue(new MockContainer("").isEmpty());
    }

    @Test
    void testNotEmpty() {
        assertFalse(new MockContainer("<mock key=\"value\">").isEmpty());
    }

    @Test
    void testSerialize() {
        String rawText = "<mock color=\"RED\">Hello world";
        MockContainer mockContainer = new MockContainer(rawText);
        assertEquals(rawText, mockContainer.serialize());
    }

    static class MockContainer extends OptionComponent {

        public MockContainer(String rawText) {
            super(rawText, "mock");
        }
    }

    static class MockRequiredContainer extends OptionComponent {

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