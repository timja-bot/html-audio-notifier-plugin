package jenkins.plugins.htmlaudio.domain;


/**
 * Keeps the {@link BuildEventRepository} clean & fresh by removing expired events.
 * 
 * @author Lars Hvile
 */
public interface BuildEventCleanupService {
    
    void removeExpiredEvents(BuildEventRepository repository);

}
