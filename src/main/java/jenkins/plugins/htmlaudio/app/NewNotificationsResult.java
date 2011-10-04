package jenkins.plugins.htmlaudio.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationId;


/**
 * The result of a query for new notifications.
 * 
 * @author Lars Hvile
 */
public final class NewNotificationsResult {
    
    private final NotificationId lastNotificationId;
    private final List<Notification> notifications;
    
    
    public NewNotificationsResult(NotificationId lastNotificationId,
            Collection<Notification> notifications) {
        this.lastNotificationId = lastNotificationId;
        this.notifications = new ArrayList<Notification>(notifications);
    }
    
    
    /**
     * Returns the id of the last registered notification. This value should be used as the input for
     * the next query to find notifications.
     */
    public NotificationId getLastNotificationId() {
        return lastNotificationId;
    }
    
    
    /**
     * Returns the notifications that have occurred since the last query.
     */
    public List<Notification> getNotifications() {
        return notifications;
    }
}
