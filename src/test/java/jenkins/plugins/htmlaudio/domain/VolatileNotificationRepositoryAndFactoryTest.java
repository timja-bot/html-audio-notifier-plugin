package jenkins.plugins.htmlaudio.domain;

import static java.util.Arrays.asList;
import static jenkins.plugins.htmlaudio.domain.NotificationId.asNotificationId;
import static org.junit.Assert.*;
import static support.ConcurrencyUtils.assertExecutionTimeLessThanMs;
import static support.ConcurrencyUtils.await;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;

import jenkins.plugins.htmlaudio.domain.NotificationRepository.NotificationRemover;
import jenkins.plugins.htmlaudio.domain.impl.VolatileNotificationRepositoryAndFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import support.ConcurrencyUtils;


@RunWith(JUnit4.class)
public class VolatileNotificationRepositoryAndFactoryTest {
    
    private final VolatileNotificationRepositoryAndFactory impl = new VolatileNotificationRepositoryAndFactory();
    private final NotificationRepository repo = impl;
    private final NotificationFactory factory = impl;
    
    
    @Test
    public void repository_is_initially_empty() {
        assertRepoEquals();
    }
    
    
    private void assertRepoEquals(Notification... notifications) {
        assertEquals(asList(notifications), repo.findNewerThan(null));
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void notifications_cannot_be_created_without_sound_URLs() {
        factory.createAndPersist(null, "details");
    }
    
    
    @Test
    public void factory_creates_notifications_with_unique_ascending_ids() {
        final Notification n1 = createNotification();
        final Notification n2 = createNotification();
        
        assertEquals(asNotificationId(n1.getId().getValue() + 1),
            n2.getId());
    }
    
    
    @Test
    public void factory_creates_expected_notifications() {
        final Notification n = factory.createAndPersist("url", "$details$");
        
        assertEquals("url", n.getSoundUrl());
        assertTrue(n.toString().contains("$details$"));
    }
    
    
    @Test
    public void created_notifications_are_instantly_available_in_repo() {
        final Notification n1 = createNotification();
        assertRepoEquals(n1);
        
        final Notification n2 = createNotification();
        assertRepoEquals(n1, n2);
    }
    
    
    private Notification createNotification() {
        return factory.createAndPersist("url", "details");
    }
    
    
    @Test
    public void specific_notifications_can_be_removed() {
        final Notification n1 = createNotification();
        final Notification n2 = createNotification();
        final Notification n3 = createNotification();
        
        assertRepoEquals(n1, n2, n3);
        
        remove(n2);
        assertRepoEquals(n1, n3);
        
        remove(n3);
        assertRepoEquals(n1);
        
        remove(n1);
        assertRepoEquals();
    }
    
    
    private void remove(final Notification n) {
        repo.remove(new NotificationRemover() {
            public void remove(Iterator<Notification> notifications) {
                while (notifications.hasNext()) {
                    final Notification next = notifications.next();
                    if (n == next) {
                        notifications.remove();
                        return;
                    }
                }
                fail("unable to remove " + n);
            }
        });
    }
    
    
    @Test
    public void all_notifications_can_be_removed() {
        final Notification n1 = createNotification();
        final Notification n2 = createNotification();
        final Notification n3 = createNotification();
        
        final List<Notification> removed = new ArrayList<Notification>();
        
        repo.remove(new NotificationRemover() {
            public void remove(Iterator<Notification> notifications) {
                while (notifications.hasNext()) {
                    removed.add(notifications.next());
                    notifications.remove();
                }
            }
        });
        
        assertEquals(asList(n1, n2, n3), removed);
        assertRepoEquals();
    }
    
    
    @Test
    public void removal_iterator_respects_iterator_contract() {
        final Notification n1 = createNotification();
        final Notification n2 = createNotification();
        
        // next() throws NoSuchElementExc when there's no more elements
        repo.remove(new NotificationRemover() {
            public void remove(Iterator<Notification> notifications) {
                notifications.next();
                notifications.next();
                
                try {
                    notifications.next();
                    fail();
                } catch (NoSuchElementException e) {
                }
            }
        });
        assertRepoEquals(n1, n2);
        
        // remove() cannot be called until an element has been fetched
        try {
            repo.remove(new NotificationRemover() {
                public void remove(Iterator<Notification> notifications) {
                    notifications.remove();
                    fail();
                }
            });
        } catch (IllegalStateException e) {
        }
        assertRepoEquals(n1, n2);
        
        // same element cannot be deleted twice
        repo.remove(new NotificationRemover() {
            public void remove(Iterator<Notification> notifications) {
                notifications.next();
                notifications.remove();
                
                try {
                    notifications.remove();
                    fail();
                } catch (IllegalStateException e) {
                }
            }
        });
        assertRepoEquals(n2);
        
        // .. and make sure it's still intact
        final Notification n3 = createNotification();
        assertRepoEquals(n2, n3);
    }
    
    
    @Test
    public void id_of_the_last_notification_can_be_retrieved() {
        assertNull(repo.getLastNotificationId());
        
        final Notification n1 = createNotification();
        assertEquals(n1.getId(), repo.getLastNotificationId());
        
        final Notification n2 = createNotification();
        assertEquals(n2.getId(), repo.getLastNotificationId());
        
        remove(n2);
        assertEquals(n1.getId(), repo.getLastNotificationId());
        
        remove(n1);
        assertNull(repo.getLastNotificationId());
    }
    
    
    @Test
    public void notifications_newer_than_X_can_be_found() {
        assertTrue(repo.findNewerThan(asNotificationId(-1234)).isEmpty());
        assertTrue(repo.findNewerThan(asNotificationId(1234)).isEmpty());
        
        final Notification n1 = createNotification();
        assertTrue(repo.findNewerThan(n1.getId()).isEmpty());
        assertEquals(asList(n1),
            repo.findNewerThan(relId(n1, -1)));
        
        final Notification n2 = createNotification();
        final Notification n3 = createNotification();
        assertEquals(asList(n2, n3),
            repo.findNewerThan(n1.getId()));
        
        assertTrue(repo.findNewerThan(n3.getId()).isEmpty());
        assertTrue(repo.findNewerThan(relId(n3, 1)).isEmpty());
        
        assertTrue(repo.findNewerThan(relId(n3, 1000)).isEmpty());
    }
    
    
    private NotificationId relId(Notification n, long value) {
        return relId(n.getId(), value);
    }
    
    
    private NotificationId relId(NotificationId id, long value) {
        return asNotificationId(id.getValue() + value);
    }
    
    
    @Test
    public void notifications_newer_than_X_can_be_found_even_after_removal_of_some() {
        final Notification n1 = createNotification();
        final Notification n2 = createNotification();
        
        assertEquals(asList(n2), repo.findNewerThan(n1.getId()));
        remove(n1);
        assertEquals(asList(n2), repo.findNewerThan(null));
        assertEquals(asList(n2), repo.findNewerThan(n1.getId()));
        
        final Notification n3 = createNotification();
        final Notification n4 = createNotification();
        final Notification n5 = createNotification();
        
        remove(n2);
        remove(n5);
        assertEquals(asList(n3, n4), repo.findNewerThan(n1.getId()));
        
        final Notification n6 = createNotification();
        assertEquals(asList(n3, n4, n6), repo.findNewerThan(n1.getId()));
    }
    
    
    @Test
    public void clients_cannot_modify_repository_indirectly() {
        createNotification();
        
        repo.findNewerThan(null).clear();
        assertEquals(1, repo.findNewerThan(null).size());
    }
    
    
    @Test
    public void clients_are_notified_when_new_notifications_are_available() {
        final CountDownLatch clientWaiting = new CountDownLatch(1);
        final CountDownLatch clientNotified = new CountDownLatch(1);
        
        new Thread() {
            @Override public void run() {
                synchronized (repo) {
                    clientWaiting.countDown();
                    ConcurrencyUtils.wait(repo);
                    clientNotified.countDown();
                }
            };
        }.start();
        await(clientWaiting);
        
        assertExecutionTimeLessThanMs(1000, new Runnable() {
            public void run() {
                createNotification();
                await(clientNotified);
            }
        });
    }
}
