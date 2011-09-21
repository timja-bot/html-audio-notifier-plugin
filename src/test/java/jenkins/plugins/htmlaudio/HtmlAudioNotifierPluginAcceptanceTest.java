package jenkins.plugins.htmlaudio;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;

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
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.HudsonTestCase;
import org.jvnet.hudson.test.TestBuilder;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;


/**
 * Contains component-/acceptance-tests using a running Jenkins instance. These tests were originally 
 * in multiple test-files, but {@link HudsonTestCase} seems to have some issues with doing proper cleanup / 
 * reset between tests, so they're all embedded in this class now. 
 * 
 * @author Lars Hvile
 */
@RunWith(JUnit38ClassRunner.class)
public class HtmlAudioNotifierPluginAcceptanceTest extends HudsonTestCase {
    
    private final HttpClient httpClient = new HttpClient();
    
    
    public void test_configuration_does_not_change_by_accident() throws Exception {
        final PluginDescriptor config = getConfig();
        
        // value-set #1
        config.setEnabledByDefault(true);
        config.setFailureSoundUrl("f");
        configRoundtrip();
        assertTrue(config.isEnabledByDefault());
        assertEquals("f", config.getFailureSoundUrl());
        
        // value-set #2
        config.setEnabledByDefault(false);
        config.setFailureSoundUrl(null);
        configRoundtrip();
        assertFalse(config.isEnabledByDefault());
        assertEquals(null, config.getFailureSoundUrl());
    }
    
    
    private PluginDescriptor getConfig() {
        return jenkins.getPlugin(HtmlAudioNotifierPlugin.class).getDescriptor();
    }
    
    
    public void test_configuration_can_be_changed() throws Exception {
        final PluginDescriptor config = getConfig();
        
        config.setEnabledByDefault(false);
        config.setFailureSoundUrl(null);
        
        HtmlForm form = createWebClient()
            .goTo("configure")
            .getFormByName("config");

        ((HtmlCheckBoxInput)form.getInputByName("htmlAudioEnabledByDefault")).setChecked(true);
        ((HtmlTextInput)form.getInputByName("htmlAudioFailureSoundUrl")).setValueAttribute("changed");
        
        submit(form);
        
        assertTrue(config.isEnabledByDefault());
        assertEquals("changed", config.getFailureSoundUrl());
    }
    
    
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
        getConfig().setEnabledByDefault(enabled);
    }
    
    
    public void test_build_events_are_exposed_to_clients() {
        
        assertSoundPlayed(false);
        failSomeBuild();
        assertSoundPlayed(true);
    }
    
    
    private void assertSoundPlayed(boolean played) {
        final String failureSound = getConfig().getFailureSoundUrl();
        final String result = invoke("next");
        
        assertEquals((played
                ? "expected the failure-sound to be played"
                : "no sounds were supposed to be played") + ", result: " + result,
            played,
            result.contains(failureSound));
    }
    
    
    private void failSomeBuild() {
        try {
            final FreeStyleProject project = createFreeStyleProject();
            
            project.getBuildersList().add(new TestBuilder() {
                public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
                    build.setResult(Result.FAILURE);
                    return false;
                }
            });
            
            final FreeStyleBuild result = project.scheduleBuild2(0).get();
            assertEquals(Result.FAILURE, result.getResult());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    
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
