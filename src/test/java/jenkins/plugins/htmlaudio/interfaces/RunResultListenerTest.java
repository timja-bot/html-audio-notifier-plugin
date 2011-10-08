package jenkins.plugins.htmlaudio.interfaces;

import static org.mockito.Mockito.*;

import hudson.model.Result;
import hudson.model.Run;

import jenkins.plugins.htmlaudio.app.NotificationService;
import jenkins.plugins.htmlaudio.interfaces.RunResultListener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
@SuppressWarnings("rawtypes")
public class RunResultListenerTest {
    
    private final RunResultListener listener = new RunResultListener();
    private final NotificationService svc = mock(NotificationService.class);
    
    
    {
        listener.setNotificationService(svc);
    }
    
    
    @Test
    public void completed_builds_are_recorded() {
        listener.onCompleted(createRun("job1", Result.SUCCESS), null);
        verify(svc).recordBuildCompletion("job1", Result.SUCCESS, null);
    }
    
    
    private Run createRun(String details, Result runResult) {
        final Run result = mock(Run.class);
        
        when(result.getFullDisplayName())
            .thenReturn(details);
        when(result.getResult())
            .thenReturn(runResult);
        
        return result;
    }
    
    
    @Test
    public void result_of_previous_build_is_recorded_if_available() {
        final Run current = createRun("job2", Result.SUCCESS);
        final Run previous = createRun(null, Result.FAILURE);
        
        when(current.getPreviousBuild())
            .thenReturn(previous);
        
        listener.onCompleted(current, null);
        verify(svc).recordBuildCompletion("job2", Result.SUCCESS, Result.FAILURE);
    }
}
