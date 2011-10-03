package jenkins.plugins.htmlaudio.domain.impl;

import java.util.concurrent.TimeUnit;

import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationCleanupService;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;


/**
 * @author Lars Hvile
 */
public class DefaultNotificationCleanupService implements NotificationCleanupService {
    
    private static final long MAX_AGE_MS = TimeUnit.MINUTES.toMillis(1);
    
    
    public void removeExpired(NotificationRepository repo) {
        
        // TODO kind of a design-flaw that we need external locking here isn't it??
        synchronized(repo) { // TODO safe?
            for (Notification n : repo.list()) {
                if (tooOld(n)) {
                    repo.remove(n);
                }
            }
        }
    }
    
    
    private boolean tooOld(Notification n) {
        return n.getAgeInMs() >= MAX_AGE_MS;
    }
}
