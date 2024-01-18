package it.angrybear.serializers;

import it.angrybear.components.HexComponent;
import it.angrybear.components.TextComponent;
import it.angrybear.enums.ClickAction;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AdventureSerializerTest {
    private static final AdventureSerializer serializer = new AdventureSerializer();

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
        //TODO:
//        return new Object[][]{
//                new Object[]{HoverAction.SHOW_ACHIEVEMENT,
//                        null, "id=achievement.mineWood"
//                },
//                new Object[]{HoverAction.SHOW_TEXT, new Text("Hello friend!"),
//                        "text=\"Hello friend!\""
//                },
//                new Object[]{HoverAction.SHOW_ENTITY,
//                        new Entity("zombie", "3f8164bf-1ed-4bcb-96be-7033beed028c",
//                                new net.md_5.bungee.api.chat.TextComponent("Zombie")),
//                        "id=\"3f8164bf-1ed-4bcb-96be-7033beed028c\" type=\"zombie\" name=\"Zombie\""
//                },
//                new Object[]{HoverAction.SHOW_ITEM,
//                        new Item("minecraft:stone_sword", 1, ItemTag.ofNbt("{Damage: 0, Enchantments:[{id:\"minecraft:sharpness\",lvl:5s}]}")),
//                        "id=minecraft:stone_sword Count=1b Tag=\"{Damage: 0, Enchantments:[{id:\\\"minecraft:sharpness\\\",lvl:5s}]}\""
//                }
//        };
        return null;
    }

    @Test
    void testMultiple() {
//        String rawText = "<red>"
//                + "<bold>Hello world, "
//                + "<hex color=#FF00AA>are you ready? "
//                + "<bold>Hope you are... "
//                + "<reset>This should be reset. "
//                ;
//
//        BaseComponent c2 = createComponent(ChatColor.RED.toString());
//        addExtra(c2, createComponent(ChatColor.BOLD + "Hello world, "));
//        addExtra(c2, createComponent(ChatColor.of("#FF00AA") + "are you ready? "));
//        addExtra(c2, createComponent(ChatColor.of("#FF00AA") + ChatColor.BOLD.toString() + "Hope you are... "));
//        addExtra(c2, createComponent(ChatColor.RESET + "This should be reset. ", this::resetComponent));
//
//        BaseComponent temp = c2;
//        for (Object[] objects : getClickTests()) {
//            ClickAction action = (ClickAction) objects[0];
//            final String option = (String) objects[1];
//
//            if (action.equals(ClickAction.OPEN_FILE)) continue;
//
//            final String text = String.format("Click %s Demo", action.name());
//            final String required = new ArrayList<>(action.getRequiredOptions().keySet()).get(0);
//
//            BaseComponent c = createComponent("");
//
//            BaseComponent component = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(text)[0];
//            resetComponent(component);
//            component.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(action.name()), option));
//            c.addExtra(component);
//
//            BaseComponent cc = createComponent(ChatColor.WHITE + " ");
//            resetComponent(cc);
//            if (!action.equals(ClickAction.COPY_TO_CLIPBOARD)) {
//                c.addExtra(cc);
//                addExtra(temp, c);
//                temp = cc;
//            } else{
//                addExtra(temp, c);
//                temp = c;
//            }
//
//            rawText += String.format("<click action=%s %s=\"%s\">%s</click> ", action, required, option, text);
//        }
//
//        BaseComponent cd = createComponent(ChatColor.WHITE + " ");
//        resetComponent(cd);
//        temp.addExtra(cd);
//        temp = cd;
//
//        for (Object[] objects : getHoverTests()) {
//            HoverAction action = (HoverAction) objects[0];
//            final Content content = (Content) objects[1];
//            final String option = (String) objects[2];
//
//            if (action.equals(HoverAction.SHOW_ACHIEVEMENT)) continue;
//
//            final String text = String.format("Hover %s Demo", action.name());
//
//            BaseComponent c = createComponent("");
//
//            BaseComponent component = net.md_5.bungee.api.chat.TextComponent.fromLegacyText(text)[0];
//            resetComponent(component);
//            component.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(action.name()), content));
//            c.addExtra(component);
//
//            BaseComponent cc = createComponent(ChatColor.WHITE + " ");
//            resetComponent(cc);
//            if (!action.equals(HoverAction.SHOW_ITEM)) c.addExtra(cc);
//
//            addExtra(temp, c);
//            temp = cc;
//
//            rawText += String.format("<hover action=%s %s>%s</hover> ", action, option, text);
//        }
//
//        TextComponent c1 = new TextComponent(rawText);
//        BaseComponent c = serializer.serializeComponent(c1);
//        assertNotNull(c);
//        assertEquals(c2.toString(), c.toString(), rawText);
    }

    @Test
    void testSimpleComponent() {
        String rawText = "Hello world";
        TextComponent c1 = new TextComponent("<red>" + rawText);
        Component c2 = Component.text(rawText);
        c2 = c2.color(NamedTextColor.RED);
        assertEquals(c2, serializer.serializeSimpleTextComponent(c1));
    }

    @ParameterizedTest
    @MethodSource("getClickTests")
    void testClickComponent(ClickAction action, String option) throws Throwable {
//        Executable executable = () -> {
//            ClickComponent c1 = new ClickComponent("<click action=" + action + " " +
//                    new ArrayList<>(action.getRequiredOptions().keySet()).get(0) +
//                    "=\"" + option + "\">Test</click>");
//            BaseComponent c2 = new net.md_5.bungee.api.chat.TextComponent("Test");
//            c2.setClickEvent(new ClickEvent(ClickEvent.Action.valueOf(action.name()), option));
//            BaseComponent tmp = new net.md_5.bungee.api.chat.TextComponent();
//            tmp.addExtra(c2);
//            assertEquals(tmp, serializer.serializeClickComponent(c1));
//        };
//        if (action.equals(ClickAction.OPEN_FILE))
//            assertThrows(InvalidOptionException.class, executable);
//        else executable.execute();
    }

//    @ParameterizedTest
//    @MethodSource("getHoverTests")
//    void testHoverComponent(HoverAction action, Content content, String options) {
////        HoverComponent c1 = new HoverComponent("<hover action=" + action + " " + options + ">Test</hover>");
////        assumeFalse(action.equals(HoverAction.SHOW_ACHIEVEMENT));
////        BaseComponent c2 = new net.md_5.bungee.api.chat.TextComponent("Test");
////        c2.setHoverEvent(new HoverEvent(HoverEvent.Action.valueOf(action.name()), content));
////        BaseComponent tmp = new net.md_5.bungee.api.chat.TextComponent();
////        tmp.addExtra(c2);
////        assertEquals(tmp.toString(), serializer.serializeHoverComponent(c1).toString());
//    }

    @Test
    void testHexComponent() {
        String color = "#FF00AA";
        String rawText = "Hello world";
        HexComponent c1 = new HexComponent("<hex color=" + color + ">" + rawText);
        Component c2 = Component.text(rawText)
                        .color(TextColor.fromHexString(color));
        assertEquals(c2, serializer.serializeHexComponent(c1));
    }

    @Test
    void testSend() {
//        TextComponent component = new TextComponent("This is an example");
//        ProxiedPlayer player = mock(ProxiedPlayer.class);
//        serializer.send(player, component);
//        verify(player, atLeastOnce()).sendMessage((BaseComponent) serializer.serializeComponent(component));
    }

    @Test
    void testSerializerMethod() {
        assertEquals(AdventureSerializer.class, ComponentSerializer.serializer().getClass());
    }
}