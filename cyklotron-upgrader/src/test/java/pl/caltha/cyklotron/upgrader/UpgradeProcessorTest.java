package pl.caltha.cyklotron.upgrader;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;

import org.junit.Test;

import pl.caltha.cyklotron.helpers.ResourceHelper;

public class UpgradeProcessorTest {

	@Test
	public void getResourceContent() {
		String assertContent = "FIND RESOURCE FROM 'category.category' SELECT id;";
		String readContent = ResourceHelper
				.getResourceFileContent("files/rml/test.rml");
		assertTrue(assertContent.equals(readContent));
	}

	@Test
	public void getConfig() throws FileNotFoundException {

		String configContent = ResourceHelper
				.getResourceFileContent("files/config/config.xml");
		InputStream is = new ByteArrayInputStream(configContent.getBytes());
		UpgraderConfig config = UpgraderConfig.getConfig(is);
		assertTrue("http://www.niepelnosprawni.pl".equals(config
				.getCyklotronConnUrl()));
		assertTrue("root".equals(config.getCyklotronLogin()));
		assertTrue("12345".equals(config.getCyklotronPassword()));
		assertTrue("integracje.caltha.pl".equals(config
				.getCyklotronConnProxyHost()));
		assertTrue(3128 == config.getCyklotronConnProxyPort());
		assertTrue("test".equals(config.getCyklotronConnProxyLogin()));
		assertTrue("12345".equals(config.getCyklotronConnProxyPassword()));
		assertTrue("/srv/cyklotron/data".equals(config
				.getCyklotronDataLocation()));
		assertTrue("/srv/cyklotron/wars".equals(config
				.getCyklotronWarsLocation()));
		assertTrue("/srv/cyklotron/tomcat".equals(config.getTomcatLocation()));
		assertTrue("127.0.0.1".equals(config.getPostgresqlHost()));
		assertTrue("5432".equals(config.getPostgresqlPort()));
		assertTrue("lcms".equals(config.getPostgresqlDatabase()));
		assertTrue("cyklotron".equals(config.getPostgresqlUser()));
		assertTrue("12345".equals(config.getPostgresqlPassword()));
		assertTrue("INFO".equals(config.getLoggerLevel().toString()));
	}
}
