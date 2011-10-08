package jenkins.plugins.htmlaudio.domain;


/**
 * Keeps the {@link NotificationRepository} clean & fresh by removing expired notifications.
 * 
 * @author Lars Hvile
 */
public interface NotificationCleanupService {
    
    void removeExpired();

}
