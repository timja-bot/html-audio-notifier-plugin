package jenkins.plugins.htmlaudio_component;

import jenkins.plugins.htmlaudio.app.HtmlAudioNotifierPlugin;
import jenkins.plugins.htmlaudio.app.HtmlAudioNotifierPlugin.PluginDescriptor;

import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;
import org.jvnet.hudson.test.HudsonTestCase;

import com.gargoylesoftware.htmlunit.html.HtmlCheckBoxInput;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;


/** 
 * @author Lars Hvile
 */
@RunWith(JUnit38ClassRunner.class)
public class HtmlAudioNotifierPluginComponentTest extends HudsonTestCase {
    
    public void test_configuration_does_not_change_by_accident() throws Exception {
        final PluginDescriptor config = getConfig();
        
        // value-set #1
        config.setEnabledByDefault(true);
        config.setSuccessSoundUrl("s");
        config.setFailureSoundUrl("f");
        configRoundtrip();
        assertTrue(config.isEnabledByDefault());
        assertEquals("s", config.getSuccessSoundUrl());
        assertEquals("f", config.getFailureSoundUrl());
        
        // value-set #2
        config.setEnabledByDefault(false);
        config.setSuccessSoundUrl(null);
        config.setFailureSoundUrl(null);
        configRoundtrip();
        assertFalse(config.isEnabledByDefault());
        assertEquals(null, config.getSuccessSoundUrl());
        assertEquals(null, config.getFailureSoundUrl());

        // value-set #3, empty URLs -> null
        config.setSuccessSoundUrl("");
        config.setFailureSoundUrl("\t");
        configRoundtrip();
        assertEquals(null, config.getSuccessSoundUrl());
        assertEquals(null, config.getFailureSoundUrl());
    }
    
    
    private PluginDescriptor getConfig() {
        return jenkins.getPlugin(HtmlAudioNotifierPlugin.class).getDescriptor();
    }
    
    
    public void test_configuration_can_be_changed() throws Exception {
        final PluginDescriptor config = getConfig();
        
        config.setEnabledByDefault(false);
        config.setSuccessSoundUrl(null);
        config.setFailureSoundUrl(null);
        
        HtmlForm form = createWebClient()
            .goTo("configure")
            .getFormByName("config");

        ((HtmlCheckBoxInput)form.getInputByName("htmlAudioEnabledByDefault")).setChecked(true);
        ((HtmlTextInput)form.getInputByName("htmlAudioSuccessSoundUrl")).setValueAttribute("success");
        ((HtmlTextInput)form.getInputByName("htmlAudioFailureSoundUrl")).setValueAttribute("failure");
        
        submit(form);
        
        assertTrue(config.isEnabledByDefault());
        assertEquals("success", config.getSuccessSoundUrl());
        assertEquals("failure", config.getFailureSoundUrl());
    }
}
