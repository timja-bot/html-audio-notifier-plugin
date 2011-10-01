package jenkins.plugins.htmlaudio.util;


/**
 * Collection of string-related utils.
 * 
 * @author Lars Hvile
 */
public final class StringUtils {
    
    /**
     * Returns {@code null} if a provided string is empty.
     * @see org.apache.commons.lang.StringUtils#isBlank(String)
     */
    public static String nullIfEmpty(String str) {
        return org.apache.commons.lang.StringUtils.isBlank(str)
            ? null
            : str;
    }
    
    
    private StringUtils() {
    }
}
