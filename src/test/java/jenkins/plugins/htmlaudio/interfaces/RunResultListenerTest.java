package jenkins.plugins.htmlaudio.interfaces;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import hudson.model.Result;
import hudson.model.Run;
import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;
import jenkins.plugins.htmlaudio.interfaces.RunResultListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import support.NotificationRepositoryAdapter;


@RunWith(JUnit4.class)
public class RunResultListenerTest {
    
    private final RunResultListener listener = new RunResultListener();
    private final List<Notification> events = new ArrayList<Notification>();
    
    private final NotificationRepository repository = new NotificationRepositoryAdapter() {
        public void add(Notification event) {
            events.add(event);
        };
    };
    
    
    {
        listener.setRepository(repository);
    }
    
    
    @Test
    public void results_that_does_not_correspond_to_buildResults_are_ignored() {
        onCompleted(Result.ABORTED);
        assertTrue(events.isEmpty());
    }
    
    
    private void onCompleted(Result r) {
        final Run<?, ?> run = Mockito.mock(Run.class);
        Mockito.when(run.getResult()).thenReturn(r);
        listener.onCompleted(run, null);
    }
    
    
    @Test
    public void results_that_do_correspond_to_buildResults_are_added_to_repository() {
        onCompleted(Result.FAILURE);
        assertEquals(1, events.size());
    }
}
