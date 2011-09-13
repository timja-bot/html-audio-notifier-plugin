package jenkins.plugins.htmlaudio;

import hudson.Extension;
import hudson.model.RootAction;

import java.io.IOException;
import java.util.Collection;

import jenkins.model.Jenkins;
import jenkins.plugins.htmlaudio.HtmlAudioNotifier.PluginDescriptor;
import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;
import jenkins.plugins.htmlaudio.domain.BuildResult;

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
    
    private static final String PLUGIN_URL = "plugin/html-audio-notifier/";
    private static final String CONTROLLER_URL = "/html-audio";
    
    private final BuildEventRepository repository = BuildEventRepository.instance();
    private PluginDescriptor descriptor;
    
    
    public void setDescriptor(PluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
    
    /**
     * Returns a simple true/false indicating whether or not the client should be enabled by default.
     */
    public void doIsEnabledByDefault(StaplerRequest req, StaplerResponse resp) throws IOException {
        writeJsonResponse(resp,
            new JSONObject().element("enabled", descriptor.isEnabledByDefault()));
    }
    
    
    private void writeJsonResponse(StaplerResponse response, JSONObject json) throws IOException {
        response.setContentType(Flavor.JSON.contentType);
        json.write(response.getWriter());
    }
    
    
    /**
     * Handles requests by clients polling for new sounds to play.
     */
    public void doNext(StaplerRequest req, StaplerResponse resp) throws IOException {
        final Collection<BuildEvent> events = findEvents(req.getParameter("previous"));
        final JSONObject result = new JSONObject()
            // TODO attach the 'last' existing event-id, if any?
            .element("sounds", createSoundsArray(events));
        
        writeJsonResponse(resp, result);
    }


    private Collection<BuildEvent> findEvents(String previous) {
        final Long previousEventId = parsePreviousEventId(previous);
        return previousEventId == null
            ? repository.list()
            : repository.findNewerThan(previousEventId);
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
    
    
    private JSONArray createSoundsArray(Collection<BuildEvent> events) {
        final JSONArray result = new JSONArray();
        
        for (BuildEvent e : events) {
            final String url = getSoundUrl(e.getResult());
            if (url != null) {
                result.element(new JSONObject()
                    .element("id", e.getId())
                    .element("src", url));
            }
        }
        
        return result;
    }
    
    
    private String getSoundUrl(BuildResult result) {
        switch (result) {
            case FAILURE:
                return createAbsoluteSoundUrl(descriptor.getFailureSoundUrl());
            default:
                return null;
        }
    }
    
    
    private String createAbsoluteSoundUrl(String url) {
        if (url == null) {
            return null;
        }
        
        return isAbsolute(url)
            ? url
            : convertToAbsoluteUrl(url);
    }


    private boolean isAbsolute(String url) {
        return url.contains("://");
    }
    
    
    private String convertToAbsoluteUrl(String url) {
        final String rootUrl = Jenkins.getInstance().getRootUrl();
        return (rootUrl.endsWith("/") ? rootUrl : rootUrl + "/")
            + PLUGIN_URL
            + url;
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
