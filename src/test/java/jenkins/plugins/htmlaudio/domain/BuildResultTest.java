package jenkins.plugins.htmlaudio.domain;

import static java.util.Arrays.asList;
import static jenkins.plugins.htmlaudio.domain.BuildResult.*;
import static org.junit.Assert.*;

import hudson.model.Result;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class BuildResultTest {
    
    @Test
    public void failure_result_is_correctly_mapped() {
        assertEquals(FAILURE, toBuildResult(Result.FAILURE, null));
        assertEquals(FAILURE, toBuildResult(Result.FAILURE, Result.FAILURE));
        assertEquals(FAILURE, toBuildResult(Result.UNSTABLE, null));
    }
    
    
    @Test
    public void success_result_is_correctly_mapped() {
        assertEquals(SUCCESS, toBuildResult(Result.SUCCESS, null));
        assertEquals(SUCCESS, toBuildResult(Result.SUCCESS, Result.SUCCESS));
        assertEquals(SUCCESS, toBuildResult(Result.SUCCESS, Result.ABORTED));
    }
    
    
    @Test
    public void success_after_failure_is_correctly_mapped() {
        assertEquals(SUCCESS_AFTER_FAILURE, toBuildResult(Result.SUCCESS, Result.FAILURE));
        assertEquals(SUCCESS_AFTER_FAILURE, toBuildResult(Result.SUCCESS, Result.UNSTABLE));
    }
    
    
    @Test
    public void other_results_are_ignored() {
        for (Result r : asList(Result.ABORTED, Result.NOT_BUILT)) {
            assertNull(toBuildResult(r, null));
            assertNull(toBuildResult(r, r));
        }
    }
}
