package support;

import static org.mockito.Mockito.*;

import jenkins.plugins.htmlaudio.domain.Notification;


public final class DomainObjectFactory {
    
    public static Notification createNotificationOfCertainAge(final long ageInMs) {
        final Notification result = mock(Notification.class);
        
        when(result.getAgeInMs())
            .thenReturn(ageInMs);
        
        return result;
    }
    
    
    private DomainObjectFactory() {
    }
}
