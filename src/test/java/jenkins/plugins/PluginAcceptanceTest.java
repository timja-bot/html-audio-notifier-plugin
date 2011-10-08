package jenkins.plugins;

import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;

import support.HtmlAudioHudsonTestCase;


/** 
 * @author Lars Hvile
 */
@RunWith(JUnit38ClassRunner.class)
public class PluginAcceptanceTest extends HtmlAudioHudsonTestCase {
    
    private static final String NOTIFICATION_MARKER = "#URL#";
    
    
    @Override
    public void setUp() throws Exception {
        super.setUp();
        getConfig().setFailureSoundUrl(NOTIFICATION_MARKER);
    }
    
    
    public void test_failed_build_results_in_notification() {
        assertFalse(isNotificationAvailable());
        failSomeBuild();
        assertTrue(isNotificationAvailable());
    }
    
    
    private boolean isNotificationAvailable() {
        return invoke("next",
            "previous", "").contains(NOTIFICATION_MARKER);
    }
}
