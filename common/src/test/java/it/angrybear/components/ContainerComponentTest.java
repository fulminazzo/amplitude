package it.angrybear.components;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static it.angrybear.components.TextComponentTest.mockComponent;
import static org.junit.jupiter.api.Assertions.*;

class ContainerComponentTest {

    private static Object[][] getTestChildren() {
        Object[][] objects = TextComponentTest.getTestComponents();
        for (Object[] object : objects) {
            object[0] = String.format("<mock>%s</mock>", object[0]);
            object[1] = mockContainerComponent(null, object[1].toString());
        }
        return objects;
    }

    private static Object[][] getTestNext() {
        Object[][] objects = TextComponentTest.getTestComponents();
        for (Object[] object : objects) {
            object[0] = String.format("<mock>%s</mock>%s", object[0], object[0]);
            object[1] = mockContainerComponent(object[1].toString(), object[1].toString());
        }
        return objects;
    }

    private static Object[][] getTestEmptyNext() {
        Object[][] objects = TextComponentTest.getTestComponents();
        for (Object[] object : objects) {
            object[0] = String.format("<mock></mock>%s", object[0]);
            object[1] = mockContainerComponent(object[1].toString(), null);
        }
        return objects;
    }

    @ParameterizedTest
    @MethodSource("getTestChildren")
    void testChildrenInContainerComponent(String rawText, String expected) {
        MockContainer textComponent = new MockContainer(rawText);
        assertEquals(expected, textComponent.toString());
    }

    @ParameterizedTest
    @MethodSource("getTestNext")
    void testNextInContainerComponent(String rawText, String expected) {
        MockContainer textComponent = new MockContainer(rawText);
        assertEquals(expected, textComponent.toString());
    }

    @ParameterizedTest
    @MethodSource("getTestEmptyNext")
    void testEmptyNextInContainerComponent(String rawText, String expected) {
        MockContainer textComponent = new MockContainer(rawText);
        assertEquals(expected, textComponent.toString());
    }

    private static String mockContainerComponent(String next, String children) {
        String mock = mockComponent(null, null, null, null, null, null, null, null, null);
        mock = mock.replace("color:", String.format("tagName: mock, children: %s, color:", children));
        mock = mock.replaceAll("^\\{next: null","{next: " + next);
        return mock;
    }


    static class MockContainer extends ContainerComponent {

        public MockContainer(String rawText) {
            super(rawText, "mock");
        }
    }
}