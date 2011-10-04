package jenkins.plugins.htmlaudio.interfaces;

import java.util.logging.Logger;

import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;
import jenkins.plugins.htmlaudio.domain.BuildResult;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;


/**
 * Collects the the completion-results of {@link Run}s at a global level.
 *  
 * @author Lars Hvile
 */
@Extension
public final class RunResultListener extends RunListener<Run<?, ?>> {
    
    private static final Logger logger = Logger.getLogger(RunResultListener.class.getName());
    
    private NotificationRepository repository;
    
    
    public void setRepository(NotificationRepository repository) {
        this.repository = repository;
    }
    
    
    @Override
    public void onCompleted(Run<?, ?> run, TaskListener listener) {
        final BuildResult br = BuildResult.toBuildResult(run.getResult());
        
        if (br == null) {
            return;
        }
        
        final Notification event = new Notification(br); // TODO should possibly be synchronized so we're guaranteed that they're inserted in the correct order?? another option is that the repo injects the ID at insertion-time 
        repository.add(event);
        // TODO oh.. and this stuff should be moved to NotificationService
        
        logger.info("generated audio-notification " + event + " based on " + run);
    }
}
