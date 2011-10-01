package jenkins.plugins.htmlaudio.app;

import java.util.logging.Logger;

import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;
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
    
    private BuildEventRepository repository;
    
    
    public void setRepository(BuildEventRepository repository) {
        this.repository = repository;
    }
    
    
    @Override
    public void onCompleted(Run<?, ?> run, TaskListener listener) {
        final BuildResult br = BuildResult.toBuildResult(run.getResult());
        
        if (br == null) {
            return;
        }
        
        final BuildEvent event = new BuildEvent(br);
        repository.add(event);
        
        logger.info("generated audio-notification " + event + " based on " + run);
    }
}
