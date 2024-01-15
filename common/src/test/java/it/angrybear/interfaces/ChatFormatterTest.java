package it.angrybear.interfaces;

import it.angrybear.enums.Color;
import it.angrybear.enums.Style;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.*;

class ChatFormatterTest {

    @ParameterizedTest
    @EnumSource(Color.class)
    void testGetFormatterFromName(ChatFormatter formatter) {
        assertEquals(formatter, ChatFormatter.getChatFormatter(formatter.getName()));
    }

    @ParameterizedTest
    @EnumSource(Style.class)
    void testGetFormatterFromCharacter(ChatFormatter formatter) {
        assertEquals(formatter, ChatFormatter.getChatFormatter(formatter.getIdentifierChar()));
    }

    @Test
    void testNotExistingChatFormatterName() {
        assertNull(ChatFormatter.getChatFormatter("not existing"));
    }

    @Test
    void testNotExistingChatFormatterCharacter() {
        assertNull(ChatFormatter.getChatFormatter('z'));
    }
}