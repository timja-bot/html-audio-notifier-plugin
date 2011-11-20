package jenkins.plugins.htmlaudio_component;

import static support.ConcurrencyUtils.assertExecutionTimeLessThanMs;
import hudson.model.Result;
import jenkins.plugins.htmlaudio.app.NotificationService;

import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;

import support.HtmlAudioHudsonTestCase;


/** 
 * @author Lars Hvile
 */
@RunWith(JUnit38ClassRunner.class)
public class ControllerComponentTest extends HtmlAudioHudsonTestCase {
    
    private NotificationService svc;
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.svc = getExtension(NotificationService.class);
        enableLongPolling(false);
    }
    
    
    private void enableLongPolling(boolean enabled) {
        getConfig().setLongPollingEnabled(enabled);
    }
    
    
    public void test_controller_exposes_enabledByDefault_state_properly() {
        enableByDefault(false);
        assertEquals("{\"enabled\":false}", invoke("isEnabledByDefault"));
        
        enableByDefault(true);
        assertEquals("{\"enabled\":true}", invoke("isEnabledByDefault"));
    }
    
    
    private void enableByDefault(boolean enabled) {
        getConfig().setEnabledByDefault(enabled);
    }
    
    
    public void test_expected_response_is_produced_when_no_notifications_are_available() {
        assertResponseContains(invoke("next", "previous", "123"),
            "\"currentNotification\":null",
            "\"notifications\":[]");
    }
    
    
    private void assertResponseContains(String response, String... elements) {
        for (String e : elements) {
            assertTrue("expected to find '" + e + "' in " + response,
                response.contains(e));
        }
    }
    
    
    public void test_expected_response_is_produced_when_notifications_are_available() {
        getConfig().setSuccessSoundUrl("successUrl");
        getConfig().setFailureSoundUrl("failureUrl");
        
        svc.recordBuildCompletion("job1", Result.SUCCESS, null);
        svc.recordBuildCompletion("job2", Result.FAILURE, null);
        
        assertResponseContains(invoke("next", "previous", ""),
            "\"currentNotification\":\"2\"",
            "\"notifications\":[\"successUrl\",\"failureUrl\"]");
        
        assertResponseContains(invoke("next", "previous", "1"),
            "\"currentNotification\":\"2\"",
            "\"notifications\":[\"failureUrl\"]");
        
        assertResponseContains(invoke("next", "previous", "2"),
            "\"currentNotification\":\"2\"",
            "\"notifications\":[]");
    }
    
    
    public void test_long_polling_is_activated_by_the_configuration_option() {
        getConfig().setSuccessSoundUrl("a");
        svc.recordBuildCompletion("job1", Result.SUCCESS, null);
        
        enableLongPolling(false);
        assertResponseContains(invoke("next"),
            "\"longPolling\":false");

        enableLongPolling(true);
        assertExecutionTimeLessThanMs(2000, new Runnable() {
            public void run() {
                assertResponseContains(invoke("next"),
                    "\"longPolling\":true");
            }
        });
    }
}
