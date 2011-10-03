package jenkins.plugins.htmlaudio.domain;


/**
 * @author Lars Hvile
 */
public interface NotificationFactory {
    
    /**
     * Creates a {@link Notification} and makes it instantly available in the {@link NotificationRepository}.
     * 
     * @param soundUrl URL of a sound that should be played
     * @param buildDetails details about the build that caused the notification, used for logging
     * @throws IllegalArgumentException if {@code soundUrl} is {@code null} 
     */
    Notification createAndPersist(String soundUrl, String buildDetails);

}
