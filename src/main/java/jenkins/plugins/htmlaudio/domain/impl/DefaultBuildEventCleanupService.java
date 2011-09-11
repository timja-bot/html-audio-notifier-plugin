package jenkins.plugins.htmlaudio.domain.impl;

import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildEventCleanupService;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;


/**
 * @author Lars Hvile
 */
public class DefaultBuildEventCleanupService implements BuildEventCleanupService {
    
    private final BuildEventRepository repo;
    private final long maxAgeMs;
    
    
    public DefaultBuildEventCleanupService(BuildEventRepository repo, long maxAgeMs) {
        this.repo = repo;
        this.maxAgeMs = maxAgeMs;
    }
    
    
    public void removeExpiredEvents() {
        for (BuildEvent e : repo.list()) {
            if (tooOld(e, maxAgeMs)) {
                repo.remove(e);
            }
        }
    }


    private boolean tooOld(BuildEvent e, long limit) {
        return e.getAgeInMs() >= limit;
    }
}
