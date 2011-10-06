package jenkins.plugins.htmlaudio_component;

import java.util.List;

import jenkins.plugins.htmlaudio.app.NotificationService;
import jenkins.plugins.htmlaudio.domain.Notification;

import org.junit.Test;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;

import support.HtmlAudioHudsonTestCase;


@RunWith(JUnit38ClassRunner.class)
public class RunResultListenerComponentTest extends HtmlAudioHudsonTestCase {
    
    private NotificationService svc;
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.svc = getExtension(NotificationService.class);
        getConfig().setFailureSoundUrl("failure");
    }
    
    
    @Test
    public void test_notifications_are_created_based_on_builds() {
        failSomeBuild();
        
        final List<Notification> notifications = svc.findNewNotifications(null).getNotifications();
        assertEquals(1, notifications.size());
        assertEquals("failure", notifications.get(0).getSoundUrl());
    }
}
