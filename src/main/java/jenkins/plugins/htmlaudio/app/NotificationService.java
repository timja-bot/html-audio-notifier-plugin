package jenkins.plugins.htmlaudio.app;

import hudson.model.Result;


/**
 * @author Lars Hvile
 */
public interface NotificationService {
    
    /**
     * TODO
     */
    // TODO next(Long previous) : Tuple<currentId, Notification>
    
    /**
     * TODO
     */
    void registerResult(Result result); // TODO RunResultListener can also be moved over to .interfaces?
        // TODO should we perhaps register Notifications directly? Configuration / SoundUrlResolver could be a domain interface
        // implemented in .application using the standard PluginDescriptor
    
        // no need to have BuildEvent anymore, Notifications only??
    
}
