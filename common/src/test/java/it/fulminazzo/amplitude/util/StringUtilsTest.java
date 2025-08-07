package it.fulminazzo.amplitude.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testSplitQuoteSensitive() {
        String[] expected = new String[]{"Hello", "\"world, are you ok?\"", "hope", "'you,are'"};
        String rawString = "Hello,\"world, are you ok?\",hope,'you,are'";
        String[] actual = StringUtils.splitQuoteSensitive(rawString, ',');
        assertEquals(Arrays.toString(expected), Arrays.toString(actual),
                String.format("Expected had %s elements, Actual has %s", expected.length, actual.length));
        assertArrayEquals(expected, actual);
    }
}