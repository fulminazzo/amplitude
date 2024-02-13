package it.angrybear.components;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ContainTest {

    private static String[] getContainsTests() {
        return new String[]{
                "<blue>",
                "<blue>not be contained",
                "<blue>not be contained<bold>",
                "<blue>not be contained<bold>can't you ",
                "<blue>not be contained<bold>can't you agree?",
                "<red>This should<blue>be or not be contained<bold>can't you agree?"
        };
    }

    private static String[] getDoesNotContainTests() {
        return new String[]{
                "<blue>be or not be contained<bold>can't u ",
                "<blue>be or not be contained<bold>can't you agree? <red>Friend",
                "<red>should<blue>container<bold>agree?",
        };
    }

    @ParameterizedTest
    @MethodSource("getContainsTests")
    void testTextContains(String raw) {
        TextComponent t1 = TextComponent.fromRaw("<red>This should<blue>be or not be contained<bold>can't you agree?");
        TextComponent t2 = TextComponent.fromRaw(raw);
        assertTrue(t1.contains(t2));
    }

    @ParameterizedTest
    @MethodSource("getDoesNotContainTests")
    void testTextDoesNotContain(String raw) {
        TextComponent t1 = TextComponent.fromRaw("<red>This should<blue>be or not be contained<bold>can't you agree?");
        TextComponent t2 = TextComponent.fromRaw(raw);
        assertFalse(t1.contains(t2));
    }

    @Test
    void testComponentContains() {
        String rawText = "<red>,"
                + "<bold>Hello world,"
                + "<hex color=#FF00AA>are you ready,"
                + "<bold>Hope you are...,"
                + "<font id=\"ILLAGERALT\">or else...,"
                + "<reset>This should be reset,"
                + "<insertion text=\"Hello there!\">Insert DEMO </insertion>,"
                + "<translatable arguments=\"Diamond Sword&1&\\\"Alex & Friends\\\"\">commands.give.successful.single</translatable>";
        TextComponent t1 = TextComponent.fromRaw(rawText);
        for (String t : rawText.split(","))
            assertTrue(t1.contains(TextComponent.fromRaw(t)),
                    String.format("Raw text did not contain \"%s\"", t));
    }
}
