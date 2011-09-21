package jenkins.plugins.htmlaudio.app;

import jenkins.model.Jenkins;
import jenkins.plugins.htmlaudio.app.util.Configuration;
import jenkins.plugins.htmlaudio.app.util.ServerUrlResolver;
import jenkins.plugins.htmlaudio.domain.BuildEventCleanupService;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;
import jenkins.plugins.htmlaudio.domain.impl.DefaultBuildEventCleanupService;
import jenkins.plugins.htmlaudio.domain.impl.VolatileBuildEventRepository;
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
    
    private final BuildEventRepository buildEventRepo = new VolatileBuildEventRepository();
    private final BuildEventCleanupService cleanupService = new DefaultBuildEventCleanupService();
    
    
    @Override
    public void postInitialize() {
        initializeController();
        initializeRunResultListener();
    }


    private void initializeController() {
        final Controller c = getComponent(Controller.class);
        c.setRepository(buildEventRepo);
        c.setCleanupService(cleanupService);
        c.setConfiguration(getDescriptor());
        
        c.setServerUrlResolver(new ServerUrlResolver() {
            public String getRootUrl() {
                return Jenkins.getInstance().getRootUrl();
            }
        });
    }
    
    
    private <T> T getComponent(Class<T> type) {
        return Jenkins.getInstance().getExtensionList(type).get(0);
    }
    
    
    private void initializeRunResultListener() {
        final RunResultListener r = getComponent(RunResultListener.class);
        r.setRepository(buildEventRepo);
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
