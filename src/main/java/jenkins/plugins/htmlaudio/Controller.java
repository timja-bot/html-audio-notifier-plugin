package jenkins.plugins.htmlaudio;

import hudson.Extension;
import hudson.model.RootAction;

import java.io.IOException;
import java.util.Collection;

import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildEventCleanupService;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;
import jenkins.plugins.htmlaudio.domain.BuildResult;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
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
    
    private static final String PLUGIN_URL = "plugin/html-audio-notifier/";
    private static final String CONTROLLER_URL = "/html-audio";

    private String rootUrl;
    private BuildEventRepository repository;
    private BuildEventCleanupService cleanupService;
    private Configuration configuration;
    
    
    public void setRootUrl(String rootUrl) {
        this.rootUrl = rootUrl;
    }
    
    
    public void setRepository(BuildEventRepository repository) {
        this.repository = repository;
    }
    
    
    public void setCleanupService(BuildEventCleanupService cleanupService) {
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
            next(req.getParameter("previous")));
    }
    
    
    /*
     * Package-private for testing.
     */
    JSONObject next(String previous) {
        removeExpiredEvents();
        
        return new JSONObject()
            .element("currentNotification", getCurrentNotificationId())
            .element("notifications", createNotificationsArray(findEvents(previous)));
    }
    
    
    private void removeExpiredEvents() {
        cleanupService.removeExpiredEvents(repository);
    }
    
    
    private Collection<BuildEvent> findEvents(String previous) {
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
        if (id == null) {
            return null;
        }
        
        try {
            return Long.valueOf(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    
    private JSONArray createNotificationsArray(Collection<BuildEvent> events) {
        final JSONArray result = new JSONArray();
        
        for (BuildEvent e : events) {
            final String url = getSoundUrl(e.getResult());
            
            if (!StringUtils.isBlank(url)) {
                result.element(toAbsoluteUrl(url));
            }
        }
        
        return result;
    }
    
    
    private String getSoundUrl(BuildResult result) {
        switch (result) {
            case FAILURE:
                return configuration.getFailureSoundUrl();
            default:
                return null;
        }
    }
    
    
    private String toAbsoluteUrl(String url) {
        return isAbsolute(url)
            ? url
            : convertToAbsoluteUrl(url);
    }


    private boolean isAbsolute(String url) {
        return url.contains("://");
    }
    
    
    private String convertToAbsoluteUrl(String url) {
        return (rootUrl.endsWith("/") ? rootUrl : rootUrl + "/")
            + PLUGIN_URL
            + url;
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
