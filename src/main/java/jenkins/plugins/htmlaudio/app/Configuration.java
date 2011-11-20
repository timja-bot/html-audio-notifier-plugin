package jenkins.plugins.htmlaudio.app;

import jenkins.plugins.htmlaudio.domain.BuildResult;


/**
 * Exposes the plugin-configuration.
 * 
 * @author Lars Hvile
 */
public interface Configuration {
    
    /**
     * Returns {@code true} if the notification-client should be enabled by default.
     */
    boolean isEnabledByDefault();
    
    /**
     * Returns {@code true} if long-polling is enabled. In this mode, client requests are blocked until
     * new events are ready, or until a timeout is reached, resulting in faster feedback.
     */
    boolean isLongPollingEnabled();
    
    /**
     * Returns a configured sound for a provided {@link BuildResult} or {@code null}.
     */
    String getSoundUrl(BuildResult result);

}
