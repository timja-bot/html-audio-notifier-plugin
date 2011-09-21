package jenkins.plugins.htmlaudio.app;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;
import jenkins.plugins.htmlaudio.domain.BuildResult;
import hudson.Extension;
import hudson.model.Run;
import hudson.model.Result;
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
        final Collection<BuildEvent> events = generateEvents(run.getResult());
        storeEvents(events);
        logger.fine("generated " + events.size() + " BuildEvent(s) based on " + run);
    }


    private Collection<BuildEvent> generateEvents(Result runResult) {
        final List<BuildEvent> result = new ArrayList<BuildEvent>();
        
        for (BuildResult r : BuildResult.values()) {
            if (r.correspondsTo(runResult)) {
                result.add(new BuildEvent(r));
            }
        }
        
        return result;
    }
    
    
    private void storeEvents(Collection<BuildEvent> events) {
        for (BuildEvent e : events) {
            repository.add(e);
        }
    }
}
