package jenkins.plugins.htmlaudio.app;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static jenkins.plugins.htmlaudio.domain.NotificationId.asNotificationId;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;

import jenkins.plugins.htmlaudio.app.impl.DefaultNotificationService;
import jenkins.plugins.htmlaudio.domain.Notification;
import jenkins.plugins.htmlaudio.domain.NotificationCleanupService;
import jenkins.plugins.htmlaudio.domain.NotificationId;
import jenkins.plugins.htmlaudio.domain.NotificationRepository;
import jenkins.plugins.htmlaudio.domain.impl.SimpleNotification;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InOrder;


@RunWith(JUnit4.class)
public class DefaultNotificationServiceTest {
    
    private final DefaultNotificationService svc = new DefaultNotificationService();
    private final NotificationRepository notificationRepo = mock(NotificationRepository.class);
    private final NotificationCleanupService notificationCleanupService = mock(NotificationCleanupService.class);
    
    private final List<Notification> noNotifications = emptyList();
    
    
    {
        svc.setNotificationRepository(notificationRepo);
        svc.setNotificationCleanupService(notificationCleanupService);
    }
    
    
    @Test
    public void query_for_new_notifications_results_in_expected_repository_invocations() {
        final NotificationId id = asNotificationId(1);
        
        svc.findNewNotifications(id);
        
        /*
         * The ordering here _is_ important. Since we don't want any external locking around the repo-usage,
         * the currently last notification-id should be extracted before we check for new notifications. This
         * way we can make sure that the correct 'lastNotificationId' is exposed to clients, and no
         * notifications are lost in space..
         */
        final InOrder inOrder = inOrder(notificationRepo);
        inOrder.verify(notificationRepo).getLastNotificationId();
        inOrder.verify(notificationRepo).findNewerThan(id);
        inOrder.verifyNoMoreInteractions();
    }
    
    
    @Test
    public void expected_reply_is_produced_when_repo_is_empty() {
        when(notificationRepo.findNewerThan(null))
            .thenReturn(noNotifications);
        
        final NewNotificationsResult result = svc.findNewNotifications(null);
        
        assertNull(result.getLastNotificationId());
        assertTrue(result.getNotifications().isEmpty());
    }
    
    
    @Test
    public void expected_reply_is_produced_when_no_new_notifications_are_available() {
        when(notificationRepo.getLastNotificationId())
            .thenReturn(asNotificationId(123));
        when(notificationRepo.findNewerThan(null))
            .thenReturn(noNotifications);
        
        final NewNotificationsResult result = svc.findNewNotifications(null);
        assertEquals(asNotificationId(123), result.getLastNotificationId());
        assertTrue(result.getNotifications().isEmpty());
    }
    
    
    @Test
    public void expected_reply_is_produced_when_new_notifications_are_available() {
        when(notificationRepo.getLastNotificationId())
            .thenReturn(asNotificationId(123));
        when(notificationRepo.findNewerThan(null))
            .thenReturn(asList(n(asNotificationId(123))));
    
        final NewNotificationsResult result = svc.findNewNotifications(null);
        assertEquals(asNotificationId(123), result.getLastNotificationId());
        assertEquals(1, result.getNotifications().size());
        assertEquals(123, result.getNotifications().get(0).getId().getValue()); 
    }
    
    
    private Notification n(NotificationId id) {
        return new SimpleNotification(id, "url", null);
    }
    
    
    /*
     * Basically a thread-safety issue. To make sure that the same notification isn't played twice, verify
     * that the lastNotificationId() value is suppressed if new notifications are found.
     */
    @Test
    public void id_of_last_registered_notification_is_suppressed_by_extracted_notifications() {
        when(notificationRepo.getLastNotificationId())
            .thenReturn(asNotificationId(123));
        when(notificationRepo.findNewerThan(null))
            .thenReturn(asList(n(asNotificationId(123)), n(asNotificationId(124))));
    
        final NewNotificationsResult result = svc.findNewNotifications(null);
        assertEquals(asNotificationId(124), result.getLastNotificationId());
    }
}
