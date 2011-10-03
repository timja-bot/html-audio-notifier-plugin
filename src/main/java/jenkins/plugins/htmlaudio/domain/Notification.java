package jenkins.plugins.htmlaudio.domain;


/**
 * A notification to a client, based on the outcome of a build.
 * 
 * @author Lars Hvile
 */
public interface Notification {
    
    /**
     * A unique id for the notification.
     */
    NotificationId getId();
    
    /**
     * The URL of a sound to play in the browser.
     */
    String getSoundUrl();
    
    /**
     * The time elapsed in milliseconds since the notification was created.
     */
    long getAgeInMs();
    
}
