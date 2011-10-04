package jenkins.plugins.htmlaudio.app.impl;

import java.util.Collection;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import hudson.model.Result;
import jenkins.plugins.htmlaudio.app.NewNotificationsResult;
import jenkins.plugins.htmlaudio.app.NotificationService;
import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationCleanupService;
import jenkins.plugins.htmlaudio.domain.NotificationId;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;


/**
 * @author Lars Hvile
 */
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
        // TODO Auto-generated method stub
        return null;
    }
    
    
    /*
     *     JSONObject next(String client, String previous) {
        removeExpiredEvents();
        
        final Collection<Notification> events = findEvents(previous);
        
        if (!events.isEmpty()) {
            logger.info("delivered " + events.size() + " event(s) to " + client
                + ", " + events);
        }
        
        return new JSONObject()
            .element("currentNotification", getCurrentNotificationId()) // TODO only rely on this if the array below is empty (important), but always collect it up front
            .element("notifications", createNotificationsArray(events));
    }
    
    
    private void removeExpiredEvents() {
        cleanupService.removeExpired(repository);
    }
    
    
    private Collection<Notification> findEvents(String previous) {
        final Long previousEventId = parsePreviousEventId(previous);
        return previousEventId == null
            ? repository.list()
            : repository.findNewerThan(previousEventId);
    }
    
    
    private Object getCurrentNotificationId() {
        final Long lastEventId = repository.getLastEventId();
        return lastEventId == null
            ? new JSONObject(true)
            : String.valueOf(lastEventId);
    }
    
    
    private Long parsePreviousEventId(String id) {
        
        // TODO NotificationId.toNotificationId(String) : Long ?
        
        if (id == null) {
            return null;
        }
        
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    
    private JSONArray createNotificationsArray(Collection<Notification> events) {
        final JSONArray result = new JSONArray();
        
        for (Notification e : events) {
            final String url = configuration.getSoundUrl(e.getResult());
            if (url != null) {
                result.element(toAbsoluteUrl(url));
            }
        }
        
        return result;
    }
    
    
    private String toAbsoluteUrl(String url) {
        return isAbsolute(url)
            ? url
            : convertToAbsoluteUrl(url);
    }


    private boolean isAbsolute(String url) {
        return url.contains("://"); // TODO sane? possible to do this from javascript instead?
    }
    
    
    private String convertToAbsoluteUrl(String relativeUrl) {
        return serverUrlResolver.getRootUrl()
                + PLUGIN_SOUNDS_URL + relativeUrl;
    }
     */
    

}
