package jenkins.plugins.htmlaudio.domain;

import static org.junit.Assert.*;
import hudson.model.Result;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class BuildResultTest {
    
    @Test
    public void expected_results_corresponds_to_FAILURE() {
        assertTrue(BuildResult.FAILURE.correspondsTo(Result.FAILURE));
        assertTrue(BuildResult.FAILURE.correspondsTo(Result.UNSTABLE));
        assertFalse(BuildResult.FAILURE.correspondsTo(Result.SUCCESS));
    }
}
