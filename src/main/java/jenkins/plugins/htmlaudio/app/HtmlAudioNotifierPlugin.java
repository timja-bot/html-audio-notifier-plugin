package jenkins.plugins.htmlaudio.app;

import jenkins.model.Jenkins;
import jenkins.plugins.htmlaudio.app.core.Configuration;
import jenkins.plugins.htmlaudio.app.core.ServerUrlResolver;
import jenkins.plugins.htmlaudio.domain.BuildEventCleanupService;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Describable;
import hudson.model.Descriptor;


/**
 * 'Main' class of the plugin. Handles the configuration & acts as a DI-container for the other components.
 * 
 * @author Lars Hvile
 */
public final class HtmlAudioNotifierPlugin extends Plugin implements Describable<HtmlAudioNotifierPlugin> {
    
    @Override
    public void postInitialize() {
        initializeController();
    }
    
    
    private void initializeController() {
        final Controller c = Jenkins.getInstance().getExtensionList(Controller.class).get(0);
        c.setRepository(BuildEventRepository.instance());
        c.setCleanupService(BuildEventCleanupService.instance());
        c.setConfiguration(getDescriptor());
        
        c.setServerUrlResolver(new ServerUrlResolver() {
            public String getRootUrl() {
                return Jenkins.getInstance().getRootUrl();
            }
        });
    }


    public PluginDescriptor getDescriptor() {
        return Jenkins.getInstance().getDescriptorByType(PluginDescriptor.class);
    }
    
    
    @Extension
    public static final class PluginDescriptor extends Descriptor<HtmlAudioNotifierPlugin>
            implements Configuration {
        
        private volatile boolean enabledByDefault;
        private volatile String failureSoundUrl;
        
        
        public PluginDescriptor() {
            this.enabledByDefault = false;
            this.failureSoundUrl = "horse.wav";
            load();
        }
        
        
        @Override
        public String getDisplayName() {
            return "HTML audio notifications";
        }
        
        
        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            enabledByDefault = json.getBoolean("htmlAudioEnabledByDefault");
            failureSoundUrl = json.getString("htmlAudioFailureSoundUrl");
            
            if (StringUtils.isBlank(failureSoundUrl)) {
                failureSoundUrl = null;
            }
            
            save();
            return super.configure(req, json);
        }
        
        
        public boolean isEnabledByDefault() {
            return enabledByDefault;
        }
        
        
        public void setEnabledByDefault(boolean enabledByDefault) {
            this.enabledByDefault = enabledByDefault;
        }
        
        
        public String getFailureSoundUrl() {
            return failureSoundUrl;
        }
        
        
        public void setFailureSoundUrl(String failureSoundUrl) {
            this.failureSoundUrl = failureSoundUrl;
        }
    }
}
