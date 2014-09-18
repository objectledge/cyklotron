package pl.caltha.cyklotron.helpers;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.log4j.Logger;

import pl.caltha.cyklotron.upgrader.UpgraderConfig;

/**
 * CyklotronHelper class
 * 
 * @author lukasz
 *
 */
public class CyklotronHelper {

	public static final String LOGIN_ACTION_PATH = "/ledge/view/site.SiteList?action=authentication.Login";

	public static final String LOGOUT_ACTION_PATH = "/ledge/view/site.SiteList?action=authentication.Logout";

	public static final String CONSOLE_ACTION_PATH = "/ledge/view/browser.Console?action=browser.ExecuteCommand";

	public static final String REINDEX_ACTION_PATH = "/ledge/view/search.ReindexDocuments?action=search.ReindexAll";

	public static final String REINDEX_STATUS_PATH = "/ledge/view/longops.ActiveOperations?code=bazy.search.ReindexAll";

	public static final String REINDEX_END_STATUS = "[]";

	public static final String VIEW_PATH = "/ledge/view/";

	static final Logger logger = Logger.getLogger(CyklotronHelper.class);

	private UpgraderConfig config;

	private String cyklotronDataLocation;

	private String cyklotronConfigLocation;

	private URI uriLogin, uriLogout, uriExecuteRML;

	private HttpClientHelper httpClient;

	public CyklotronHelper(UpgraderConfig config) {
		try {
			this.config = config;

			this.cyklotronDataLocation = config.getCyklotronDataLocation();
			this.cyklotronConfigLocation = config.getCyklotronDataLocation()
					+ "/config";

			this.uriLogin = new URI(config.getCyklotronConnUrl()
					+ LOGIN_ACTION_PATH);
			this.uriLogout = new URI(config.getCyklotronConnUrl()
					+ LOGOUT_ACTION_PATH);
			this.uriExecuteRML = new URI(config.getCyklotronConnUrl()
					+ CONSOLE_ACTION_PATH);
			this.httpClient = new HttpClientHelper();
			httpClient.setCookieStore(new BasicCookieStore());
			if (config.isCyklotronConnProxy()) {
				HttpHost proxy = new HttpHost(
						config.getCyklotronConnProxyHost(),
						config.getCyklotronConnProxyPort());
				RequestConfig requestConfig = RequestConfig.custom()
						.setProxy(proxy).build();
				httpClient.setRequestConfig(requestConfig);
				if (config.isCyklotronConnProxyAuth()) {
					CredentialsProvider credsProvider = new BasicCredentialsProvider();
					credsProvider.setCredentials(
							new AuthScope(config.getCyklotronConnProxyHost(),
									config.getCyklotronConnProxyPort()),
							new UsernamePasswordCredentials(config
									.getCyklotronConnProxyLogin(), config
									.getCyklotronConnProxyPassword()));
					httpClient.setCredsProvider(credsProvider);
				}
			}
		} catch (URISyntaxException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Cyklotron login action
	 * 
	 * @param login
	 * @param password
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void login(String login, String password)
			throws ClientProtocolException, IOException {

		Map<String, String> qp = new HashMap<String, String>();
		qp.put("login", login);
		qp.put("password", password);
		String response = httpClient.sentPostResquest(uriLogin, qp);
		logger.debug("login response:" + response);
	}

	/**
	 * Cyklotron login action
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void login() throws ClientProtocolException, IOException {

		Map<String, String> qp = new HashMap<String, String>();
		qp.put("login", config.getCyklotronLogin());
		qp.put("password", config.getCyklotronPassword());
		String response = httpClient.sentPostResquest(uriLogin, qp);
		logger.debug("login response:" + response);
	}

	/**
	 * Cyklotron logout action
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void logout() throws ClientProtocolException, IOException {
		String response = httpClient.sentGetResquest(uriLogout);
		logger.debug("logout response:" + response);
		httpClient.clearCookieStore();
	}

	/**
	 * function execute RML commands
	 * 
	 * @param command
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void executeRmlCommand(String command)
			throws ClientProtocolException, IOException {
		Map<String, String> qp = new HashMap<String, String>();
		qp.put("command", command);
		String response = httpClient.sentPostResquest(uriExecuteRML, qp);
		logger.debug("execute rml response:" + response);
	}

	/**
	 * function execute RML commands from resource file. File must be located
	 * under 'files/rml'
	 * 
	 * @param command
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void executeRml(String fileName) throws ClientProtocolException,
			IOException {
		Map<String, String> qp = new HashMap<String, String>();
		String command = ResourceHelper.getResourceFileContent("files/rml/"
				+ fileName);
		if (command != null) {
			qp.put("command", command);
			String response = httpClient.sentPostResquest(uriExecuteRML, qp);
			logger.debug("execute rml response:" + response);
		} else {
			logger.debug("file 'files/rml/" + fileName + "' not found");
		}
	}

	/**
	 * function execute fix methods
	 * 
	 * @param method
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public void executeFixMethod(String method) throws ClientProtocolException,
			IOException, URISyntaxException {
		String response = httpClient.sentGetResquest(new URI(config
				.getCyklotronConnUrl() + VIEW_PATH + method));
		logger.debug("execute fix method response:" + response);
	}

	/**
	 * set config file
	 * 
	 * @param version
	 * @param fileName
	 */
	public void setConfig(String version, String fileName) {
		Map<String, String> toReplace = new HashMap<String, String>();
		toReplace.put("${serverName}", config.getPostgresqlHost());
		toReplace.put("${databaseName}", config.getPostgresqlDatabase());
		toReplace.put("${user}", config.getPostgresqlUser());
		toReplace.put("${password}", config.getPostgresqlPassword());
		setConfigWithParams(version, fileName, toReplace);
	}

	/**
	 * delete config file
	 * 
	 * @param fileName
	 */
	public void deleteConfig(String fileName) {
		ResourceHelper.deleteFile(cyklotronConfigLocation + "/" + fileName);
	}

	/**
	 * make search folder backup
	 * 
	 */
	public void backupSearchIndexes() {
		Path dir = Paths.get(cyklotronDataLocation + "/data/search");
		Path dir_cpy = Paths.get(cyklotronDataLocation + "/data/search_cpy");
		if (dir != null) {
			if (dir_cpy != null && Files.isDirectory(dir_cpy)
					&& Files.isWritable(dir_cpy)) {
				ResourceHelper.deleteDir(dir_cpy);
			}
			if (Files.isDirectory(dir) && Files.isWritable(dir)) {
				ResourceHelper.copyDir(cyklotronDataLocation + "/data/search",
						cyklotronDataLocation + "/data/search_cpy");
			} else {
				logger.error("Dir " + dir.toString() + " is not writable");
			}
		} else {
			logger.info("Delete abort. Dir " + cyklotronDataLocation
					+ "/data/search" + " not exist");
		}
	}

	/**
	 * All search reindexing
	 * 
	 */
	public void executeReindexSearchMethod() throws ClientProtocolException,
			IOException, URISyntaxException {
		logger.debug("Execute reindex action");
		httpClient.sentGetResquest(new URI(config.getCyklotronConnUrl()
				+ REINDEX_ACTION_PATH));
		int i = 0;
		String response = "";
		while (!REINDEX_END_STATUS.equals(response) && i < 10) {
			sleep(60);
			logger.debug("Execute reindex status request");
			response = httpClient.sentGetResquest(new URI(config
					.getCyklotronConnUrl() + REINDEX_STATUS_PATH));
			logger.debug("Reindex status response: " + response);
			i++;
		}
		if (!REINDEX_END_STATUS.equals(response)) {
			logger.warn("Reindexing not finished on time. Run reindex '"
					+ REINDEX_ACTION_PATH + "' after upgrade process !!!");
		}
		logger.debug("Reindex action finished");
	}

	/**
	 * set config file
	 * 
	 * @param version
	 * @param fileName
	 * @param params
	 */
	private void setConfigWithParams(String version, String fileName,
			Map<String, String> params) {
		String configContent = ResourceHelper
				.getResourceFileContent("files/config/" + version + "/"
						+ fileName);
		if (configContent != null) {
			if (params != null) {
				for (String key : params.keySet()) {
					configContent = configContent.replace(key, params.get(key));
				}
			}
			ResourceHelper.saveFile(configContent, cyklotronConfigLocation
					+ "/" + fileName);
			logger.debug("seve config file to:" + cyklotronConfigLocation + "/"
					+ fileName);
		} else {
			logger.debug("file resource 'files/config/" + version + "/"
					+ fileName + "' not found");
		}
	}

	/**
	 * sleep for n seconds
	 * 
	 * @param seconds
	 */
	private void sleep(int seconds) {
		try {
			logger.debug("sleep for " + seconds + " seconds");
			for (int i = 0; i < seconds; i++) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
	}

}
