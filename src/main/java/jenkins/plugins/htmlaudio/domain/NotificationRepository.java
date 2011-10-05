package jenkins.plugins.htmlaudio.domain;

import java.util.Iterator;
import java.util.List;


/**
 * Repository for {@link Notification}s.
 * 
 * @author Lars Hvile
 */
public interface NotificationRepository {
    
    /**
     * Returns the id of the newest {@link Notification} or {@code null} if the repository is empty.
     */
    NotificationId getLastNotificationId();
    
    /**
     * Returns each {@link Notification} that is newer than, i.e. happened after, a provided notification.
     * 
     * @param id a {@link NotificationId} or {@code null} to list each notification
     */
    List<Notification> findNewerThan(NotificationId id);
    
    /**
     * Provides access to an {@link Iterator} that can be used to remove individual notifications.
     */
    void remove(NotificationRemover remover);
    
    interface NotificationRemover {
        void remove(Iterator<Notification> notifications);
    }
}
