package jenkins.plugins.htmlaudio.app;

import jenkins.plugins.htmlaudio.app.HtmlAudioNotifierPlugin;
import jenkins.plugins.htmlaudio.app.HtmlAudioNotifierPlugin.PluginDescriptor;

import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;


@RunWith(JUnit38ClassRunner.class)
public class HtmlAudioNotifierPluginTest extends HudsonTestCase {
    
    /**
     * Configuration round-trip testing.
     * 
     * @see <a href="https://wiki.jenkins-ci.org/display/JENKINS/Unit+Test">Unit Test</a>
     */
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
}
