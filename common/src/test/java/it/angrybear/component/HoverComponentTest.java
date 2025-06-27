package it.angrybear.component;

import it.angrybear.exception.InvalidOptionException;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

class HoverComponentTest {

    private static Object[][] getTestParameters() {
        return new Object[][]{
                // Not given action
                new Object[]{"<hover >Hello world</hover>", false},
                // SHOW_ACHIEVEMENT
                new Object[]{"<hover action=SHOW_ACHIEVEMENT>Hello world</hover>", false},
                new Object[]{"<hover action=SHOW_ACHIEVEMENT id=achievement.mineWood>Hello world</hover>", true},
                // SHOW_ENTITY
                new Object[]{"<hover action=SHOW_ENTITY>Hello world</hover>", false},
                new Object[]{"<hover action=SHOW_ENTITY type=\"zombie\" id=\"3f8164bf-1ed-4bcb-96be-7033beed028c\">Hello world</hover>", false},
                new Object[]{"<hover action=SHOW_ENTITY name=\"Zombie\" id=\"3f8164bf-1ed-4bcb-96be-7033beed028c\">Hello world</hover>", false},
                new Object[]{"<hover action=SHOW_ENTITY name=\"Zombie\" type=\"zombie\">Hello world</hover>", false},
                new Object[]{"<hover action=SHOW_ENTITY name=\"Zombie\" type=\"zombie\" id=\"3f8164bf-1ed-4bcb-96be-7033beed028c\">Hello world</hover>", true},
                new Object[]{"<hover action=SHOW_ENTITY json=\"{name=\\\"Zombie\\\", type=\\\"zombie\\\", id=\\\"3f8164bf-1ed-4bcb-96be-7033beed028c\\\"}\">Hello world</hover>", true},
                // SHOW_ITEM
                new Object[]{"<hover action=SHOW_ITEM>Hello world</hover>", false},
                new Object[]{"<hover action=SHOW_ITEM Count=1 id=minecraft:stone_sword>Hello world</hover>", false},
                new Object[]{"<hover action=SHOW_ITEM Count=1b id=minecraft:stone_sword>Hello world</hover>", true},
                // SHOW_TEXT
                new Object[]{"<hover action=SHOW_TEXT>Hello world</hover>", false},
                new Object[]{"<hover action=SHOW_TEXT text=\"Hello from Fulminazzo\">Hello world</hover>", true},
        };
    }

    @ParameterizedTest
    @MethodSource("getTestParameters")
    void testVariousActions(String rawText, boolean valid) {
        Executable getComponent = () -> new HoverComponent(rawText);
        if (valid) assertDoesNotThrow(getComponent);
        else {
            Exception exception = assertThrows(RuntimeException.class, getComponent);
            assertTrue(exception instanceof MissingRequiredOptionException ||
                    exception instanceof InvalidOptionException);
        }
    }
}