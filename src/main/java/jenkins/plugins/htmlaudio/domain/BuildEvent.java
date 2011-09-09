package jenkins.plugins.htmlaudio.domain;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;


/**
 * A uniquely identified build event.
 * 
 * @author Lars Hvile
 */
public final class BuildEvent {
    
    private static final AtomicLong idSequence = new AtomicLong();
    
    private final long id;
    private final BuildResult result;
    private final long created;
    
    
    public BuildEvent(BuildResult result) {
        this(result, new Date());
    }
    
    
    public BuildEvent(BuildResult result, Date created) {
        this.id = idSequence.incrementAndGet();
        this.result = result;
        this.created = created.getTime();
    }
    
    
    public long getId() {
        return id;
    }
    
    
    public BuildResult getResult() {
        return result;
    }
    
    
    public long getAgeInMs() {
        return System.currentTimeMillis() - created;
    }
    
    
    @Override
    public String toString() {
        return "#" + id + "-" + result;
    }
}
