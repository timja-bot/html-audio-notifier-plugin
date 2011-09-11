package jenkins.plugins.htmlaudio.domain;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jenkins.plugins.htmlaudio.domain.impl.DefaultBuildEventCleanupService;

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
        final long minute = TimeUnit.MINUTES.toMillis(1);
        final long base = System.currentTimeMillis();
        
        final BuildEvent e1 = e(base);
        final BuildEvent e2 = e(base - 30 * second);
        final BuildEvent e3 = e(base - 1 * minute);
        final BuildEvent e4 = e(base - 2 * minute);
        
        events.addAll(Arrays.asList(e1, e2, e3, e4));
        
        // remove all older than 2 minutes
        assertEquals(Arrays.asList(e4),
            removeExpired(2 * minute));
        
        // .. >= 30sec
        assertEquals(Arrays.asList(e2, e3),
            removeExpired(30 * second));
        
        assertEquals(Arrays.asList(e1), events);
    }
    
    
    private BuildEvent e(long created) {
        return new BuildEvent(null, new Date(created));
    }
    
    
    private List<BuildEvent> removeExpired(long maxAge) {
        try {
            new DefaultBuildEventCleanupService(repo, maxAge).removeExpiredEvents();
            return new ArrayList<BuildEvent>(removed);
        } finally {
            events.removeAll(removed);
            removed.clear();
        }
    }
    
    
    private class MockRepo implements BuildEventRepository {
        
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
    }
}
