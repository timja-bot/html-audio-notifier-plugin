package jenkins.plugins.htmlaudio.domain.impl;

import java.util.concurrent.TimeUnit;

import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildEventCleanupService;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;


/**
 * @author Lars Hvile
 */
public class DefaultBuildEventCleanupService implements BuildEventCleanupService {
    
    private static final long MAX_AGE_MS = TimeUnit.MINUTES.toMillis(1);
    
    
    public void removeExpiredEvents(BuildEventRepository repo) {
        synchronized(repo) {
            for (BuildEvent e : repo.list()) {
                if (tooOld(e)) {
                    repo.remove(e);
                }
            }
        }
    }
    
    
    private boolean tooOld(BuildEvent e) {
        return e.getAgeInMs() >= MAX_AGE_MS;
    }
}
