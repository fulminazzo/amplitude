package it.angrybear.component;

import it.angrybear.exception.InvalidOptionException;
import it.angrybear.exception.MissingRequiredOptionException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class ClickComponentTest {

    private static Object[][] getTestParameters() {
        return new Object[][]{
                // Not given action
                new Object[]{"<click >Hello world</click>", false},
                // OPEN_URL
                new Object[]{"<click action=OPEN_URL url=https://www.google.com>Hello world</click>", true},
                new Object[]{"<click action=OPEN_URL>Hello world</click>", false},
                new Object[]{"<click action=OPEN_URL url=invalid_link>Hello world</click>", false},
                // OPEN_FILE
                new Object[]{"<click action=OPEN_FILE>Hello world</click>", false},
                new Object[]{"<click action=OPEN_FILE file=./password.txt>Hello world</click>", false},
                // RUN_COMMAND
                new Object[]{"<click action=RUN_COMMAND>Hello world</click>", false},
                new Object[]{"<click action=RUN_COMMAND command=\"op Fulminazzo\">Hello world</click>", true},
                // SUGGEST_COMMAND
                new Object[]{"<click action=SUGGEST_COMMAND>Hello world</click>", false},
                new Object[]{"<click action=SUGGEST_COMMAND command=\"op Fulminazzo\">Hello world</click>", true},
                // CHANGE_PAGE
                new Object[]{"<click action=CHANGE_PAGE>Hello world</click>", false},
                new Object[]{"<click action=CHANGE_PAGE page=\"not a number!\">Hello world</click>", false},
                new Object[]{"<click action=CHANGE_PAGE page=10>Hello world</click>", true},
                // COPY_TO_CLIPBOARD
                new Object[]{"<click action=COPY_TO_CLIPBOARD>Hello world</click>", false},
                new Object[]{"<click action=COPY_TO_CLIPBOARD text=\"I hacked you!\">Hello world</click>", true},
        };
    }

    @ParameterizedTest
    @MethodSource("getTestParameters")
    void testVariousActions(String rawText, boolean valid) {
        Executable getComponent = () -> new ClickComponent(rawText);
        if (valid) assertDoesNotThrow(getComponent);
        else {
            Exception exception = assertThrows(RuntimeException.class, getComponent);
            assertTrue(exception instanceof MissingRequiredOptionException ||
                    exception instanceof InvalidOptionException);
        }
    }

    @Test
    void testSameOptions() {
        String rawText = "<red><click action=RUN_COMMAND command=\"say Hello\">Inner text</click>";
        TextComponent textComponent = new TextComponent(rawText);
        ClickComponent component = (ClickComponent) textComponent.getNext();
        assertEquals(Color.RED, component.getColor());
        assertEquals(Color.RED, component.getChild().getColor());
    }
}