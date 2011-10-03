package jenkins.plugins.htmlaudio.domain;

import static java.util.Arrays.asList;
import static jenkins.plugins.htmlaudio.domain.NotificationId.asNotificationId;
import static jenkins.plugins.htmlaudio.domain.NotificationId.createNotificationId;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class NotificationIdTest {
    
    final NotificationId id1 = asNotificationId(1);
    final NotificationId id2 = asNotificationId(2);
    final NotificationId id3 = asNotificationId(3);
    
    
    @Test
    public void unique_ids_can_be_generated() {
        final NotificationId first = createNotificationId();
        final NotificationId second = createNotificationId();

        assertEquals(first.getValue() + 1,
            second.getValue());
    }
    
    
    @Test
    public void equals_is_implemented_in_terms_of_value_and_follows_general_equals_contract() {
        assertEquals(id1, id1);
        assertEquals(id1, asNotificationId(1));
        assertFalse(id1.equals(id2));
        assertFalse(id1.equals(null));
    }
    
    
    @Test
    public void id_respects_hashCode_contract() {
        assertEquals(id1.hashCode(), id1.hashCode());
        assertEquals(id1.hashCode(), asNotificationId(1).hashCode());
        assertFalse(id1.hashCode() == id2.hashCode());
    }
    
    
    @Test
    public void ids_are_naturally_ordered_by_value() {
        final SortedSet<NotificationId> ids = new TreeSet<NotificationId>();
        ids.add(id3);
        ids.add(id1);
        ids.add(id2);
        
        assertEquals(asList(id1, id2, id3),
            new ArrayList<NotificationId>(ids));
    }
}
