package jenkins.plugins.htmlaudio.interfaces;

import jenkins.plugins.htmlaudio.app.NotificationService;
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
@SuppressWarnings("rawtypes")
public final class RunResultListener extends RunListener<Run> {
    
    private NotificationService notificationService;
    
    
    public void setNotificationService(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    
    @Override
    public void onCompleted(Run run, TaskListener listener) {
        final Run previousBuild = run.getPreviousBuild();
        
        notificationService.recordBuildCompletion(
            run.getFullDisplayName(),
            run.getResult(),
            previousBuild == null
                ? null
                : previousBuild.getResult());
    }
}
