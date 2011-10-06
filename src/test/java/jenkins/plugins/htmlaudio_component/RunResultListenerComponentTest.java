package jenkins.plugins.htmlaudio_component;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class RunResultListenerComponentTest {
    
    @Test
    public void something() {
        fail();
    }
    
    /* TODO add some
    
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
    }*/
}
