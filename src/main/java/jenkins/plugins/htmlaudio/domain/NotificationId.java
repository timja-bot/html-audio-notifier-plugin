package jenkins.plugins.htmlaudio.domain;

import static java.lang.Long.parseLong;
import static org.apache.commons.lang.StringUtils.isBlank;


/**
 * Uniquely identifies a {@link Notification}.
 * 
 * @author Lars Hvile
 */
public final class NotificationId implements Comparable<NotificationId> {
    
    private final long value;
    
    
    /**
     * Creates a {@link NotificationId} from a long-value. 
     */
    public static NotificationId asNotificationId(long value) {
        return new NotificationId(value);
    }
    
    
    /**
     * Parses a {@link NotificationId} from a string.
     * 
     * @return a {@link NotificationId} or {@code null} if invalid
     */
    public static NotificationId parseNotificationId(String value) {
        try {
            return isBlank(value)
                ? null
                : asNotificationId(parseLong(value.trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    
    private NotificationId(long value) {
        this.value = value;
    }
    
    
    @Override
    public boolean equals(Object o) {
        return o instanceof NotificationId
            && this.value == ((NotificationId)o).value;
    }
    
    
    @Override
    public int hashCode() {
        return Long.valueOf(value).hashCode();
    }
    
    
    public int compareTo(NotificationId o) {
        return Long.valueOf(value).compareTo(o.value);
    }
    
    
    @Override
    public String toString() {
        return value + "";
    }
    
    
    public long getValue() {
        return value;
    }
}
