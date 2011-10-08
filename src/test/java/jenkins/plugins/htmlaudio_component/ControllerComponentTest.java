package jenkins.plugins.htmlaudio_component;

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
        
        assertEquals("{\"currentNotification\":null,\"notifications\":[]}",
            invoke("next",
                "previous", "123"));
    }
    
    
    public void test_expected_response_is_produced_when_notifications_are_available() {
        getConfig().setSuccessSoundUrl("successUrl");
        getConfig().setFailureSoundUrl("failureUrl");
        
        svc.recordBuildCompletion("job1", Result.SUCCESS, null);
        svc.recordBuildCompletion("job2", Result.FAILURE, null);
        
        assertEquals("{\"currentNotification\":\"2\",\"notifications\":[\"successUrl\",\"failureUrl\"]}",
            invoke("next",
                "previous", ""));
        
        assertEquals("{\"currentNotification\":\"2\",\"notifications\":[\"failureUrl\"]}",
            invoke("next",
                "previous", "1"));
        
        assertEquals("{\"currentNotification\":\"2\",\"notifications\":[]}",
            invoke("next",
                "previous", "2"));
    }
}
