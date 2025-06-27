package it.angrybear.serializers;

import it.angrybear.components.*;
import it.angrybear.components.ClickAction;
import it.angrybear.components.HoverAction;
import it.angrybear.exception.InvalidOptionException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("deprecation")
class LegacyBungeeSerializerTest {
    private static final LegacyBungeeSerializer serializer = new LegacyBungeeSerializer();

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
                        new net.md_5.bungee.api.chat.TextComponent("achievement.mineWood"), "id=achievement.mineWood"
                },
                new Object[]{HoverAction.SHOW_TEXT, new net.md_5.bungee.api.chat.TextComponent("Hello friend!"),
                        "text=\"Hello friend!\""
                },
                new Object[]{HoverAction.SHOW_ENTITY,
                        new net.md_5.bungee.api.chat.TextComponent("{type:zombie,id:\"3f8164bf-1ed-4bcb-96be-7033beed028c\",name:Zombie}"),
                        "id=\"3f8164bf-1ed-4bcb-96be-7033beed028c\" type=\"zombie\" name=\"Zombie\""
                },
                new Object[]{HoverAction.SHOW_ITEM,
                        new net.md_5.bungee.api.chat.TextComponent("{Count:1b,id:\"minecraft:stone_sword\",tag:{Damage: 0, Enchantments:[{id:\"minecraft:sharpness\",lvl:5s}]}}"),
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

        BaseComponent c2 = createComponent(ChatColor.RED.toString());
        addExtra(c2, createComponent(ChatColor.BOLD + "Hello world, "));
        addExtra(c2, createComponent("are you ready? "));
        addExtra(c2, createComponent(ChatColor.BOLD + "Hope you are... "));
        addExtra(c2, createComponent(ChatColor.BOLD + "or else... "));
        addExtra(c2, createComponent(ChatColor.RESET + "This should be reset. ", this::resetComponent));
        addExtra(c2, createComponent(ChatColor.WHITE.toString()));
        addExtra(c2, createComponent("Insert DEMO ", c -> c.setInsertion("Hello there!")));
        addExtra(c2, createComponent(""));
        net.md_5.bungee.api.chat.TranslatableComponent tc = new net.md_5.bungee.api.chat.TranslatableComponent("commands.give.successful.single");
        tc.addWith(new net.md_5.bungee.api.chat.TextComponent("Diamond Sword"));
        tc.addWith(new net.md_5.bungee.api.chat.TextComponent("1"));
        tc.addWith(new net.md_5.bungee.api.chat.TextComponent("Alex & Friends"));
        addExtra(c2, tc);

        BaseComponent temp = c2;
        for (Object[] objects : getClickTests()) {
            ClickAction action = (ClickAction) objects[0];
            final String option = (String) objects[1];

            if (action.equals(ClickAction.OPEN_FILE)) continue;

            final String text = String.format("Click %s Demo", action.name());
            final String required = new ArrayList<>(action.getRequiredOptions().keySet()).get(0);

            BaseComponent c = createComponent("");

            BaseComponent component = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(text)[0];
            component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(action.name()), option));
            c.addExtra(component);

            BaseComponent cc = createComponent(" ");
            if (!action.equals(ClickAction.COPY_TO_CLIPBOARD)) {
                c.addExtra(cc);
                addExtra(temp, c);
                temp = cc;
            } else{
                addExtra(temp, c);
                temp = c;
            }

            rawText += String.format("<click action=%s %s=\"%s\">%s</click> ", action, required, option, text);
        }

        BaseComponent cd = createComponent(" ");
        temp.addExtra(cd);
        temp = cd;

        for (Object[] objects : getHoverTests()) {
            HoverAction action = (HoverAction) objects[0];
            final BaseComponent content = (BaseComponent) objects[1];
            final String option = (String) objects[2];

            if (action.equals(HoverAction.SHOW_ACHIEVEMENT)) continue;

            final String text = String.format("Hover %s Demo", action.name());

            BaseComponent c = createComponent("");

            BaseComponent component = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(text)[0];
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(action.name()), new BaseComponent[]{content}));
            c.addExtra(component);

            BaseComponent cc = createComponent(" ");
            if (!action.equals(HoverAction.SHOW_ITEM)) c.addExtra(cc);

            addExtra(temp, c);
            temp = cc;

            rawText += String.format("<hover action=%s %s>%s</hover> ", action, option, text);
        }

        TextComponent c1 = new TextComponent(rawText);
        BaseComponent c = serializer.serializeComponent(c1);
        assertNotNull(c);
        assertEquals(c2.toString(), c.toString(), rawText);
    }

    @Test
    void testSimpleComponent() {
        String rawText = "Hello world";
        TextComponent c1 = new TextComponent("<red>" + rawText);
        BaseComponent c2 = new net.md_5.bungee.api.chat.TextComponent(rawText);
        c2.setColor(ChatColor.RED);
        assertEquals(c2, serializer.serializeSimpleTextComponent(c1));
    }

    @ParameterizedTest
    @MethodSource("getClickTests")
    void testClickComponent(ClickAction action, String option) throws Throwable {
        Executable executable = () -> {
            ClickComponent c1 = new ClickComponent("<click action=" + action + " " +
                    new ArrayList<>(action.getRequiredOptions().keySet()).get(0) +
                    "=\"" + option + "\">Test</click>");
            BaseComponent c2 = new net.md_5.bungee.api.chat.TextComponent("Test");
            c2.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(action.name()), option));
            BaseComponent tmp = new net.md_5.bungee.api.chat.TextComponent();
            tmp.addExtra(c2);
            assertEquals(tmp, serializer.serializeClickComponent(c1));
        };
        if (action.equals(ClickAction.OPEN_FILE))
            assertThrows(InvalidOptionException.class, executable);
        else executable.execute();
    }

    @ParameterizedTest
    @MethodSource("getHoverTests")
    void testHoverComponent(HoverAction action, BaseComponent content, String options) {
        HoverComponent c1 = new HoverComponent("<hover action=" + action + " " + options + ">Test</hover>");
        BaseComponent c2 = new net.md_5.bungee.api.chat.TextComponent("Test");
        c2.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(action.name()), new BaseComponent[]{content}));
        BaseComponent tmp = new net.md_5.bungee.api.chat.TextComponent();
        tmp.addExtra(c2);
        assertEquals(tmp.toString(), serializer.serializeHoverComponent(c1).toString());
    }

    @Test
    void testHexComponent() {
        String color = "#FF00AA";
        String rawText = "Hello world";
        HexComponent c1 = new HexComponent("<hex color=" + color + ">" + rawText);
        BaseComponent[] c = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(rawText);
        BaseComponent c2 = c[0];
        for (int i = 1; i < c.length; i++) c2.addExtra(c[i]);
        assertEquals(c2, serializer.serializeHexComponent(c1));
    }

    @Test
    void testHexComponentShowingHex() {
        String color = "#FF00AA";
        String rawText = "Hello world";
        HexComponent c1 = new HexComponent("<hex color=" + color + ">" + rawText);
        BaseComponent[] c = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(color + rawText);
        BaseComponent c2 = c[0];
        for (int i = 1; i < c.length; i++) c2.addExtra(c[i]);
        serializer.setShowingHex(true);
        assertEquals(c2, serializer.serializeHexComponent(c1));
        serializer.setShowingHex(false);
    }

    @Test
    void testSend() {
        TextComponent component = new TextComponent("This is an example");
        ProxiedPlayer player = mock(ProxiedPlayer.class);
        serializer.send(player, component);
        verify(player, atLeastOnce()).sendMessage((BaseComponent) serializer.serializeComponent(component));
    }

    @Test
    void testSerializerMethod() {
        assertEquals(LegacyBungeeSerializer.class, ComponentSerializer.serializer().getClass());
    }

    private void addExtra(BaseComponent c1, BaseComponent c2) {
        if (c1.getExtra() == null || (c1.getClickEvent() != null || c1.getHoverEvent() != null))
            c1.addExtra(c2);
        else addExtra(c1.getExtra().get(c1.getExtra().size() - 1), c2);
    }

    private void resetComponent(BaseComponent c) {
        c.setBold(false);
        c.setStrikethrough(false);
        c.setItalic(false);
        c.setObfuscated(false);
        c.setUnderlined(false);
    }

    private BaseComponent createComponent(String text) {
        return createComponent(text, null);
    }

    private BaseComponent createComponent(String text, Consumer<BaseComponent> function) {
        BaseComponent[] component;
        if (text.isEmpty()) component = new BaseComponent[]{new net.md_5.bungee.api.chat.TextComponent()};
        else component = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(text);
        BaseComponent c = component[0];
        if (function != null) function.accept(c);
        for (int i = 1; i < component.length; i++) {
            c.addExtra(component[i]);
            if (function != null) function.accept(c);
        }
        return c;
    }
}