package jenkins.plugins.htmlaudio;

import java.util.Collection;

import jenkins.model.Jenkins;
import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;
import jenkins.plugins.htmlaudio.domain.BuildResult;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.bind.JavaScriptMethod;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Descriptor;


/**
 * TODO
 * 
 * @author Lars Hvile
 */
public final class HtmlAudioNotifier extends Plugin implements Describable<HtmlAudioNotifier> {
    
    private static final String PLUGIN_URL = "plugin/html-audio-notifier/";
    
    private final BuildEventRepository repository = BuildEventRepository.instance();
    
    
    // TODO pull the remote-access stuff into a dedicated class?
    @JavaScriptMethod
    public boolean isEnabledByDefault() {
        return getDescriptor().isEnabledByDefault();
    }
    
    
    @JavaScriptMethod
    public JSONObject nextSounds(String prevEventId) {
        final Collection<BuildEvent> events = findEvents(prevEventId);
        
        return new JSONObject()
            // TODO attach the 'last' existing event-id, if any?
            .element("sounds", createSoundsArray(events));
    }


    private Collection<BuildEvent> findEvents(String prevEventId) {
        final Long prev = parsePreviousEventId(prevEventId);
        return prev == null
            ? repository.list()
            : repository.findNewerThan(prev);
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
                return createAbsoluteSoundUrl(getDescriptor().getFailureSoundUrl());
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


    public PluginDescriptor getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(PluginDescriptor.class);
    }
    
    
    @Extension // TODO extract?
    public static final class PluginDescriptor extends Descriptor<HtmlAudioNotifier> {
        
        private boolean enabledByDefault;
        private String failureSoundUrl;
        
        
        public PluginDescriptor() {
            this.enabledByDefault = true;
            this.failureSoundUrl = "horse.wav";
            load();
        }
        
        
        @Override
        public String getDisplayName() {
            return "hoppla"; // TODO where is this used?
        }
        
        
        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            enabledByDefault = json.getBoolean("enabledByDefault");
            failureSoundUrl = json.getString("failureSoundUrl");
            save();
            return super.configure(req, json);
        }
        
        
        public boolean isEnabledByDefault() {
            return enabledByDefault;
        }
        
        
        public String getFailureSoundUrl() {
            return failureSoundUrl;
        }
    }
}
