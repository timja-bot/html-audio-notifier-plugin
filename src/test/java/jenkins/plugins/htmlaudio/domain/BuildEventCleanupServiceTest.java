package jenkins.plugins.htmlaudio.domain;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class BuildEventCleanupServiceTest {
    
    private final List<BuildEvent> events = new ArrayList<BuildEvent>();
    private final List<BuildEvent> removed = new ArrayList<BuildEvent>();
    private final BuildEventRepository repo = new MockRepo();
    
    
    @Test
    public void events_of_expected_age_are_removed_from_repository() {
        final long second = TimeUnit.SECONDS.toMillis(1);
        final long base = System.currentTimeMillis();
        
        final BuildEvent e1 = e(base);
        final BuildEvent e2 = e(base - 30 * second);
        final BuildEvent e3 = e(base - 60 * second);
        final BuildEvent e4 = e(base - 120 * second);
        
        events.addAll(Arrays.asList(e1, e2, e3, e4));
        
        // everything older than 1 minute should be removed
        assertEquals(Arrays.asList(e3, e4),
            removeExpired());

        assertEquals(Arrays.asList(e1, e2), events);
    }
    
    
    private BuildEvent e(long created) {
        return new BuildEvent(null, new Date(created));
    }
    
    
    private List<BuildEvent> removeExpired() {
        try {
            BuildEventCleanupService.instance().removeExpiredEvents(repo);
            return new ArrayList<BuildEvent>(removed);
        } finally {
            events.removeAll(removed);
            removed.clear();
        }
    }
    
    
    private class MockRepo extends BuildEventRepository {
        
        public void remove(BuildEvent event) {
            removed.add(event);
        }

        public Collection<BuildEvent> list() {
            return Collections.unmodifiableList(events);
        }

        public void add(BuildEvent event) {
            throw new UnsupportedOperationException();
        }

        public Collection<BuildEvent> findNewerThan(long buildEventId) {
            throw new UnsupportedOperationException();
        }
        
        @Override
        public Long getLastEventId() {
            throw new UnsupportedOperationException();
        }
    }
}
