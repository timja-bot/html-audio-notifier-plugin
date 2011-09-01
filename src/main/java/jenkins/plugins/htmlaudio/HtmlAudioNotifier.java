package jenkins.plugins.htmlaudio;

import jenkins.model.Jenkins;
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
    
    
    @JavaScriptMethod
    public boolean isEnabledByDefault() {
        return getDescriptor().isEnabledByDefault();
    }
    
    
    @JavaScriptMethod
    public String wazzup() {
        // TODO check what's up =)
        return getDescriptor().getFailureSoundUrl();
    }
    
    
    public PluginDescriptor getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(PluginDescriptor.class);
    }
    
    
    @Extension // TODO extract?
    public static final class PluginDescriptor extends Descriptor<HtmlAudioNotifier> {
        
        private boolean enabledByDefault;
        private String failureSoundUrl;
        
        
        public PluginDescriptor() {
            // TODO set defaults?
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
