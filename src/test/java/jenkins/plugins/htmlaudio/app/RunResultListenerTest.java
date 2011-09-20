package jenkins.plugins.htmlaudio.app;

import static org.junit.Assert.*;

import hudson.model.Result;
import hudson.model.Run;
import jenkins.plugins.htmlaudio.app.RunResultListener;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


@RunWith(JUnit4.class)
public class RunResultListenerTest {
    
    private final RunResultListener listener = new RunResultListener();
    private final BuildEventRepository repository = BuildEventRepository.instance();
    
    
    @Test
    public void results_that_does_not_correspond_to_buildResults_are_ignored() {
        onCompleted(Result.SUCCESS);
        assertTrue(repository.list().isEmpty());
    }
    
    
    private void onCompleted(Result r) {
        final Run<?, ?> run = Mockito.mock(Run.class);
        Mockito.when(run.getResult()).thenReturn(r);
        listener.onCompleted(run, null);
    }
    
    
    @Test
    public void results_that_do_correspond_to_buildResults_are_added_to_repository() {
        onCompleted(Result.FAILURE);
        assertEquals(1, repository.list().size());
    }
}
