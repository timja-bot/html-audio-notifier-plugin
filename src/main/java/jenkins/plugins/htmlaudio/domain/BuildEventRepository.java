package jenkins.plugins.htmlaudio.domain;

import java.util.Collection;

import jenkins.plugins.htmlaudio.domain.impl.VolatileBuildEventRepository;


/**
 * Repository for {@link BuildEvent}s.
 * 
 * @author Lars Hvile
 */
public abstract class BuildEventRepository {
    
    private static final BuildEventRepository instance = new VolatileBuildEventRepository();
    
    public static BuildEventRepository instance() {
        return instance;
    }
    
    /**
     * Adds a new {@link BuildEvent}.
     */
    public abstract void add(BuildEvent event);
    
    /**
     * Removes an existing {@link BuildEvent}.
     */
    public abstract void remove(BuildEvent event);
    
    /**
     * Returns each existing {@link BuildEvent}.
     */
    public abstract Collection<BuildEvent> list();
    
    /**
     * Returns each {@link BuildEvent} that is newer than, i.e. happened after, a provided event.
     */
    public abstract Collection<BuildEvent> findNewerThan(long buildEventId);
    
    /**
     * Returns the id of the newest {@link BuildEvent} or {@code null} if the repository is empty.
     */
    public abstract Long getLastEventId();
    
}
