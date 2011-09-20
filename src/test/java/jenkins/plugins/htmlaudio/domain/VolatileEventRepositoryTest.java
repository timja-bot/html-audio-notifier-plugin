package jenkins.plugins.htmlaudio.domain;

import static org.junit.Assert.*;
import static support.DomainObjectFactory.*;

import java.util.Arrays;

import jenkins.plugins.htmlaudio.domain.impl.VolatileBuildEventRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class VolatileEventRepositoryTest {
    
    private final BuildEventRepository repo = new VolatileBuildEventRepository();
    
    
    @Test
    public void repository_is_initially_empty() {
        assertTrue(repo.list().isEmpty());
    }
    
    
    @Test
    public void events_can_be_added() {
        final BuildEvent e1 = event();
        final BuildEvent e2 = event();
        
        repo.add(e1);
        assertRepoEquals(e1);
        
        repo.add(e2);
        assertRepoEquals(e1, e2);
    }
    
    
    private void assertRepoEquals(BuildEvent... events) {
        assertEquals(Arrays.asList(events), repo.list());
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void same_event_cannot_be_added_twice() {
        final BuildEvent e = event();
        
        repo.add(e);
        repo.add(e);
    }
    
    
    @Test
    public void events_can_be_removed() {
        final BuildEvent e1 = event();
        final BuildEvent e2 = event();
        final BuildEvent e3 = event();
        
        repo.add(e1);
        repo.add(e2);
        repo.add(e3);
        assertRepoEquals(e1, e2, e3);
        
        repo.remove(e2);
        assertRepoEquals(e1, e3);
        
        repo.remove(e1);
        repo.remove(e3);
        assertRepoEquals();
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void unknown_events_cannot_be_removed() {
        repo.remove(event());
    }
    
    
    @Test
    public void ordering_of_events_is_based_on_id() {
        final BuildEvent e1 = event();
        final BuildEvent e2 = event();
        
        repo.add(e2);
        repo.add(e1);
        assertRepoEquals(e1, e2);
    }
    
    
    @Test
    public void events_newer_than_a_provided_event_can_be_found() {
        final BuildEvent e1 = event();
        final BuildEvent e2 = event();
        final BuildEvent e3 = event();
        
        assertTrue(repo.findNewerThan(-1234).isEmpty());
        assertTrue(repo.findNewerThan(1234).isEmpty());
        
        repo.add(e1);
        assertTrue(repo.findNewerThan(e1.getId()).isEmpty());
        assertEquals(Arrays.asList(e1), repo.findNewerThan(e1.getId() - 1));
        
        repo.add(e2);
        repo.add(e3);
        assertEquals(Arrays.asList(e2, e3), repo.findNewerThan(e1.getId()));
        
        assertTrue(repo.findNewerThan(e3.getId()).isEmpty());
        assertTrue(repo.findNewerThan(e3.getId() + 1).isEmpty());
        
        final BuildEvent e4 = event();
        final BuildEvent e5 = event();
        repo.add(e5);
        
        assertEquals(Arrays.asList(e5), repo.findNewerThan(e4.getId()));
        assertTrue(repo.findNewerThan(e5.getId() + 1000).isEmpty());
    }
    
    
    @Test
    public void clients_cannot_mutate_repository_indirectly() {
        final BuildEvent e = event();
        repo.add(e);
        
        repo.list().clear();
        repo.findNewerThan(e.getId() - 1).clear();
        
        assertEquals(1, repo.list().size());
    }
    
    
    @Test
    public void id_of_the_last_event_can_be_retrieved() {
        final BuildEvent e1 = event();
        final BuildEvent e2 = event();
        
        assertNull(repo.getLastEventId());
        
        repo.add(e1);
        assertEquals(e1.getId(), (long)repo.getLastEventId());
        
        repo.add(e2);
        assertEquals(e2.getId(), (long)repo.getLastEventId());
        
        repo.remove(e2);
        assertEquals(e1.getId(), (long)repo.getLastEventId());
        
        repo.remove(e1);
        assertNull(repo.getLastEventId());
    }
}
