package jenkins.plugins.htmlaudio.app.impl;

import static jenkins.plugins.htmlaudio.util.StringUtils.nullIfEmpty;
import hudson.Extension;
import jenkins.plugins.htmlaudio.app.Configuration;
import jenkins.plugins.htmlaudio.app.HtmlAudioNotifierPlugin.PluginDescriptor;
import jenkins.plugins.htmlaudio.domain.BuildResult;


/**
 * Adapts the plugin-descriptor to the cleaner {@link Configuration}-interface.
 * 
 * @author Lars Hvile
 */
@Extension
public final class PluginConfiguration implements Configuration {
    
    private PluginDescriptor descriptor;
    
    
    public void setPluginDescriptor(PluginDescriptor descriptor) {
        this.descriptor = descriptor;
    }
    
    
    public boolean isEnabledByDefault() {
        return descriptor.isEnabledByDefault();
    }
    
    
    public String getSoundUrl(BuildResult result) {
        return nullIfEmpty(findConfiguredSoundForResult(descriptor, result));
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
