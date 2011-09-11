package jenkins.plugins.htmlaudio.domain;

import jenkins.plugins.htmlaudio.domain.impl.DefaultBuildEventCleanupService;


/**
 * Keeps the {@link BuildEventRepository} clean & fresh by removing expired events.
 * 
 * @author Lars Hvile
 */
public abstract class BuildEventCleanupService {
    
    private static final BuildEventCleanupService instance = new DefaultBuildEventCleanupService();
    
    public static BuildEventCleanupService instance() {
        return instance;
    }
    
    public abstract void removeExpiredEvents(BuildEventRepository repository);

}
