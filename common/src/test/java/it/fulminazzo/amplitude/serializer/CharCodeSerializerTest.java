package it.fulminazzo.amplitude.serializer;

import it.fulminazzo.amplitude.component.Component;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CharCodeSerializerTest {

    @ParameterizedTest
    @CsvSource({
            "&c&cHello world,<red><hover action=SHOW_TEXT text=\"Hello friend\">Hello world</hover>",
            "&x&F&F&0&0&A&AHello &x&F&F&0&0&A&A&lworld,<hex color=#FF00AA>Hello <bold>world",
            "<reed>Hello world,<reed>Hello <click action=OPEN_URL url=https://www.google.com>world</click>",
            "<reed>Hello world how are you today?,<reed>Hello <click action=OPEN_URL url=https://www.google.com>world</click> how are you today?",
            "<reed>Hello world &dhow are you today?,<reed>Hello <click action=OPEN_URL url=https://www.google.com>world <light_purple>how</click> are you today?",
            "&kHello world,<obfuscated>Hello world",
            "&lHello world,<bold>Hello world",
            "&mHello world,<strikethrough>Hello world",
            "&nHello world,<underlined>Hello world",
            "&oHello world,<italic>Hello world",
            "&oHello &rworld,<italic>Hello <reset>world",
            "Hello world,<insertion text=\"Invisible\">Hello world</insertion>",
            "Hello world,<translatable>Hello world</translatable>",
            "Hello world,<font id=\"ILLAGERALT\">Hello world",
            "&c&c&lHello world,<red><bold>Hello world",
            "&c&c&l&c&l&oHello world,<red><bold><italic>Hello world",
            "&c&c&l&c&l&oHello &rworld,<red><bold><italic>Hello <reset>world"
    })
    void testVariousMessagesAmpersand(String expected, String rawText) {
        Component component = new Component(rawText);
        assertEquals(expected, ComponentSerializer.ampersand().serializeComponent(component));
    }

    @ParameterizedTest
    @CsvSource({
            "§cHello world,<red>Hello world",
            "<reed>Hello world,<reed>Hello world",
            "§kHello world,<obfuscated>Hello world",
            "§lHello world,<bold>Hello world",
            "§mHello world,<strikethrough>Hello world",
            "§nHello world,<underlined>Hello world",
            "§oHello world,<italic>Hello world",
            "Hello world,<insertion text=\"Invisible\">Hello world</insertion>",
            "Hello world,<font id=\"ILLAGERALT\">Hello world",
            "§c§c§lHello world,<red><bold>Hello world",
            "§c§c§l§c§l§oHello world,<red><bold><italic>Hello world",
            "§c§c§l§c§l§oHello §rworld,<red><bold><italic>Hello <reset>world"
    })
    void testVariousMessagesSectionSign(String expected, String rawText) {
        Component component = new Component(rawText);
        assertEquals(expected, ComponentSerializer.sectionSign().serializeComponent(component));
    }

    @ParameterizedTest
    @CsvSource({
            "<COLOR>cHello world,<red>Hello world",
            "<reed>Hello world,<reed>Hello world",
            "<COLOR>kHello world,<obfuscated>Hello world",
            "<COLOR>lHello world,<bold>Hello world",
            "<COLOR>mHello world,<strikethrough>Hello world",
            "<COLOR>nHello world,<underlined>Hello world",
            "<COLOR>oHello world,<italic>Hello world",
            "Hello world,<insertion text=\"Invisible\">Hello world</insertion>",
            "Hello world,<font id=\"ILLAGERALT\">Hello world",
            "<COLOR>c<COLOR>c<COLOR>lHello world,<red><bold>Hello world",
            "<COLOR>c<COLOR>c<COLOR>l<COLOR>c<COLOR>l<COLOR>oHello world,<red><bold><italic>Hello world",
            "<COLOR>c<COLOR>c<COLOR>l<COLOR>c<COLOR>l<COLOR>oHello <COLOR>rworld,<red><bold><italic>Hello <reset>world"
    })
    void testVariousMessagesAbstract(String expected, String rawText) {
        Component component = new Component(rawText);
        assertEquals(expected, new CharCodeSerializer("<COLOR>").serializeComponent(component));
    }
}