package jenkins.plugins.htmlaudio;


import jenkins.model.Jenkins;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Descriptor;


/**
 * TODO
 * 
 * @author Lars Hvile
 */ // TODO rename me, ..Plugin
public final class HtmlAudioNotifier extends Plugin implements Describable<HtmlAudioNotifier> {
    
    @Override
    public void postInitialize() {
        final Controller c = Jenkins.getInstance().getExtensionList(Controller.class).get(0);
        c.setRootUrl(Jenkins.getInstance().getRootUrl());
        c.setRepository(BuildEventRepository.instance());
        c.setConfiguration(getDescriptor());
    }
    
    
    public PluginDescriptor getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(PluginDescriptor.class);
    }
    
    
    @Extension
    public static final class PluginDescriptor extends Descriptor<HtmlAudioNotifier>
            implements Configuration {
        
        private boolean enabledByDefault; // TODO synchronization?
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
