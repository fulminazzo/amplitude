package it.angrybear.interfaces;

import it.angrybear.enums.Style;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ChatFormatterTest {

    @ParameterizedTest
    @MethodSource("it.angrybear.enums.Color#values")
    void testGetFormatterFromName(ChatFormatter formatter) {
        assertEquals(formatter, ChatFormatter.getChatFormatter(formatter.getName()));
    }

    @ParameterizedTest
    @EnumSource(Style.class)
    void testGetFormatterFromCharacter(ChatFormatter formatter) {
        assertEquals(formatter, ChatFormatter.getChatFormatter(formatter.getIdentifierChar()));
    }

    @ParameterizedTest
    @CsvSource({
            "BOLD,b",
            "BOLD,B",
            "ITALIC,i",
            "ITALIC,I",
            "STRIKETHROUGH,s",
            "STRIKETHROUGH,S",
            "UNDERLINE,u",
            "UNDERLINE,U"
    })
    void testAdditionalStyleCharacters(String expected, String character) {
        assertEquals(Style.valueOf(expected), ChatFormatter.getChatFormatter(character.charAt(0)));
    }

    @Test
    void testStrike() {
        assertEquals(Style.STRIKETHROUGH, ChatFormatter.getChatFormatter("strike"));
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