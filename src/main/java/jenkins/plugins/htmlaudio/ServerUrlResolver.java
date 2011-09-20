package jenkins.plugins.htmlaudio;

import jenkins.model.Jenkins;


/**
 * Resolves the server's root-URL.
 * 
 * @author Lars Hvile
 */
public interface ServerUrlResolver {

    /**
     * @see Jenkins#getRootUrl()
     */
    String getRootUrl();

}
