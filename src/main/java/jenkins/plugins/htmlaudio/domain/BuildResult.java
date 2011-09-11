package jenkins.plugins.htmlaudio.domain;

import hudson.model.Result;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * The possible (interesting) outcomes of a build.
 * 
 * @author Lars Hvile
 */
public enum BuildResult {
    FAILURE(Result.FAILURE, Result.UNSTABLE);
    
    private final Set<Result> mapping;
    
    private BuildResult(Result... mapping) {
        this.mapping = new HashSet<Result>(Arrays.asList(mapping));
    }
    
    /**
     * Returns {@code true} if a provided {@link Result} corresponds to a {@link BuildResult}.
     */
    public boolean correspondsTo(Result r) {
        return mapping.contains(r);
    }
}
