package support;

import java.util.Date;

import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildResult;


public final class DomainObjectFactory {
    
    public static BuildEvent event() {
        return new BuildEvent(BuildResult.FAILURE);
    }
    
    
    public static BuildEvent event(long created) {
        return event(new Date(created));
    }
    
    
    public static BuildEvent event(Date created) {
        return new BuildEvent(BuildResult.FAILURE, created);
    }
    
    
    private DomainObjectFactory() {
    }
}
