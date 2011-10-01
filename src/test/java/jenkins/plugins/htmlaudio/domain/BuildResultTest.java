package jenkins.plugins.htmlaudio.domain;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

import hudson.model.Result;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class BuildResultTest {
    
    @Test
    public void failure_result_is_correctly_mapped() {
        assertEquals(BuildResult.FAILURE, BuildResult.toBuildResult(Result.FAILURE));
        assertEquals(BuildResult.FAILURE, BuildResult.toBuildResult(Result.UNSTABLE));
    }
    
    
    @Test
    public void success_result_is_correctly_mapped() {
        assertEquals(BuildResult.SUCCESS, BuildResult.toBuildResult(Result.SUCCESS));
    }
    
    
    @Test
    public void other_results_are_ignored() {
        for (Result r : asList(Result.ABORTED, Result.NOT_BUILT)) {
            assertNull(BuildResult.toBuildResult(r));
        }
    }
}
