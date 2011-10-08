package jenkins.plugins.htmlaudio.app;

import jenkins.model.Jenkins;
import jenkins.plugins.htmlaudio.app.impl.DefaultNotificationService;
import jenkins.plugins.htmlaudio.app.impl.PluginConfiguration;
import jenkins.plugins.htmlaudio.domain.impl.DefaultNotificationCleanupService;
import jenkins.plugins.htmlaudio.domain.impl.VolatileNotificationRepositoryAndFactory;
import jenkins.plugins.htmlaudio.interfaces.Controller;
import jenkins.plugins.htmlaudio.interfaces.RunResultListener;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

import hudson.Extension;
import hudson.Plugin;
import hudson.init.InitMilestone;
import hudson.init.Initializer;
import hudson.model.Describable;
import hudson.model.Descriptor;


/**
 * 'Main' class of the plugin. Handles the configuration & acts as a DI-container for the other components.
 * 
 * @author Lars Hvile
 */
public final class HtmlAudioNotifierPlugin extends Plugin implements Describable<HtmlAudioNotifierPlugin> {
    
    /**
     * Initializes the other components in the plugin by injecting their required dependencies.
     * To get access to the other components, this must happen in, or after,
     * {@link InitMilestone#PLUGINS_PREPARED}. Using {@link #postInitialize()} is not an option since it may
     * be called in {@link InitMilestone#PLUGINS_LISTED}.
     */
    @Initializer(after=InitMilestone.PLUGINS_PREPARED)
    public static void initializePlugin() {
        final VolatileNotificationRepositoryAndFactory notificationRepoAndFactory
            = new VolatileNotificationRepositoryAndFactory();

        final DefaultNotificationCleanupService notificationCleanupService
            = new DefaultNotificationCleanupService();
        
        notificationCleanupService.setNotificationRepository(notificationRepoAndFactory);
        
        final HtmlAudioNotifierPlugin plugin = Jenkins.getInstance().getPlugin(HtmlAudioNotifierPlugin.class);
        final PluginConfiguration configuration = getComponent(PluginConfiguration.class);
        final DefaultNotificationService notificationService = getComponent(DefaultNotificationService.class);
        final Controller controller = getComponent(Controller.class);
        final RunResultListener listener = getComponent(RunResultListener.class);
        
        configuration.setPluginDescriptor(plugin.getDescriptor());

        notificationService.setNotificationRepository(notificationRepoAndFactory);
        notificationService.setNotificationFactory(notificationRepoAndFactory);
        notificationService.setConfiguration(configuration);
        notificationService.setNotificationCleanupService(notificationCleanupService);
        
        controller.setConfiguration(configuration);
        controller.setNotificationService(notificationService);
        
        listener.setNotificationService(notificationService);
    }
    
    
    private static <T> T getComponent(Class<T> type) {
        return Jenkins.getInstance().getExtensionList(type).get(0);
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
            successSoundUrl = json.getString("htmlAudioSuccessSoundUrl");
            failureSoundUrl = json.getString("htmlAudioFailureSoundUrl");
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
}
