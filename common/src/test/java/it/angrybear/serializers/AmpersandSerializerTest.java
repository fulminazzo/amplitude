package it.angrybear.serializers;

import it.angrybear.components.TextComponent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AmpersandSerializerTest {

    @ParameterizedTest
    @CsvSource({"&cHello world,<red>Hello world",
            "<reed>Hello world,<reed>Hello world",
            "&kHello world,<magic>Hello world",
            "&lHello world,<bold>Hello world",
            "&mHello world,<strikethrough>Hello world",
            "&nHello world,<underline>Hello world",
            "&oHello world,<italic>Hello world",
            "&c&c&lHello world,<red><bold>Hello world",
            "&c&c&l&c&l&oHello world,<red><bold><italic>Hello world",
            "&c&c&l&c&l&oHello &rworld,<red><bold><italic>Hello <reset>world"})
    void testVariousMessages(String expected, String rawText) {
        TextComponent textComponent = new TextComponent(rawText);
        assertEquals(expected, new AmpersandSerializer().serializeComponent(textComponent));
    }
}