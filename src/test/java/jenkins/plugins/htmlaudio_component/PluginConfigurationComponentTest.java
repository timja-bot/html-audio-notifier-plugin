package jenkins.plugins.htmlaudio_component;

import jenkins.plugins.htmlaudio.app.Configuration;
import jenkins.plugins.htmlaudio.app.HtmlAudioNotifierPlugin.PluginDescriptor;
import jenkins.plugins.htmlaudio.domain.BuildResult;

import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.RunWith;

import support.HtmlAudioHudsonTestCase;


/** 
 * @author Lars Hvile
 */
@RunWith(JUnit38ClassRunner.class)
public class PluginConfigurationComponentTest extends HtmlAudioHudsonTestCase {
    
    private PluginDescriptor descriptor;
    private Configuration configuration;
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        descriptor = getConfig();
        configuration = getExtension(Configuration.class);
    }
    
    
    public void test_enabled_by_default_mirrors_value_from_descriptor() {
        descriptor.setEnabledByDefault(false);
        assertFalse(configuration.isEnabledByDefault());
        
        descriptor.setEnabledByDefault(true);
        assertTrue(configuration.isEnabledByDefault());
    }
    
    
    public void test_expected_URL_is_returned_for_each_result() {
        descriptor.setSuccessSoundUrl("success");
        descriptor.setFailureSoundUrl("failure");
        
        assertEquals("success", configuration.getSoundUrl(BuildResult.SUCCESS));
        assertEquals("failure", configuration.getSoundUrl(BuildResult.FAILURE));
    }
    
    
    public void test_null_is_returned_for_empty_URLs() {
        descriptor.setSuccessSoundUrl("");
        assertEquals(null, configuration.getSoundUrl(BuildResult.SUCCESS));
        
        descriptor.setSuccessSoundUrl("  \t\n  ");
        assertEquals(null, configuration.getSoundUrl(BuildResult.SUCCESS));
        
        descriptor.setSuccessSoundUrl(null);
        assertEquals(null, configuration.getSoundUrl(BuildResult.SUCCESS));
    }
}
