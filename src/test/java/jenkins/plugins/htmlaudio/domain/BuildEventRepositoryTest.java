package jenkins.plugins.htmlaudio.domain;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class BuildEventRepositoryTest {
    
    private final BuildEventRepository repo = new DefaultBuildEventRepository();
    
    
    @Test
    public void repository_is_initially_empty() {
        assertTrue(repo.list().isEmpty());
    }
    
    
    @Test
    public void events_can_be_added() {
        final BuildEvent e1 = e();
        final BuildEvent e2 = e();
        
        repo.add(e1);
        assertRepoEquals(e1);
        
        repo.add(e2);
        assertRepoEquals(e1, e2);
    }
    
    
    private BuildEvent e() {
        return new BuildEvent(null);
    }
    
    
    private void assertRepoEquals(BuildEvent... events) {
        assertEquals(Arrays.asList(events), repo.list());
    }
    
    
    @Test
    public void ordering_of_events_is_based_on_id() {
        final BuildEvent e1 = e();
        final BuildEvent e2 = e();
        
        repo.add(e2);
        repo.add(e1);
        assertRepoEquals(e1, e2);
    }
    
    
    @Test
    public void events_newer_than_a_provided_event_can_be_found() {
        final BuildEvent e1 = e();
        final BuildEvent e2 = e();
        final BuildEvent e3 = e();
        
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
        
        final BuildEvent e4 = e();
        final BuildEvent e5 = e();
        repo.add(e5);
        
        assertEquals(Arrays.asList(e5), repo.findNewerThan(e4.getId()));
        assertTrue(repo.findNewerThan(e5.getId() + 1000).isEmpty());
    }
    
    
    @Test
    public void events_older_than_a_provided_limit_can_be_removed() {
        final long base = System.currentTimeMillis();
        final long hour = TimeUnit.HOURS.toMillis(1);
        
        final BuildEvent e1 = e(base);
        final BuildEvent e2 = e(base - 1 * hour);
        final BuildEvent e3 = e(base - 2 * hour);
        final BuildEvent e4 = e(base);
        
        for (BuildEvent e : Arrays.asList(e1, e2, e3, e4)) {
            repo.add(e);
        }
        assertRepoEquals(e1, e2, e3, e4);
        
        // keep all
        repo.removeOlderThan(3 * hour);
        assertEquals(4, repo.list().size());
        
        // remove the oldest (e3)
        repo.removeOlderThan(2 * hour);
        assertRepoEquals(e1, e2, e4);
        
        // 2nd oldest (e2)
        repo.removeOlderThan(1 * hour);
        assertRepoEquals(e1, e4);
        
        // .. and the rest of 'em
        repo.removeOlderThan(0);
        assertRepoEquals();
    }
    
    
    private BuildEvent e(long created) {
        return new BuildEvent(null, new Date(created));
    }
    
    
    @Test
    public void clients_cannot_mutate_repository_indirectly() {
        final BuildEvent e = e();
        repo.add(e);
        
        repo.list().clear();
        repo.findNewerThan(e.getId() - 1).clear();
        
        assertEquals(1, repo.list().size());
    }
}
