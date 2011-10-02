package jenkins.plugins.htmlaudio.domain;


/**
 * A notification based on a {@link BuildEvent}.
 * 
 * @author Lars Hvile
 */
public final class Notification { // TODO useful?
    
    private final BuildEvent event;
    private final String soundUrl;
    
    
    public Notification(BuildEvent event, String soundUrl) {
        this.event = event;
        this.soundUrl = soundUrl;
    }
    
    
    @Override
    public String toString() {
        return event + " - " + soundUrl;
    }
    
    
    /**
     * The URL of a sound that should be played.
     */
    public String getSoundUrl() {
        return soundUrl;
    }
}
