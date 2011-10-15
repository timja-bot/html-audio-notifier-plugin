package jenkins.plugins.htmlaudio.domain.impl;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationCleanupService;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;
import jenkins.plugins.htmlaudio.domain.NotificationRepository.NotificationRemover;


/**
 * @author Lars Hvile
 */
public class DefaultNotificationCleanupService implements NotificationCleanupService {
    
    private static final long MAX_AGE_MS = TimeUnit.MINUTES.toMillis(1);
    
    private NotificationRepository repo;
    
    
    public void setNotificationRepository(NotificationRepository repo) {
        this.repo = repo;
    }
    
    
    public void removeExpired() {
        
        repo.remove(new NotificationRemover() {
            public void remove(Iterator<Notification> notifications) {
                
                while (notifications.hasNext()) {
                    if (tooOld(notifications.next())) {
                        notifications.remove();
                    } else {
                        break;
                    }
                }
            }
        });
    }
    
    
    private boolean tooOld(Notification n) {
        return n.getAgeInMs() >= MAX_AGE_MS;
    }
}
