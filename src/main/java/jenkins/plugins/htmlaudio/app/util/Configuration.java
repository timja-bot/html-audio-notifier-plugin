package jenkins.plugins.htmlaudio.app.util;


/**
 * Exposes the plugin-configuration.
 * 
 * @author Lars Hvile
 */
public interface Configuration {
    
    boolean isEnabledByDefault();
    
    String getFailureSoundUrl();

}
