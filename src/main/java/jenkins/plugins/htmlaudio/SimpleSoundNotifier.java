package jenkins.plugins.hulte;

import jenkins.model.Jenkins;

import org.kohsuke.stapler.bind.JavaScriptMethod;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

/**
 * 
 * 
 * @author Lars Hvile
 */
public final class SimpleSoundNotifier extends Plugin {
    
    {
        System.out.println("> SimpleSoundNotifier started.. ");
    }
    
    
    public static SimpleSoundNotifier instance() {
        System.out.println("yep");
        // Jenkins.getInstance().getPlugin(shortName)
        // Jenkins.getInstance().getPluginManager().getPlugin(shortName)
        return Jenkins.getInstance().getPlugin(SimpleSoundNotifier.class);
    }
    
    
    @Extension
    public static final class GlobalBuildListener extends RunListener<Run<?, ?>> { // TODO dedicated class? has to report build-results into some shared structure..
        
        {
            System.out.println("> GlobalBuildListener started.. ");
            // hudson.model.Hudson.getInstance().getPl
        }
        
        @Override
        public void onCompleted(Run<?, ?> r, TaskListener listener) {

            // TODO do something useful...
            System.out.println("> " + r.getDisplayName() + " / " + r.getResult());
        }
    }
    
    
    @JavaScriptMethod
    public String wazzup() {
        return "http://localhost:8080/plugin/simple-sound-notifier/test.wav"; // TODO relative / not hardcoded plz
    }
}
