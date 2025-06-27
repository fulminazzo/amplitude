package it.angrybear.component;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReplaceTest {
    private static final TextComponent t3 = new TextComponent("<dark_purple>Amazing!");

    private static Object[][] getReplaceTests() {
        return new Object[][]{
                new Object[]{"<blue>", String.format("<red>This should%sbe or not be contained<bold>can't you agree?", t3.serialize())},
                new Object[]{"not be contained", String.format("<red>This should<blue>be or %s<bold>can't you agree?", t3.serialize())},
                new Object[]{"not be contained<bold>", String.format("<red>This should<blue>be or %scan't you agree?", t3.serialize())},
                new Object[]{"not be contained<bold>can't you ", String.format("<red>This should<blue>be or %sagree?", t3.serialize())},
                new Object[]{"not be contained<bold>can't you agree?", String.format("<red>This should<blue>be or %s", t3.serialize())},
                new Object[]{"<red>This should<blue>be or not be contained<bold>can't you agree?", String.format("%s", t3.serialize())}
        };
    }

    private static Object[][] getReplaceColorTests() {
        return new Object[][]{
                new Object[]{"<blue>", String.format("<red>This should%s<red>be or not be contained<bold>can't you agree?", t3.serialize())},
                new Object[]{"not be contained", String.format("<red>This should<blue>be or %s<blue><bold>can't you agree?", t3.serialize())},
                new Object[]{"not be contained<bold>", String.format("<red>This should<blue>be or %s<blue>can't you agree?", t3.serialize())},
                new Object[]{"not be contained<bold>can't you ", String.format("<red>This should<blue>be or %s<blue>agree?", t3.serialize())},
                new Object[]{"not be contained<bold>can't you agree?", String.format("<red>This should<blue>be or %s<blue>", t3.serialize())},
                new Object[]{"<red>This should<blue>be or not be contained<bold>can't you agree?", String.format("%s", t3.serialize())}
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
    @MethodSource("getReplaceTests")
    void testReplace(String raw, String replaced) {
        TextComponent t1 = TextComponent.fromRaw("<red>This should<blue>be or not be contained<bold>can't you agree?");
        TextComponent t2 = TextComponent.fromRaw(raw);
        assertEquals(replaced, t1.replace(t2, t3).serialize());
    }

    @ParameterizedTest
    @MethodSource("getReplaceColorTests")
    void testReplaceColor(String raw, String replaced) {
        TextComponent t1 = TextComponent.fromRaw("<red>This should<blue>be or not be contained<bold>can't you agree?");
        TextComponent t2 = TextComponent.fromRaw(raw);
        assertEquals(replaced, t1.replace(t2, t3, true).serialize());
    }

    @ParameterizedTest
    @MethodSource("getDoesNotContainTests")
    void testNotReplace(String raw) {
        TextComponent t1 = TextComponent.fromRaw("<red>This should<blue>be or not be contained<bold>can't you agree?");
        TextComponent t2 = TextComponent.fromRaw(raw);
        assertEquals(t1.serialize(), t1.replace(t2, t3).serialize());
    }
}
