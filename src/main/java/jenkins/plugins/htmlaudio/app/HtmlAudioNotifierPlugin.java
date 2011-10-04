package jenkins.plugins.htmlaudio.app;

import static jenkins.plugins.htmlaudio.util.StringUtils.nullIfEmpty;
import jenkins.model.Jenkins;
import jenkins.plugins.htmlaudio.app.util.ServerUrlResolver;
import jenkins.plugins.htmlaudio.domain.NotificationCleanupService;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;
import jenkins.plugins.htmlaudio.domain.BuildResult;
import jenkins.plugins.htmlaudio.domain.impl.DefaultNotificationCleanupService;
import jenkins.plugins.htmlaudio.domain.impl.VolatileNotificationRepositoryAndFactory;
import net.sf.json.JSONObject;

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
    
    private final NotificationRepository buildEventRepo = new VolatileNotificationRepositoryAndFactory();
    private final NotificationCleanupService cleanupService = new DefaultNotificationCleanupService();
    private final Configuration configuration = new PluginConfiguration();
    
    
    @Override
    public void postInitialize() {
        initializeController();
        initializeRunResultListener();
    }


    private void initializeController() {
        final Controller c = getComponent(Controller.class);
        c.setRepository(buildEventRepo);
        c.setCleanupService(cleanupService);
        c.setConfiguration(configuration);
        
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
    public static final class PluginDescriptor extends Descriptor<HtmlAudioNotifierPlugin> {
        
        private volatile boolean enabledByDefault = false;
        private volatile String successSoundUrl = "pop.wav";
        private volatile String failureSoundUrl = "horse.wav";
        
        
        public PluginDescriptor() {
            load();
        }
        
        
        @Override
        public String getDisplayName() {
            return "HTML audio notifications";
        }
        
        
        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException { 
            enabledByDefault = json.getBoolean("htmlAudioEnabledByDefault");
            successSoundUrl = nullIfEmpty(json.getString("htmlAudioSuccessSoundUrl"));
            failureSoundUrl = nullIfEmpty(json.getString("htmlAudioFailureSoundUrl"));
            save();
            return super.configure(req, json);
        }
        
        
        public boolean isEnabledByDefault() {
            return enabledByDefault;
        }
        
        
        public void setEnabledByDefault(boolean enabledByDefault) {
            this.enabledByDefault = enabledByDefault;
        }
        
        
        public String getSuccessSoundUrl() {
            return successSoundUrl;
        }
        
        
        public void setSuccessSoundUrl(String successSoundUrl) {
            this.successSoundUrl = successSoundUrl;
        }
        
        
        public void setFailureSoundUrl(String failureSoundUrl) {
            this.failureSoundUrl = failureSoundUrl;
        }
        
        
        public String getFailureSoundUrl() {
            return failureSoundUrl;
        }
    }
    
    
    private class PluginConfiguration implements Configuration {

        public boolean isEnabledByDefault() {
            return getDescriptor().isEnabledByDefault();
        }
        
        
        public String getSoundUrl(BuildResult result) {
            return nullIfEmpty(findConfiguredSoundForResult(getDescriptor(), result));
        }
        
        
        private String findConfiguredSoundForResult(PluginDescriptor descriptor, BuildResult result) {
            switch (result) {
                case SUCCESS:
                    return descriptor.getSuccessSoundUrl();
                    
                case FAILURE:
                    return descriptor.getFailureSoundUrl();

                default:
                    throw new IllegalArgumentException("unknown result-type " + result);
            }
        }
    }
}
