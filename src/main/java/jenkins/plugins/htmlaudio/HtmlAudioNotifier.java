package jenkins.plugins.htmlaudio;

import org.kohsuke.stapler.bind.JavaScriptMethod;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;


/**
 * TODO
 * 
 * @author Lars Hvile
 */
public final class HtmlAudioNotifier extends Plugin {
    
    @Extension
    public static final class GlobalBuildListener extends RunListener<Run<?, ?>> { // TODO dedicated class? has to report build-results into some shared structure..
        
        @Override
        public void onCompleted(Run<?, ?> r, TaskListener listener) {

            // TODO do something useful...
            System.out.println("> " + r.getDisplayName() + " / " + r.getResult());
        }
    }
    
    
    @JavaScriptMethod
    public String wazzup() {
        return "http://localhost:8080/plugin/html-audio-notifier/test.wav"; // TODO relative / not hardcoded plz
    }
}
