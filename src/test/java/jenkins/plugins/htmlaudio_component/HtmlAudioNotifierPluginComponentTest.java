package jenkins.plugins.htmlaudio_component;

import jenkins.plugins.htmlaudio.app.HtmlAudioNotifierPlugin;
import jenkins.plugins.htmlaudio.app.HtmlAudioNotifierPlugin.PluginDescriptor;

import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.HudsonTestCase;

import org.htmlunit.html.HtmlCheckBoxInput;
import org.htmlunit.html.HtmlForm;
import org.htmlunit.html.HtmlTextInput;


/** 
 * @author Lars Hvile
 */
@RunWith(JUnit38ClassRunner.class)
public class HtmlAudioNotifierPluginComponentTest extends HudsonTestCase {
    
    public void test_configuration_does_not_change_by_accident() throws Exception {
        final PluginDescriptor config = getConfig();
        
        // value-set #1
        config.setEnabledByDefault(true);
        config.setLongPollingEnabled(true);
        config.setSuccessSoundUrl("s");
        config.setSuccessAfterFailureSoundUrl("sf");
        config.setFailureSoundUrl("f");
        configRoundtrip();
        assertTrue(config.isEnabledByDefault());
        assertTrue(config.isLongPollingEnabled());
        assertEquals("s", config.getSuccessSoundUrl());
        assertEquals("sf", config.getSuccessAfterFailureSoundUrl());
        assertEquals("f", config.getFailureSoundUrl());
        
        // value-set #2
        config.setEnabledByDefault(false);
        config.setLongPollingEnabled(false);
        config.setSuccessSoundUrl(null);
        config.setSuccessAfterFailureSoundUrl(null);
        config.setFailureSoundUrl(null);
        configRoundtrip();
        assertFalse(config.isEnabledByDefault());
        assertFalse(config.isLongPollingEnabled());
        assertEquals("", config.getSuccessSoundUrl());
        assertEquals("", config.getSuccessAfterFailureSoundUrl());
        assertEquals("", config.getFailureSoundUrl());
    }
    
    
    private PluginDescriptor getConfig() {
        return jenkins.getPlugin(HtmlAudioNotifierPlugin.class).getDescriptor();
    }
    
    
    public void test_configuration_can_be_changed() throws Exception {
        final PluginDescriptor config = getConfig();
        
        config.setEnabledByDefault(false);
        config.setLongPollingEnabled(false);
        config.setSuccessSoundUrl(null);
        config.setSuccessAfterFailureSoundUrl(null);
        config.setFailureSoundUrl(null);
        
        HtmlForm form = createWebClient()
            .goTo("configure")
            .getFormByName("config");

        ((HtmlCheckBoxInput)form.getInputByName("htmlAudioEnabledByDefault")).setChecked(true);
        ((HtmlCheckBoxInput)form.getInputByName("htmlAudioLongPollingEnabled")).setChecked(true);
        ((HtmlTextInput)form.getInputByName("htmlAudioSuccessSoundUrl")).setValue("success");
        ((HtmlTextInput)form.getInputByName("htmlAudioSuccessAfterFailureSoundUrl")).setValue("successAfter");
        ((HtmlTextInput)form.getInputByName("htmlAudioFailureSoundUrl")).setValue("failure");
        
        submit(form);
        
        assertTrue(config.isEnabledByDefault());
        assertTrue(config.isLongPollingEnabled());
        assertEquals("success", config.getSuccessSoundUrl());
        assertEquals("successAfter", config.getSuccessAfterFailureSoundUrl());
        assertEquals("failure", config.getFailureSoundUrl());
    }
}
