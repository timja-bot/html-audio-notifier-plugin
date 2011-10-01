package jenkins.plugins.htmlaudio.util;

import static jenkins.plugins.htmlaudio.util.StringUtils.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class StringUtilsTest {
    
    @Test
    public void empty_strings_are_converted_to_nulls() {
        assertEquals("a", nullIfEmpty("a"));
        assertEquals(" abc ", nullIfEmpty(" abc "));
        assertNull(nullIfEmpty(" "));
        assertNull(nullIfEmpty(""));
        assertNull(nullIfEmpty(null));
        assertNull(nullIfEmpty("\t\n  "));
    }
}
