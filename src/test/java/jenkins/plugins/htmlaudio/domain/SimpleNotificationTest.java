package jenkins.plugins.htmlaudio.domain;

import static org.junit.Assert.*;

import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.impl.SimpleNotification;

import org.junit.Test;
import org.junit.runners.JUnit4;

import org.junit.runner.RunWith;


@RunWith(JUnit4.class)
public class SimpleNotificationTest {
    
    @Test
    public void age_is_calculated_correctly() throws InterruptedException {
        final long started = System.currentTimeMillis();
        final Notification n = createNotification();
        
        long age = 0;
        while (age < 50) {
            Thread.sleep(10);
            
            age = n.getAgeInMs();
            final long expected = System.currentTimeMillis() - started;
            assertTrue("expected age <= " + expected + ", was " + age,
                age <= expected);
        }
    }
    
    
    private Notification createNotification() {
        return new SimpleNotification(null, null, null);
    }
}
