package support;

import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationId;


public class NotificationAdapter implements Notification {

    public NotificationId getId() {
        throw new UnsupportedOperationException("not implemented");
    }

    public String getSoundUrl() {
        throw new UnsupportedOperationException("not implemented");
    }

    public long getAgeInMs() {
        throw new UnsupportedOperationException("not implemented");
    }
}
