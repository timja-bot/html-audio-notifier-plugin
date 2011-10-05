package jenkins.plugins.htmlaudio.interfaces;

import hudson.Extension;
import hudson.model.RootAction;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import jenkins.plugins.htmlaudio.app.Configuration;
import jenkins.plugins.htmlaudio.app.NewNotificationsResult;
import jenkins.plugins.htmlaudio.app.NotificationService;
import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationId;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Flavor;


/**
 * Acts as a controller for the javascript/JSON client, which may or may not be total abuse of
 * {@link RootAction} =).
 * 
 * @author Lars Hvile
 */
@Extension
public final class Controller implements RootAction {
    
    private static final String CONTROLLER_URL = "/html-audio";
    
    private static final Logger logger = Logger.getLogger(Controller.class.getName());
    
    private Configuration configuration;
    private NotificationService notificationService;
    
    
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
    
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    
    /**
     * Returns a simple true/false indicating whether or not the client should be enabled by default.
     */
    public void doIsEnabledByDefault(StaplerRequest req, StaplerResponse resp) throws IOException {
        writeJsonResponse(resp,
            isEnabledByDefault());
    }
    
    
    private JSONObject isEnabledByDefault() {
        return new JSONObject()
            .element("enabled", configuration.isEnabledByDefault());
    }
    
    
    private void writeJsonResponse(StaplerResponse response, JSONObject json) throws IOException {
        response.setContentType(Flavor.JSON.contentType);
        json.write(response.getWriter());
    }
    
    
    /**
     * Handles requests by clients polling for new sounds to play.
     */
    public void doNext(StaplerRequest req, StaplerResponse resp) throws IOException {
        writeJsonResponse(resp,
            findNext(req.getRemoteAddr(), req.getParameter("previous")));
    }
    
    
    private JSONObject findNext(String client, String previous) {
        final NewNotificationsResult next = notificationService.findNewNotifications(null); // TODO not null
     
        if (!next.getNotifications().isEmpty()) {
            logger.info("delivering " + next.getNotifications().size() + " event(s) to " + client
                + ", " + next.getNotifications());
        }
        
        return new JSONObject()
            .element("currentNotification", createCurrentNotificationObject(next.getLastNotificationId()))
            .element("notifications", createNotificationsArray(next.getNotifications()));
    }
    
    
    private Object createCurrentNotificationObject(NotificationId lastNotificationId) {
        return lastNotificationId == null
            ? new JSONObject(true)
            : String.valueOf(lastNotificationId.getValue());
    }
    
    
    private JSONArray createNotificationsArray(Collection<Notification> notifications) {
        final JSONArray result = new JSONArray();
        
        for (Notification n : notifications) {
            result.element(n.getSoundUrl());
        }
        
        return result;
    }
    
    
    public String getUrlName() {
        return CONTROLLER_URL;
    }
    
    
    public String getIconFileName() {
        return null;
    }
    
    
    public String getDisplayName() {
        return null;
    }
}
