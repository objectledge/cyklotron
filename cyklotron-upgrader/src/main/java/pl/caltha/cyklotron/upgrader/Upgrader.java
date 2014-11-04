package pl.caltha.cyklotron.upgrader;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Upgrader class
 * 
 * @author lukasz
 *
 */
public class Upgrader {

	private static final Logger logger = Logger.getLogger(Upgrader.class);

	private static Map<String, String> params;

	public static void main(String[] args) {
		try {
			params = getParams(args);
			FileInputStream configFile = new FileInputStream(
					params.get("config"));
			UpgraderConfig config = UpgraderConfig.getConfig(configFile);
			LogManager.getRootLogger().setLevel(config.getLoggerLevel());
			UpgradeProcessor upgradeProcessor = new UpgradeProcessor(config);
			boolean test = params.containsKey("test");
			if (test) {
				upgradeProcessor.test(params.get("test"));
			} else {
				String from = params.containsKey("from") ? params.get("from")
						: "";
				String to = params.containsKey("to") ? params.get("to") : "";
				upgradeProcessor.upgrade(
						CyklotronVerision.fromVersionName(from),
						CyklotronVerision.fromVersionName(to));
			}
		} catch (IllegalArgumentException e) {
			logger.error(e.getMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static Map<String, String> getParams(String[] args)
			throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		Map<String, String> rewrite = new HashMap<String, String>();
		rewrite.put("-c", "config");
		rewrite.put("-config", "config");
		rewrite.put("-f", "from");
		rewrite.put("-from", "from");
		rewrite.put("-t", "to");
		rewrite.put("-to", "to");
		rewrite.put("-test", "test");
		if (args.length % 2 == 0 && args.length > 0) {
			for (int i = 0; i < args.length; i = i + 2) {
				if (rewrite.containsKey(args[i])) {
					params.put(rewrite.get(args[i]), args[i + 1]);
				}
			}
			if (!params.containsKey("config")) {
				throw new IllegalArgumentException(
						"Path to config file parameter required: '[-c|-config] config.xml'");
			}
		} else {
			String msg = "Syntax:\n cyklotron-upgrader-{verison}.jar [-c|-config] path/to/config/file [[-f|-from] version_number [-t|-to] version_number | [-test [all|cyklotron|tomcat|postgres]]]\n\n";
			msg += "version_number set:" + CyklotronVerision.listVersionNames()
					+ "\n\n";
			throw new IllegalArgumentException("Bad parameters." + "\n" + msg);
		}
		return params;
	}
}