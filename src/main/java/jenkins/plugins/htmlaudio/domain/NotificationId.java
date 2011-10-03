package jenkins.plugins.htmlaudio.domain;

import java.util.concurrent.atomic.AtomicLong;


/**
 * Uniquely identifies a {@link Notification}.
 * 
 * @author Lars Hvile
 */
public final class NotificationId implements Comparable<NotificationId> {
    
    private static final AtomicLong idSequence = new AtomicLong();
    
    private final long value;
    
    
    /**
     * Creates a new id.
     */
    public static NotificationId createNotificationId() {
        return asNotificationId(idSequence.incrementAndGet());
    }
    
    
    /**
     * Creates a {@link NotificationId} from a long-value. 
     */
    public static NotificationId asNotificationId(long value) {
        return new NotificationId(value);
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
