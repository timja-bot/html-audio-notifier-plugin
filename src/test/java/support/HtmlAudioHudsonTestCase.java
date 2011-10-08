package support;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import jenkins.plugins.htmlaudio.app.HtmlAudioNotifierPlugin;
import jenkins.plugins.htmlaudio.app.HtmlAudioNotifierPlugin.PluginDescriptor;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.TestBuilder;


/**
 * Baseclass for running component-/acceptance-tests on the plugin within a running Jenkins instance. 
 * 
 * @author Lars Hvile
 */
public class HtmlAudioHudsonTestCase extends HudsonTestCase {
    
    private static final String TMP_DIR_PROP = "java.io.tmpdir";
    
    private final String systemTmpDir = System.getProperty(TMP_DIR_PROP);
    private final File testTmpDir = new File(systemTmpDir, getClass().getSimpleName());
    
    private final HttpClient httpClient = new HttpClient();
    
    
    @Override
    protected void setUp() throws Exception {
        System.setProperty(TMP_DIR_PROP, testTmpDir.getAbsolutePath());
        
        if (testTmpDir.exists()) {
            recursiveDelete(testTmpDir);
        }
        assertTrue(testTmpDir.mkdir());
        
        super.setUp();
    }
    
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        System.setProperty(TMP_DIR_PROP, systemTmpDir);
        recursiveDelete(testTmpDir);
    }
    
    
    private void recursiveDelete(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                recursiveDelete(f);
            } else {
                delete(f);
            }
        }
        delete(dir);
    }
    
    
    private void delete(File f) {
        if (!f.delete()) {
            f.deleteOnExit();
        }
    }


    protected final PluginDescriptor getConfig() {
        return jenkins.getPlugin(HtmlAudioNotifierPlugin.class).getDescriptor();
    }
    
    
    protected final <T> T getExtension(Class<T> type) {
        final List<T> extensions = jenkins.getExtensionList(type);
        assertEquals("expected 1 extension of type " + type + ", found " + extensions,
                1, extensions.size());
        return extensions.get(0);
    }
    
    
    protected final void failSomeBuild() {
        try {
            final FreeStyleProject project = createFreeStyleProject();
            
            project.getBuildersList().add(new TestBuilder() {
                public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
                        throws IOException {
                    return false;
                }
            });
            
            final FreeStyleBuild build = project.scheduleBuild2(0).get();
            assertEquals(Result.FAILURE, build.getResult());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
    protected final String invoke(String action, String... parameters) {
        try {
            final HttpMethod request = createRequest(action,
                    createRequestParameters(parameters));
            
            if (httpClient.executeMethod(request) != HttpServletResponse.SC_OK) {
                fail("response: " + request.getStatusLine()
                        + "\n" + request.getResponseBodyAsString());
            }
            
            return request.getResponseBodyAsString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    private List<NameValuePair> createRequestParameters(String[] parameters) {
        assertTrue(parameters.length % 2 == 0);
        
        final List<NameValuePair> result = new ArrayList<NameValuePair>();
        
        for (int i = 0; i < parameters.length; i += 2) {
            result.add(new NameValuePair(parameters[i], parameters[i + 1]));
        }
        
        result.add(new NameValuePair(".crumb", "test"));    // TODO css-forgery stuff? we have to support this for the JS-client as well...
        
        return result;
    }
    
    
    private HttpMethod createRequest(String action, List<NameValuePair> parameters)
            throws IOException {
        final PostMethod result = new PostMethod(controllerUrl() + "/" + action);
        result.setRequestBody(parameters.toArray(new NameValuePair[0]));
        return result;
    }


    private String controllerUrl() {
        return serverUrl() + "html-audio";
    }


    private String serverUrl() {
        try {
            return getURL().toExternalForm();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
