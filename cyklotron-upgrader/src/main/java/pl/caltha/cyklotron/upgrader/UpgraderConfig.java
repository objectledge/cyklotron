package pl.caltha.cyklotron.upgrader;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * Cyklotron upgrader config class
 * 
 * @author lukasz
 *
 */
public class UpgraderConfig {

	static final Logger logger = Logger.getLogger(UpgraderConfig.class);

	private String cyklotronLogin;

	private String cyklotronPassword;

	private String cyklotronConnUrl;

	private String cyklotronConnProxyHost;

	private int cyklotronConnProxyPort;

	private String cyklotronConnProxyLogin;

	private String cyklotronConnProxyPassword;

	private String cyklotronDataLocation;
	
	private String cyklotronWarsLocation;

	private String tomcatLocation;

	private String postgresqlHost;

	private String postgresqlPort;

	private String postgresqlDatabase;

	private String postgresqlUser;

	private String postgresqlPassword;

	private Level loggerLevel;

	public UpgraderConfig(Document doc) {

		XPath xPath = XPathFactory.newInstance().newXPath();
		try {
			this.cyklotronConnUrl = (String) xPath.evaluate("//cyklotron/url",
					doc, XPathConstants.STRING);
			this.cyklotronLogin = (String) xPath.evaluate("//cyklotron/login",
					doc, XPathConstants.STRING);
			this.cyklotronPassword = (String) xPath.evaluate(
					"//cyklotron/password", doc, XPathConstants.STRING);
			this.cyklotronConnProxyHost = (String) xPath.evaluate(
					"//cyklotron/proxy/host", doc, XPathConstants.STRING);
			String port = (String) xPath.evaluate("//cyklotron/proxy/port",
					doc, XPathConstants.STRING);
			this.cyklotronConnProxyPort = port.isEmpty() ? 0 : Integer
					.parseInt((String) xPath.evaluate("//cyklotron/proxy/port",
							doc, XPathConstants.STRING));
			this.cyklotronConnProxyLogin = (String) xPath.evaluate(
					"//cyklotron/proxy/credentials/login", doc,
					XPathConstants.STRING);
			this.cyklotronConnProxyPassword = (String) xPath.evaluate(
					"//cyklotron/proxy/credentials/password", doc,
					XPathConstants.STRING);
			this.cyklotronDataLocation = (String) xPath.evaluate(
					"//cyklotron/data/location", doc, XPathConstants.STRING);			
			this.cyklotronWarsLocation = (String) xPath.evaluate(
					"//cyklotron/wars/location", doc, XPathConstants.STRING);
			this.tomcatLocation = (String) xPath.evaluate("//tomcat/location",
					doc, XPathConstants.STRING);
			if (this.tomcatLocation.isEmpty()) {
				this.tomcatLocation = System.getProperty("catalina.home", "");
			}
			this.postgresqlHost = (String) xPath.evaluate("//postgresql/host",
					doc, XPathConstants.STRING);
			this.postgresqlPort = (String) xPath.evaluate("//postgresql/port",
					doc, XPathConstants.STRING);
			this.postgresqlDatabase = (String) xPath.evaluate(
					"//postgresql/database", doc, XPathConstants.STRING);
			this.postgresqlUser = (String) xPath.evaluate("//postgresql/user",
					doc, XPathConstants.STRING);
			this.postgresqlPassword = (String) xPath.evaluate(
					"//postgresql/password", doc, XPathConstants.STRING);
			this.loggerLevel = Level.toLevel((String) xPath.evaluate(
					"//logger/level", doc, XPathConstants.STRING), Level.INFO);
		} catch (XPathExpressionException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	public static UpgraderConfig getConfig(InputStream configFile) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(configFile);
			return new UpgraderConfig(doc);
		} catch (ParserConfigurationException | IOException | SAXException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
	}

	public String getCyklotronLogin() {
		return cyklotronLogin;
	}

	public String getCyklotronPassword() {
		return cyklotronPassword;
	}

	public String getCyklotronConnUrl() {
		return cyklotronConnUrl;
	}

	public boolean isCyklotronConnProxy() {
		return !cyklotronConnProxyHost.isEmpty();
	}

	public String getCyklotronConnProxyHost() {
		return cyklotronConnProxyHost;
	}

	public int getCyklotronConnProxyPort() {
		return cyklotronConnProxyPort;
	}

	public boolean isCyklotronConnProxyAuth() {
		return !cyklotronConnProxyLogin.isEmpty()
				|| !cyklotronConnProxyPassword.isEmpty();
	}

	public String getCyklotronConnProxyLogin() {
		return cyklotronConnProxyLogin;
	}

	public String getCyklotronConnProxyPassword() {
		return cyklotronConnProxyPassword;
	}

	public String getCyklotronWarsLocation() {
		return cyklotronWarsLocation;
	}

	public String getCyklotronDataLocation() {
		return cyklotronDataLocation;
	}

	public String getTomcatLocation() {
		return tomcatLocation;
	}

	public String getPostgresqlHost() {
		return postgresqlHost;
	}

	public String getPostgresqlPort() {
		return postgresqlPort;
	}

	public String getPostgresqlDatabase() {
		return postgresqlDatabase;
	}

	public String getPostgresqlUser() {
		return postgresqlUser;
	}

	public String getPostgresqlPassword() {
		return postgresqlPassword;
	}

	public Level getLoggerLevel() {
		return loggerLevel;
	}
}
