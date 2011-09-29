package jenkins.plugins.htmlaudio.app;

import static org.junit.Assert.*;
import static support.DomainObjectFactory.*;

import java.util.concurrent.atomic.AtomicBoolean;

import jenkins.plugins.htmlaudio.app.Controller;
import jenkins.plugins.htmlaudio.app.util.Configuration;
import jenkins.plugins.htmlaudio.app.util.ServerUrlResolver;
import jenkins.plugins.htmlaudio.domain.BuildEvent;
import jenkins.plugins.htmlaudio.domain.BuildEventCleanupService;
import jenkins.plugins.htmlaudio.domain.BuildEventRepository;
import jenkins.plugins.htmlaudio.domain.impl.VolatileBuildEventRepository;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ControllerTest {
    
    private final Controller c = new Controller();
    private final BuildEventRepository repo = new VolatileBuildEventRepository();
    
    private String rootUrl = "http://root/";
    private boolean enabledByDefault = false;
    private String failureSoundUrl = "f";
    
    
    {
        c.setServerUrlResolver(new ServerUrlResolver() {
            public String getRootUrl() {
                return rootUrl;
            }
        });
        
        c.setRepository(repo);
        
        c.setConfiguration(new Configuration() {
            public boolean isEnabledByDefault() {
                return enabledByDefault;
            }
            public String getFailureSoundUrl() {
                return failureSoundUrl;
            }
        });
        
        c.setCleanupService(new BuildEventCleanupService() {
            public void removeExpiredEvents(BuildEventRepository repository) {
                // empty
            }
        });
    }
    
    
    @Test
    public void isEnabled_produces_expected_result() {
        enabledByDefault = false;
        assertEquals(new JSONObject().element("enabled", false),
            c.isEnabledByDefault());
        
        enabledByDefault = true;
        assertEquals(new JSONObject().element("enabled", true),
            c.isEnabledByDefault());
    }
    
    
    @Test
    public void current_notification_is_null_if_repository_is_empty() {
        assertNull(repo.getLastEventId());
        
        assertEquals(new JSONObject(true),
            c.next(null).get("currentNotification"));
    }
    
    
    @Test
    public void id_of_last_event_is_exposed_to_clients() {
        final BuildEvent e1 = event();
        repo.add(e1);
        assertEquals(e1.getId() + "",
            c.next(null).get("currentNotification"));
        
        final BuildEvent e2 = event();
        repo.add(e2);
        assertEquals(e2.getId() + "",
            c.next(null).get("currentNotification"));
        
        // make sure we still get the id even if no notifications should be produced..
        assertEquals(e2.getId() + "",
            c.next(e2.getId() + "").get("currentNotification"));
    }
    
    
    @Test
    public void all_new_events_are_published_as_notifications() {
        final BuildEvent e1 = event();
        final BuildEvent e2 = event();
        
        repo.add(e1);
        repo.add(e2);
        
        assertEquals(2,
            ((JSONArray)c.next(null).get("notifications")).size());
        
        assertEquals(1,
            ((JSONArray)c.next(e1.getId() + "").get("notifications")).size());
        
        assertEquals(0,
            ((JSONArray)c.next(e2.getId() + "").get("notifications")).size());
        
        assertEquals(0,
            ((JSONArray)c.next(e2.getId() + 1 + "").get("notifications")).size());
    }
    
    
    @Test
    public void invalid_provided_values_for_previous_notification_are_discarded() {
        addEvent();
        
        assertEquals(1,
            ((JSONArray)c.next(null).get("notifications")).size());
        
        assertEquals(1,
            ((JSONArray)c.next("invalid").get("notifications")).size());
    }
    
    
    private void addEvent() {
        final BuildEvent e = event();
        repo.add(e);
    }
    
    
    @Test
    public void FAILURE_events_produces_the_failure_notification() {
        addEvent();
        
        failureSoundUrl = "expected";
        
        final String actual = ((JSONArray)c.next(null).get("notifications")).get(0).toString();
        
        assertTrue(actual,
            actual.contains(failureSoundUrl));
    }
    
    
    @Test
    public void no_notifications_are_produced_if_sound_url_is_not_configured() {
        addEvent();
        
        failureSoundUrl = null;
        assertNoNotificationsProduced();
        
        failureSoundUrl = "  ";
        assertNoNotificationsProduced();
    }
    
    
    private void assertNoNotificationsProduced() {
        assertEquals(0,
            ((JSONArray)c.next(null).get("notifications")).size());
    }
    
    
    @Test
    public void absolute_sound_urls_are_not_modified() {
        addEvent();
        
        failureSoundUrl = "http://someSound";
        
        assertEquals(failureSoundUrl,
            ((JSONArray)c.next(null).get("notifications")).get(0).toString());
    }
    
    
    @Test
    public void relative_urls_are_converted_to_absolute() {
        addEvent();
        failureSoundUrl = "rel";
        
        assertEquals(rootUrl + "plugin/html-audio-notifier/sounds/rel",
            ((JSONArray)c.next(null).get("notifications")).get(0).toString());
    }
    
    
    @Test
    public void expired_events_are_automatically_removed() {
        final AtomicBoolean cleanedUp = new AtomicBoolean();
        
        c.setCleanupService(new BuildEventCleanupService() {
            public void removeExpiredEvents(BuildEventRepository repository) {
                assertSame(ControllerTest.this.repo, repository);
                cleanedUp.set(true);
            }
        });
        
        assertFalse(cleanedUp.get());
        c.next(null);
        assertTrue(cleanedUp.get());
    }
}
