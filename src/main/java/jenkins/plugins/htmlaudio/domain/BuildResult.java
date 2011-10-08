package jenkins.plugins.htmlaudio.domain;

import hudson.model.Result;


/**
 * The possible / interesting outcomes of a build.
 * 
 * @author Lars Hvile
 */
public enum BuildResult {

    SUCCESS_AFTER_FAILURE {
        @Override
        protected boolean matches(Result current, Result previous) {
            return current == Result.SUCCESS
                && isFailure(previous);
        }
    },
    
    FAILURE {
        @Override
        protected boolean matches(Result current, Result previous) {
            return isFailure(current);
        }
    },
    
    SUCCESS {
        @Override
        protected boolean matches(Result current, Result previous) {
            return current == Result.SUCCESS;
        }
    };
    
    
    /**
     * Returns a {@link BuildResult} matching a provided set of {@link Result}s or {@code null}.
     */
    public static BuildResult toBuildResult(Result current, Result previous) {
        for (BuildResult br : BuildResult.values()) {
            if (br.matches(current, previous)) {
                return br;
            }
        }
        return null;
    }
    
    
    private static boolean isFailure(Result r) {
        return Result.FAILURE == r
            || Result.UNSTABLE == r;
    }
    
    
    protected abstract boolean matches(Result current, Result previous);
}
