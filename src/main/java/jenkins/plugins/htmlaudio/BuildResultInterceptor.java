package jenkins.plugins.htmlaudio;

import hudson.Extension;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

/**
 * TODO
 *  
 * @author Lars Hvile
 */
@Extension
public final class BuildResultInterceptor extends RunListener<Run<?, ?>> {
    
    @Override
    public void onCompleted(Run<?, ?> r, TaskListener listener) {

        // TODO do something useful...
        System.out.println("> " + r.getDisplayName() + " / " + r.getResult());
    }
}