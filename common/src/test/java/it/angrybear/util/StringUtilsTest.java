package it.angrybear.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test
    void testSplitQuoteSensitive() {
        String[] tmp = new String[]{"Hello", "\"world, are you ok?\"", "hope", "'you,are'"};
        String rawString = "Hello,\"world, are you ok?\",hope,'you,are'";
        assertEquals(Arrays.toString(tmp), Arrays.toString(StringUtils.splitQuoteSensitive(rawString, ',')));
        assertArrayEquals(tmp, StringUtils.splitQuoteSensitive(rawString, ','));
    }
}