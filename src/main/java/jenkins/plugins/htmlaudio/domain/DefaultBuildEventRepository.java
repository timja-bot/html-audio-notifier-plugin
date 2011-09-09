package jenkins.plugins.htmlaudio.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * @author Lars Hvile
 */
public final class DefaultBuildEventRepository implements BuildEventRepository {
    
    private final Object lock = new Object();
    private final List<Long> index = new ArrayList<Long>();
    private final List<BuildEvent> events = new ArrayList<BuildEvent>();
    
    
    public void add(BuildEvent event) {
        synchronized (lock) {
            final int position = insertToIndex(event.getId());
            events.add(position, event);
        }
    }
    
    
    private int insertToIndex(long id) {
        index.add(id);
        Collections.sort(index);
        return index.indexOf(id);
    }
    
    
    public Collection<BuildEvent> list() {
        synchronized (lock) {
            return safeCopy(events);
        }
    }
    
    
    private static <E> List<E> safeCopy(List<E> source) {
        return new ArrayList<E>(source);
    }
    
    
    public Collection<BuildEvent> findNewerThan(long buildEventId) {
        synchronized (lock) {
            return safeCopy(events.subList(
                from(buildEventId),
                to()));
        }
    }
    
    
    private int from(long id) {
        return Math.abs(binarySearchIndex(id) + 1);
    }
    
    
    private int binarySearchIndex(long id) {
        return Collections.binarySearch(index, id);
    }
    
    
    private int to() {
        return index.size();
    }
    
    
    public void removeOlderThan(long maxAgeMs) {
        synchronized (lock) {
            final Iterator<BuildEvent> events = this.events.iterator();
            
            while (events.hasNext()) {
                final BuildEvent e = events.next(); 
                if (e.getAgeInMs() >= maxAgeMs) {
                    events.remove();
                    index.remove(e.getId());
                }
            }
        }
    }
}
