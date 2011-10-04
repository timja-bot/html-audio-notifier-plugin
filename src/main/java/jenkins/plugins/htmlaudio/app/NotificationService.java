package jenkins.plugins.htmlaudio.app;

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
     * TODO
     */
        // TODO convert Result / Tuple<Result, Result> to a domain-object? implemented in infrastructure.jenkins??
    // void registerResult(Result result); // TODO RunResultListener can also be moved over to .interfaces?
        // TODO should we perhaps register Notifications directly? Configuration / SoundUrlResolver could be a domain interface
        // implemented in .application using the standard PluginDescriptor
    
        // no need to have BuildEvent anymore, Notifications only??
}    
