package jenkins.plugins.hulte;

import hudson.Extension;
import hudson.Plugin;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.model.listeners.RunListener;

// stapler json/xml @ExportedBean
public final class SimpleSoundNotifier extends Plugin {
    
    {
        System.out.println("> SimpleSoundNotifier started.. ");
    }
    
    @Extension
    public static final class GlobalBuildListener extends RunListener<Run<?, ?>> {
        
        {
            System.out.println("> GlobalBuildListener started.. ");
        }
        
        @Override
        public void onCompleted(Run<?, ?> r, TaskListener listener) {

            // TODO do something useful...
            System.out.println("> " + r.getDisplayName() + " / " + r.getResult());
        }
    }
}
