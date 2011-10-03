package jenkins.plugins.htmlaudio.domain.impl;

import java.util.Date;

import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationId;


/**
 * @author Lars Hvile
 */
public final class SimpleNotification implements Notification {

    private final NotificationId id;
    private final String soundUrl;
    private final String buildDetails;
    private final Date created = new Date(); 
    
    
    public SimpleNotification(NotificationId id, String soundUrl, String buildDetails) {
        this.id = id;
        this.soundUrl = soundUrl;
        this.buildDetails = buildDetails;
    }
    
    
    public NotificationId getId() {
        return id;
    }
    
    
    public final String getSoundUrl() {
        return soundUrl;
    }
    
    
    public final long getAgeInMs() {
        return System.currentTimeMillis() - created.getTime();
    }
    
    
    @Override
    public final String toString() {
        return "#" + getId() + ", build: " + buildDetails + ", sound: " + soundUrl
            + ", created: " + created;
    }
}
