package it.angrybear.serializers;

import it.angrybear.components.TextComponent;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharCodeSerializerTest {

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
    void testVariousMessagesAmpersand(String expected, String rawText) {
        TextComponent textComponent = new TextComponent(rawText);
        assertEquals(expected, new AmpersandSerializer().serializeComponent(textComponent));
    }

    @ParameterizedTest
    @CsvSource({"§cHello world,<red>Hello world",
            "<reed>Hello world,<reed>Hello world",
            "§kHello world,<magic>Hello world",
            "§lHello world,<bold>Hello world",
            "§mHello world,<strikethrough>Hello world",
            "§nHello world,<underline>Hello world",
            "§oHello world,<italic>Hello world",
            "§c§c§lHello world,<red><bold>Hello world",
            "§c§c§l§c§l§oHello world,<red><bold><italic>Hello world",
            "§c§c§l§c§l§oHello §rworld,<red><bold><italic>Hello <reset>world"})
    void testVariousMessagesSectionSign(String expected, String rawText) {
        TextComponent textComponent = new TextComponent(rawText);
        assertEquals(expected, new SectionSignSerializer().serializeComponent(textComponent));
    }

    @ParameterizedTest
    @CsvSource({"<COLOR>cHello world,<red>Hello world",
            "<reed>Hello world,<reed>Hello world",
            "<COLOR>kHello world,<magic>Hello world",
            "<COLOR>lHello world,<bold>Hello world",
            "<COLOR>mHello world,<strikethrough>Hello world",
            "<COLOR>nHello world,<underline>Hello world",
            "<COLOR>oHello world,<italic>Hello world",
            "<COLOR>c<COLOR>c<COLOR>lHello world,<red><bold>Hello world",
            "<COLOR>c<COLOR>c<COLOR>l<COLOR>c<COLOR>l<COLOR>oHello world,<red><bold><italic>Hello world",
            "<COLOR>c<COLOR>c<COLOR>l<COLOR>c<COLOR>l<COLOR>oHello <COLOR>rworld,<red><bold><italic>Hello <reset>world"})
    void testVariousMessagesAbstract(String expected, String rawText) {
        TextComponent textComponent = new TextComponent(rawText);
        assertEquals(expected, new CharCodeSerializer("<COLOR>").serializeComponent(textComponent));
    }
}