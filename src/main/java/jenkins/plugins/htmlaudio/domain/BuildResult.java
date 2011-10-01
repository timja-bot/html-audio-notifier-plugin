package jenkins.plugins.htmlaudio.domain;

import hudson.model.Result;


/**
 * The possible / interesting outcomes of a build.
 * 
 * @author Lars Hvile
 */
public enum BuildResult {
    
    FAILURE {
        @Override
        protected boolean matches(Result r) {
            return r == Result.FAILURE
                    || r == Result.UNSTABLE;
        }
    },
    
    SUCCESS {
        @Override
        protected boolean matches(Result r) {
            return r == Result.SUCCESS;
        }
    };
    
    
    /**
     * Returns a {@link BuildResult} matching a provided {@link Result} or {@code null}.
     */
    public static BuildResult toBuildResult(Result r) {
        for (BuildResult br : BuildResult.values()) {
            if (br.matches(r)) {
                return br;
            }
        }
        return null;
    }
    
    
    protected abstract boolean matches(Result r);
}
