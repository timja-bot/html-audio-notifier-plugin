package jenkins.plugins.htmlaudio;

import jenkins.plugins.htmlaudio.domain.BuildResult;
import hudson.Extension;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;


/**
 * Collects the the completion-results of {@link Run}s at a global level.
 *  
 * @author Lars Hvile
 */
@Extension
public final class BuildResultListener extends RunListener<Run<?, ?>> {
    
    // TODO?  private static final Logger LOGGER = Logger.getLogger(GlobalBuildStatsPlugin.class.getName());
    
    
    @Override
    public void onCompleted(Run<?, ?> r, TaskListener listener) {
        
        for (BuildResult result : BuildResult.values()) {
            if (result.correspondsTo(r.getResult())) {
                // TODO register
            }
        }

        // TODO do something useful...
        System.out.println("> #" + r.number + ": " + r.getDisplayName() + " / " + r.getResult());
    }
}
