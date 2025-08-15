package it.fulminazzo.amplitude.component;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * These are special tests that arise from problems encountered while working on other projects.
 * As such, they contain code or strings from those as testing benchmarks.
 */
class SpecialComponentTest {

    @Test
    void testToLegacy() {
        String raw = "<<lightpurple>player>";

        Component component = Component.fromRaw(raw);

        assertEquals("<§dplayer>", component.toLegacy());
    }

    private static Object[][] fromRawComponents() {
        return new Object[][]{
                new Object[]{
                        "<red>Dupe<white>Book <darkgray>» <white><red>You don't have enough experience. Required <darkred>1000 XP levels",
                        new Component("Dupe").setColor(Color.RED)
                                .addNext(new Component("Book ").setColor(Color.WHITE))
                                .addNext(new Component("» ").setColor(Color.DARK_GRAY))
                                .addNext(new Component().setColor(Color.WHITE))
                                .addNext(new Component("You don't have enough experience. Required ").setColor(Color.RED))
                                .addNext(new Component("1000 XP levels").setColor(Color.DARK_RED))
                }
        };
    }

    @ParameterizedTest
    @MethodSource("fromRawComponents")
    void testGeneralComponents(String text, Component expected) {
        Component component = Component.fromRaw(text);

        while (expected != null) {
            assertEquals(expected, component);
            expected = expected.getNext();
            component = component.getNext();
        }
    }

    private static Object[][] fromRawComponentsToSerialize() {
        return new Object[][]{
                new Object[]{"&f&l💬 Astra &8(<page>/<max_page>)", "<white><bold>💬 Astra <darkgray><bold>(<page>/<max_page>)"},
                new Object[]{
                        "<name>&8: &4<deaths> &cdeaths &8(&eSince last death: <time_since_death>&8)",
                        "<name><darkgray>: <darkred><deaths> <red>deaths <darkgray>(<yellow>Since last death: <time_since_death><darkgray>)"
                },
                new Object[]{
                        "<prefix>&eServer stats:\n" +
                                "&8- &fName: &8<server_name>\n" +
                                "&8- &fDate of birth: &a<date_of_birth>\n" +
                                "&8- &fAge: &2<age>\n" +
                                "&8- &fTotal players: &e<total_players>\n" +
                                "&8- &fTotal deaths: &c<total_deaths>",
                        "<prefix><yellow>Server stats:\n" +
                                "<darkgray>- <white>Name: <darkgray><server_name>\n" +
                                "- <white>Date of birth: <green><date_of_birth>\n" +
                                "<darkgray>- <white>Age: <darkgreen><age>\n" +
                                "<darkgray>- <white>Total players: <yellow><total_players>\n" +
                                "<darkgray>- <white>Total deaths: <red><total_deaths>"
                },
                new Object[]{
                        "<aqua><bold>DOL<red><bold>002 <darkgray><bold>» <reset><yellow>Server stats:\n" +
                                "<darkgray>- <white>Name: <darkgray><server_name>\n" +
                                "- <white>Date of birth: <green><date_of_birth>\n" +
                                "<darkgray>- <white>Age: <darkgreen>§e12 days§8, §e2 hours§8, §e35 minutes§8, §e22 seconds\n" +
                                "<darkgray>- <white>Total players: <yellow><total_players>\n" +
                                "<darkgray>- <white>Total deaths: <red><total_deaths>\n" +
                                "<yellow>Players top deaths:",
                        "<aqua><bold>DOL<red><bold>002 <darkgray><bold>» <reset><yellow>Server stats:\n" +
                                "<darkgray>- <white>Name: <darkgray><server_name>\n" +
                                "- <white>Date of birth: <green><date_of_birth>\n" +
                                "<darkgray>- <white>Age: <darkgreen><yellow>12 days<darkgray>, <yellow>2 hours<darkgray>, <yellow>35 minutes<darkgray>, <yellow>22 seconds\n" +
                                "<darkgray>- <white>Total players: <yellow><total_players>\n" +
                                "<darkgray>- <white>Total deaths: <red><total_deaths>\n" +
                                "<yellow>Players top deaths:"
                }
        };
    }

    @ParameterizedTest
    @MethodSource("fromRawComponentsToSerialize")
    void testGeneralComponentsSerialized(String text, String expected) {
        Component component = Component.fromRaw(text);

        assertEquals(expected, component.serialize());
    }

    private static Object[][] replaceComponents() {
        return new Object[][]{
                new Object[]{
                        "<player>",
                        "<player>",
                        "<hover action=\"SHOW_TEXT\" text=\"<white>Name: <yellow>Bipolale\n" +
                                "<white>First login: <green>07/18/2025 22:20\">Bipolale</hover>",
                        "<hover action=\"SHOW_TEXT\" text=\"<white>Name: <yellow>Bipolale\n" +
                                "<white>First login: <green>07/18/2025 22:20\">Bipolale</hover>"
                },
        };
    }

    @ParameterizedTest
    @MethodSource("replaceComponents")
    void testGeneralReplaceComponents(String text, String from, String to, String expected) {
        Component component = Component.fromRaw(text);
        Component fromComponent = Component.fromRaw(from);
        Component toComponent = Component.fromRaw(to);
        Component expectedComponent = Component.fromRaw(expected);

        Component actualComponent = component.replace(fromComponent, toComponent);

        assertEquals(expectedComponent, actualComponent);
    }

}