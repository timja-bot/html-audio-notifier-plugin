package jenkins.plugins.htmlaudio.app.core;


/**
 * Exposes the plugin-configuration.
 * 
 * @author Lars Hvile
 */
public interface Configuration {
    
    boolean isEnabledByDefault();
    
    String getFailureSoundUrl();

}
