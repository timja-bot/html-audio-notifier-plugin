package jenkins.plugins.htmlaudio.app;

import hudson.model.Result;
import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationId;


/**
 * @author Lars Hvile
 */
public interface NotificationService {
    
    /**
     * Returns every {@link Notification} that has been registered after the previously encountered
     * notification.
     * 
     * @param previous the last encountered {@link NotificationId} or {@code null} to list all each one
     * @return a {@link NewNotificationsResult}
     */
    NewNotificationsResult findNewNotifications(NotificationId previous);
    
    /**
     * Records the completion of a build, which may turn out to generate a notification.
     * 
     * @param buildDetails a simple string-representation of the build that completed
     * @param result the build-outcome
     */
    void recordBuildCompletion(String buildDetails, Result result);
    
}
