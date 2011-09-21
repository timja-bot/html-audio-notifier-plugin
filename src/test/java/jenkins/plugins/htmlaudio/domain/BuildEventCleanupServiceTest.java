package jenkins.plugins.htmlaudio.domain;

import static org.junit.Assert.*;
import static support.DomainObjectFactory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import jenkins.plugins.htmlaudio.domain.impl.DefaultBuildEventCleanupService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import support.BuildEventRepositoryAdapter;


@RunWith(JUnit4.class)
public class BuildEventCleanupServiceTest {
    
    private final List<BuildEvent> events = new ArrayList<BuildEvent>();
    private final List<BuildEvent> removed = new ArrayList<BuildEvent>();
    private final BuildEventRepository repo = new MockRepo();
    
    
    @Test
    public void events_of_expected_age_are_removed_from_repository() {
        final long second = TimeUnit.SECONDS.toMillis(1);
        final long base = System.currentTimeMillis();
        
        final BuildEvent e1 = event(base);
        final BuildEvent e2 = event(base - 30 * second);
        final BuildEvent e3 = event(base - 60 * second);
        final BuildEvent e4 = event(base - 120 * second);
        
        events.addAll(Arrays.asList(e1, e2, e3, e4));
        
        // everything older than 1 minute should be removed
        assertEquals(Arrays.asList(e3, e4),
            removeExpired());

        assertEquals(Arrays.asList(e1, e2), events);
    }
    
    
    private List<BuildEvent> removeExpired() {
        try {
            new DefaultBuildEventCleanupService().removeExpiredEvents(repo);
            return new ArrayList<BuildEvent>(removed);
        } finally {
            events.removeAll(removed);
            removed.clear();
        }
    }
    
    
    private class MockRepo extends BuildEventRepositoryAdapter {
        
        public void remove(BuildEvent event) {
            removed.add(event);
        }

        public Collection<BuildEvent> list() {
            return Collections.unmodifiableList(events);
        }
    }
}
