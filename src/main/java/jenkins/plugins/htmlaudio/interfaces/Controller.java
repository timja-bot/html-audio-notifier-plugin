package jenkins.plugins.htmlaudio.interfaces;

import hudson.Extension;
import hudson.model.RootAction;

import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

import jenkins.plugins.htmlaudio.app.Configuration;
import jenkins.plugins.htmlaudio.app.util.ServerUrlResolver;
import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationCleanupService;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;

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
    
    private static final String PLUGIN_SOUNDS_URL = "plugin/html-audio-notifier/sounds/";
    private static final String CONTROLLER_URL = "/html-audio";
    
    private static final Logger logger = Logger.getLogger(Controller.class.getName());

    private ServerUrlResolver serverUrlResolver;
    private NotificationRepository repository;
    private NotificationCleanupService cleanupService;
    private Configuration configuration;
    
    
    public void setServerUrlResolver(ServerUrlResolver serverUrlResolver) {
        this.serverUrlResolver = serverUrlResolver;
    }
    
    
    public void setRepository(NotificationRepository repository) {
        this.repository = repository;
    }
    
    
    public void setCleanupService(NotificationCleanupService cleanupService) {
        this.cleanupService = cleanupService;
    }
    
    
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
    
    
    /**
     * Returns a simple true/false indicating whether or not the client should be enabled by default.
     */
    public void doIsEnabledByDefault(StaplerRequest req, StaplerResponse resp) throws IOException {
        writeJsonResponse(resp,
            isEnabledByDefault());
    }
    
    
    /*
     * Package-private for testing.
     */
    JSONObject isEnabledByDefault() {
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
            next(req.getRemoteAddr(), req.getParameter("previous")));
    }
    
    
    /*
     * Package-private for testing.
     */
    JSONObject next(String client, String previous) {
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
    
    
    /**
     * Converts an URL to an absolute URL if necessary. Used by the configuration-page for testing
     * sounds in the browser.
     */
    public void doToAbsoluteUrl(StaplerRequest req, StaplerResponse resp) throws IOException {
        resp.setContentType("text/plain");
        resp.getWriter().print(toAbsoluteUrl(req.getParameter("url")));
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
