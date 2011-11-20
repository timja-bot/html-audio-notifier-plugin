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
     * notification, or waits for new ones to arrive.
     * 
     * @param previous the last encountered {@link NotificationId} or {@code null} to list all each one
     * @param timeoutMs the number of milliseconds to wait for new notifications before returning an empty
     *  result
     * @return a {@link NewNotificationsResult}
     */
    NewNotificationsResult waitForNewNotifications(NotificationId previous, long timeoutMs);
    
    /**
     * Records the completion of a build, which may turn out to generate a notification.
     * 
     * @param buildDetails a simple string-representation of the build that completed
     * @param result the outcome of the build
     * @param previousResult the outcome of the previous build or {@code null}
     */
    void recordBuildCompletion(String buildDetails, Result result, Result previousResult);
    
}
