package support;

import java.util.List;

import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationId;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;


public class NotificationRepositoryAdapter implements NotificationRepository {

    public List<Notification> findNewerThan(NotificationId id) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    public NotificationId getLastNotificationId() {
        throw new UnsupportedOperationException("not implemented");
    }
    
    public void remove(NotificationRemover remover) {
        throw new UnsupportedOperationException("not implemented");
    }
}
