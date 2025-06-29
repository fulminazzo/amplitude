package it.fulminazzo.amplitude.converter;

import it.fulminazzo.amplitude.component.ClickComponent;
import it.fulminazzo.amplitude.component.HexComponent;
import it.fulminazzo.amplitude.component.HoverComponent;
import it.fulminazzo.amplitude.component.Component;
import it.fulminazzo.amplitude.component.ClickAction;
import it.fulminazzo.amplitude.component.HoverAction;
import it.fulminazzo.amplitude.exception.InvalidOptionException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdventureConverterTest {
    private static final AdventureConverter serializer = new AdventureConverter();

    private static Object[][] getClickTests() {
        return new Object[][]{
                new Object[]{ClickAction.OPEN_URL, "https://www.google.com"},
                new Object[]{ClickAction.OPEN_FILE, "/home/smith/Desktop/pass.txt"},
                new Object[]{ClickAction.RUN_COMMAND, "say Hello world"},
                new Object[]{ClickAction.SUGGEST_COMMAND, "say Hello world"},
                new Object[]{ClickAction.CHANGE_PAGE, "1"},
                new Object[]{ClickAction.COPY_TO_CLIPBOARD, "Simple text"},
        };
    }

    private static Object[][] getHoverTests() {
        return new Object[][]{
                new Object[]{HoverAction.SHOW_ACHIEVEMENT,
                        HoverEvent.showAchievement("achievement.mineWood"), "id=achievement.mineWood"
                },
                new Object[]{HoverAction.SHOW_TEXT,
                        HoverEvent.showText(net.kyori.adventure.text.Component.text("Hello friend!")),
                        "text=\"Hello friend!\""
                },
                new Object[]{HoverAction.SHOW_ENTITY,
                        HoverEvent.showEntity(Key.key("zombie"),
                                UUID.fromString("3f8164bf-1ed-4bcb-96be-7033beed028c"),
                                net.kyori.adventure.text.Component.text("Zombie")),
                        "id=\"3f8164bf-1ed-4bcb-96be-7033beed028c\" type=\"zombie\" name=\"Zombie\""
                },
                new Object[]{HoverAction.SHOW_ITEM,
                        HoverEvent.showItem(Key.key("stone_sword"),
                                1, BinaryTagHolder.binaryTagHolder("{Damage: 0, Enchantments:[{id:\"minecraft:sharpness\",lvl:5s}]}")),
                        "id=minecraft:stone_sword Count=1b Tag=\"{Damage: 0, Enchantments:[{id:\\\"minecraft:sharpness\\\",lvl:5s}]}\""
                }
        };
    }

    @Test
    void testMultiple() {
        String rawText = "<red>"
                + "<bold>Hello world, "
                + "<hex color=#FF00AA>are you ready? "
                + "<bold>Hope you are... "
                + "<font id=\"ILLAGERALT\">or else... "
                + "<reset>This should be reset. "
                + "<insertion text=\"Hello there!\">Insert DEMO </insertion>"
                + "<translatable arguments=\"Diamond Sword&1&\\\"Alex & Friends\\\"\">commands.give.successful.single</translatable>"
                ;

        net.kyori.adventure.text.Component c2 = net.kyori.adventure.text.Component.text("").color(NamedTextColor.RED);
        c2 = addExtra(c2, net.kyori.adventure.text.Component.text("Hello world, ")
                .color(NamedTextColor.RED)
                .decorate(TextDecoration.BOLD));
        c2 = addExtra(c2, net.kyori.adventure.text.Component.text("are you ready? ")
                .color(TextColor.fromHexString("#FF00AA"))
                .decorate(TextDecoration.BOLD));
        c2 = addExtra(c2, net.kyori.adventure.text.Component.text("Hope you are... ")
                .color(TextColor.fromHexString("#FF00AA"))
                .decorate(TextDecoration.BOLD));
        c2 = addExtra(c2, net.kyori.adventure.text.Component.text("or else... ")
                .color(TextColor.fromHexString("#FF00AA"))
                .decorate(TextDecoration.BOLD)
                .font(Key.key("illageralt"))
        );
        c2 = addExtra(c2, net.kyori.adventure.text.Component.text("This should be reset. ")
                .color(NamedTextColor.WHITE)
                .decoration(TextDecoration.BOLD, TextDecoration.State.FALSE)
                .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE)
                .decoration(TextDecoration.STRIKETHROUGH, TextDecoration.State.FALSE)
                .decoration(TextDecoration.OBFUSCATED, TextDecoration.State.FALSE)
                .decoration(TextDecoration.UNDERLINED, TextDecoration.State.FALSE)
                .font(Key.key("default"))
        );
        c2 = addExtra(c2, net.kyori.adventure.text.Component.text("Insert DEMO ")
                .insertion("Hello there!")
        );
        c2 = addExtra(c2, net.kyori.adventure.text.Component.translatable("commands.give.successful.single").args(
                net.kyori.adventure.text.Component.text("Diamond Sword"), net.kyori.adventure.text.Component.text("1"), net.kyori.adventure.text.Component.text("Alex & Friends")
        ));

        for (Object[] objects : getClickTests()) {
            ClickAction action = (ClickAction) objects[0];
            final String option = (String) objects[1];

            if (action.equals(ClickAction.OPEN_FILE)) continue;

            final String text = String.format("Click %s Demo", action.name());
            final String required = new ArrayList<>(action.getRequiredOptions().keySet()).get(0);

            net.kyori.adventure.text.Component c = net.kyori.adventure.text.Component.text(text)
                    .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.valueOf(action.name()), option));
            c2 = addExtra(c2, c);

            if (!action.equals(ClickAction.COPY_TO_CLIPBOARD))
                c2 = addExtra(c2, net.kyori.adventure.text.Component.text(" "));

            rawText += String.format("<click action=%s %s=\"%s\">%s</click> ", action, required, option, text);
        }

        c2 = addExtra(c2, net.kyori.adventure.text.Component.text(" "));

        for (Object[] objects : getHoverTests()) {
            HoverAction action = (HoverAction) objects[0];
            final HoverEvent<?> hoverEvent = (HoverEvent<?>) objects[1];
            final String option = (String) objects[2];

            final String text = String.format("Hover %s Demo", action.name());

            net.kyori.adventure.text.Component component = net.kyori.adventure.text.Component.text(text).hoverEvent(hoverEvent);
            c2 = addExtra(c2, component);

            if (!action.equals(HoverAction.SHOW_ITEM))
                c2 = addExtra(c2, net.kyori.adventure.text.Component.text(" "));

            rawText += String.format("<hover action=%s %s>%s</hover> ", action, option, text);
        }

        Component c1 = new Component(rawText);
        net.kyori.adventure.text.Component c = serializer.convertComponent(c1);
        assertNotNull(c);
        assertEquals(c2, c, rawText);
    }

    @Test
    void testSimpleComponent() {
        String rawText = "Hello world";
        Component c1 = new Component("<red>" + rawText);
        net.kyori.adventure.text.Component c2 = net.kyori.adventure.text.Component.text(rawText);
        c2 = c2.color(NamedTextColor.RED);
        assertEquals(c2, serializer.convertSimpleComponent(c1));
    }

    @ParameterizedTest
    @MethodSource("getClickTests")
    void testClickComponent(ClickAction action, String option) throws Throwable {
        Executable executable = () -> {
            ClickComponent c1 = new ClickComponent("<click action=" + action + " " +
                    new ArrayList<>(action.getRequiredOptions().keySet()).get(0) +
                    "=\"" + option + "\">Test</click>");
            net.kyori.adventure.text.Component c2 = net.kyori.adventure.text.Component.text("Test");
            c2 = c2.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.valueOf(action.name()), option));
            assertEquals(c2, serializer.convertClickComponent(c1));
        };
        if (action.equals(ClickAction.OPEN_FILE))
            assertThrows(InvalidOptionException.class, executable);
        else executable.execute();
    }

    @ParameterizedTest
    @MethodSource("getHoverTests")
    void testHoverComponent(HoverAction action, HoverEvent<?> hoverEvent, String options) {
        HoverComponent c1 = new HoverComponent("<hover action=" + action + " " + options + ">Test</hover>");
        net.kyori.adventure.text.Component c2 = net.kyori.adventure.text.Component.text("Test");
        c2 = c2.hoverEvent(hoverEvent);
        assertEquals(c2, serializer.convertHoverComponent(c1));
    }

    @Test
    void testHexComponent() {
        String color = "#FF00AA";
        String rawText = "Hello world";
        HexComponent c1 = new HexComponent("<hex color=" + color + ">" + rawText);
        net.kyori.adventure.text.Component c2 = net.kyori.adventure.text.Component.text(rawText)
                        .color(TextColor.fromHexString(color));
        assertEquals(c2, serializer.convertHexComponent(c1));
    }

    @Test
    void testSend() {
        Component component = new Component("This is an example");
        Audience player = mock(Audience.class);
        serializer.send(player, component);
        verify(player, atLeastOnce()).sendMessage(serializer.convertComponent(component));
    }

    @Test
    void testConverterMethod() {
        assertEquals(AdventureConverter.class, ComponentConverter.converter().getClass());
    }

    private net.kyori.adventure.text.Component addExtra(net.kyori.adventure.text.Component c1, net.kyori.adventure.text.Component c2) {
        List<net.kyori.adventure.text.Component> children = new ArrayList<>(c1.children());
        if (children.isEmpty()) return c1.append(c2);
        else {
            children.set(children.size() - 1, addExtra(children.get(children.size() - 1), c2));
            return c1.children(children);
        }
    }
}