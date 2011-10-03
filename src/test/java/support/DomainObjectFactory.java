package support;

import jenkins.plugins.htmlaudio.domain.Notification;


public final class DomainObjectFactory {
    
    public static Notification createNotificationOfCertainAge(final long ageInMs) {
        return new NotificationAdapter() {
            @Override public long getAgeInMs() {
                return ageInMs;
            }
        };
    }
    
    
    private DomainObjectFactory() {
    }
}
