package jenkins.plugins.htmlaudio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.HudsonTestCase;


// TODO docme
@RunWith(JUnit38ClassRunner.class)
public class ControllerComponentTest extends HudsonTestCase { // TODO put in .acceptance?
    
    private final HttpClient httpClient = new HttpClient();
    
    
    /**
     * Make sure that the relative2absolute url conversion works, regression-test from the 1.430 upgrade.
     */
    public void test_controller_converts_to_absolute_urls() {
        final String response = invoke("toAbsoluteUrl",
                "url", "a-relative-url");
        assertEquals(serverUrl() + "plugin/html-audio-notifier/a-relative-url", response);
    }
    
    
    public void test_controller_exposes_enabledByDefault_state_properly() {
        enable(false);
        assertEquals("{\"enabled\":false}", invoke("isEnabledByDefault"));
        
        enable(true);
        assertEquals("{\"enabled\":true}", invoke("isEnabledByDefault"));
    }
    
    
    private void enable(boolean enabled) {
        jenkins.getPlugin(HtmlAudioNotifierPlugin.class).getDescriptor().setEnabledByDefault(enabled);
    }
    
    
    // TODO util-stuff, shared by .acceptance tests??
    private String invoke(String action, String... parameters) {
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
