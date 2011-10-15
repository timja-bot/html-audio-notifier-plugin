package jenkins.plugins.htmlaudio.app.impl;

import static jenkins.plugins.htmlaudio.domain.BuildResult.toBuildResult;
import hudson.Extension;
import hudson.model.Result;

import java.util.List;
import java.util.logging.Logger;

import jenkins.plugins.htmlaudio.app.Configuration;
import jenkins.plugins.htmlaudio.app.NewNotificationsResult;
import jenkins.plugins.htmlaudio.app.NotificationService;
import jenkins.plugins.htmlaudio.domain.BuildResult;
import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationCleanupService;
import jenkins.plugins.htmlaudio.domain.NotificationFactory;
import jenkins.plugins.htmlaudio.domain.NotificationId;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;


/**
 * @author Lars Hvile
 */
@Extension
public final class DefaultNotificationService implements NotificationService {
    
    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());
    
    private NotificationRepository repo;
    private NotificationFactory factory;
    private Configuration configuration;
    private NotificationCleanupService cleanupService;
    
    
    public void setNotificationRepository(NotificationRepository repository) {
        this.repo = repository;
    }
    
    
    public void setNotificationFactory(NotificationFactory factory) {
        this.factory = factory;
    }
    
    
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
    
    public void setNotificationCleanupService(NotificationCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }
    
    
    public NewNotificationsResult findNewNotifications(NotificationId previous) {
        cleanupService.removeExpired();
        
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
    
    
    public void recordBuildCompletion(String buildDetails, Result result, Result previousResult) {
        cleanupService.removeExpired();
        
        final String soundUrl = getSoundForResult(result, previousResult);
        if (soundUrl == null) {
            return;
        }
        
        final Notification n = factory.createAndPersist(soundUrl, buildDetails);
        logger.info("created a new notification, " + n);
    }
    
    
    private String getSoundForResult(Result result, Result previousResult) {
        final BuildResult br = toBuildResult(result, previousResult);
        return br == null
            ? null
            : configuration.getSoundUrl(br);
    }
}
