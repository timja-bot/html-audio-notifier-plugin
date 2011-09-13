package jenkins.plugins.htmlaudio.domain.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;


/**
 * Simple in-memory implementation of {@link BuildEventRepository}.
 * 
 * @author Lars Hvile
 */
public final class VolatileBuildEventRepository extends BuildEventRepository {
   
    private final Object mutex = new Object();
    private final List<Long> index = new ArrayList<Long>();
    private final List<BuildEvent> events = new ArrayList<BuildEvent>();
    
    
    public void add(BuildEvent event) {
        synchronized (mutex) {
            if (contains(event)) {
                throw new IllegalArgumentException(event + " already exists");
            }
            
            final int position = insertToIndex(event.getId());
            events.add(position, event);
        }
    }
    
    
    private boolean contains(BuildEvent event) {
        return binarySearchIndex(event.getId()) >= 0;
    }
    
    
    private int binarySearchIndex(long id) {
        return Collections.binarySearch(index, id);
    }
    
    
    private int insertToIndex(long id) {
        index.add(id);
        Collections.sort(index);
        return binarySearchIndex(id);
    }
    
    
    public void remove(BuildEvent event) {
        synchronized (mutex) {
            final int pos = binarySearchIndex(event.getId());
            
            if (pos < 0) {
                throw new IllegalArgumentException(event + " not found");
            }
            
            index.remove(pos);
            events.remove(pos);
        }
    }
    
    
    public Collection<BuildEvent> list() {
        synchronized (mutex) {
            return safeCopy(events);
        }
    }
    
    
    private static <E> List<E> safeCopy(List<E> source) {
        return new ArrayList<E>(source);
    }
    
    
    public Collection<BuildEvent> findNewerThan(long buildEventId) {
        synchronized (mutex) {
            return safeCopy(events.subList(
                from(buildEventId),
                to()));
        }
    }
    
    
    private int from(long id) {
        return Math.abs(binarySearchIndex(id) + 1);
    }
    
    
    private int to() {
        return index.size();
    }
    
    
    @Override
    public Long getLastEventId() {
        synchronized (mutex) {
            return index.isEmpty()
                ? null
                : index.get(index.size() - 1);
        }
    }
}
