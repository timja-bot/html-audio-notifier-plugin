package jenkins.plugins.htmlaudio.domain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class BuildEventTest {
    
    @Test
    public void event_ids_are_automatically_generated_and_unique() {
        final long first = e().getId();
        assertEquals(first + 1, e().getId());
    }
    
    
    private BuildEvent e() {
        return new BuildEvent(null);
    }
}
