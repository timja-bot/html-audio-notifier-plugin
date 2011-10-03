package support;

import java.util.Collection;

import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationId;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;


public class NotificationRepositoryAdapter implements NotificationRepository {

    public void remove(Notification event) {
        throw new UnsupportedOperationException("not implemented");
    }

    public Collection<Notification> list() {
        throw new UnsupportedOperationException("not implemented");
    }
    
    public Collection<Notification> findNewerThan(NotificationId id) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    public NotificationId getLastNotificationId() {
        throw new UnsupportedOperationException("not implemented");
    }
}
