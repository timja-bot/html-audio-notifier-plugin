package jenkins.plugins.htmlaudio.app.impl;

import hudson.Extension;

import java.util.List;

import jenkins.plugins.htmlaudio.app.NewNotificationsResult;
import jenkins.plugins.htmlaudio.app.NotificationService;
import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationCleanupService;
import jenkins.plugins.htmlaudio.domain.NotificationId;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;


/**
 * @author Lars Hvile
 */
@Extension
public final class DefaultNotificationService implements NotificationService {
    
    private NotificationRepository repo;
    private NotificationCleanupService cleanupService;
    
    
    public void setNotificationRepository(NotificationRepository repository) {
        this.repo = repository;
    }
    
    
    public void setNotificationCleanupService(NotificationCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }
    
    
    public NewNotificationsResult findNewNotifications(NotificationId previous) {
        removeExpiredNotifications(); // TODO doesn't really belong here..
        
        final NotificationId lastNotificationIdBeforeQuery = repo.getLastNotificationId();
        final List<Notification> notifications = repo.findNewerThan(previous);
        
        return new NewNotificationsResult(
                findLastNotficationId(lastNotificationIdBeforeQuery, notifications),
                notifications);
    }


    private NotificationId findLastNotficationId(NotificationId lastNotificationIdBeforeQuery,
            List<Notification> notifications) {
        return notifications.isEmpty()
            ? lastNotificationIdBeforeQuery
            : notifications.get(notifications.size() - 1).getId();
    }
    
    
    private void removeExpiredNotifications() {
        cleanupService.removeExpired(repo);
    }
}
