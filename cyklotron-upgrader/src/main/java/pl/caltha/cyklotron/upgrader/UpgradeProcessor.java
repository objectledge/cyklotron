package pl.caltha.cyklotron.upgrader;

import org.apache.log4j.Logger;

import pl.caltha.cyklotron.helpers.CyklotronHelper;
import pl.caltha.cyklotron.helpers.PostgresqlHelper;
import pl.caltha.cyklotron.helpers.TomcatHelper;

/**
 * UpgradeProcessor class
 * 
 * @author lukasz
 *
 */
public class UpgradeProcessor {

	static final Logger logger = Logger.getLogger(UpgradeProcessor.class);

	private CyklotronHelper cyklotronHelper;

	private TomcatHelper tomcatHelper;

	private PostgresqlHelper psqlHelper;

	public UpgradeProcessor(UpgraderConfig config) {
		cyklotronHelper = new CyklotronHelper(config);
		tomcatHelper = new TomcatHelper(config);
		psqlHelper = new PostgresqlHelper(config);
	}

	/**
	 * Upgrade Cyklotron
	 * 
	 * @param from
	 *            CyklotronVerision
	 * @param to
	 *            CyklotronVerision
	 */
	public void upgrade(CyklotronVerision from, CyklotronVerision to) {
		CyklotronVerision current = from.equals(CyklotronVerision.UNDEFINED) ? CyklotronVerision.CYKLOTRON_2_8
				: from;
		to = to.equals(CyklotronVerision.UNDEFINED) ? CyklotronVerision.CYKLOTRON_2_28_4
				: to;
		while (current.lower(to)) {
			if (current.equals(CyklotronVerision.CYKLOTRON_2_8)) {
				upgrade_2_9_4();
				current = CyklotronVerision.CYKLOTRON_2_9_4;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_9_4)) {
				upgrade_2_10_1();
				current = CyklotronVerision.CYKLOTRON_2_10_1;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_10_1)) {
				upgrade_2_11();
				current = CyklotronVerision.CYKLOTRON_2_11;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_11)) {
				upgrade_2_12();
				current = CyklotronVerision.CYKLOTRON_2_12;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_12)) {
				upgrade_2_13_4();
				current = CyklotronVerision.CYKLOTRON_2_13_4;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_13_4)) {
				upgrade_2_14();
				current = CyklotronVerision.CYKLOTRON_2_14;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_14)) {
				upgrade_2_15_2();
				current = CyklotronVerision.CYKLOTRON_2_15_2;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_15_2)) {
				upgrade_2_16_5();
				current = CyklotronVerision.CYKLOTRON_2_16_5;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_16_5)) {
				upgrade_2_17_2();
				current = CyklotronVerision.CYKLOTRON_2_17_2;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_17_2)) {
				upgrade_2_18_1();
				current = CyklotronVerision.CYKLOTRON_2_18_1;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_18_1)) {
				upgrade_2_19_7();
				current = CyklotronVerision.CYKLOTRON_2_19_7;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_19_7)) {
				upgrade_2_21_2();
				current = CyklotronVerision.CYKLOTRON_2_21_2;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_21_2)) {
				upgrade_2_22_5();
				current = CyklotronVerision.CYKLOTRON_2_22_5;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_22_5)) {
				upgrade_2_23_0();
				current = CyklotronVerision.CYKLOTRON_2_23_0;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_23_0)) {
				upgrade_2_24_16();
				current = CyklotronVerision.CYKLOTRON_2_24_16;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_24_16)) {
				upgrade_2_25_0();
				current = CyklotronVerision.CYKLOTRON_2_25_0;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_25_0)) {
				upgrade_2_26_6();
				current = CyklotronVerision.CYKLOTRON_2_26_6;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_26_6)) {
				upgrade_2_27_6();
				current = CyklotronVerision.CYKLOTRON_2_27_6;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_27_6)) {
				upgrade_2_28_0();
				current = CyklotronVerision.CYKLOTRON_2_28_0;
				continue;
			} else if (current.equals(CyklotronVerision.CYKLOTRON_2_28_0)) {
				upgrade_2_28_4();
				current = CyklotronVerision.CYKLOTRON_2_28_4;
				continue;
			}
		}
	}

	/**
	 * Test connections with cyklotron, tomcat, psql
	 * 
	 * @param test
	 */
	public void test(String test) {
		try {
			logger.info("Start upgrade test");
			if ("cyklotron".equals(test) || "all".equals(test)) {
				logger.info("Login to cyklotron");
				cyklotronHelper.login();
				logger.info("Execute rml test select query");
				cyklotronHelper.executeRml("test.rml");
				logger.info("Logout from cyklotron");
				cyklotronHelper.logout();
			}
			if ("tomcat".equals(test) || "all".equals(test)) {
				logger.info("Stop tomcat");
				tomcatHelper.stop();
				logger.info("Clear webapps directory");
				tomcatHelper.deleteWebappRootDir();
				logger.info("Start tomcat");
				tomcatHelper.start();
			}
			if ("all".equals(test)) {
				logger.info("Login to cyklotron after tomcat restart");
				cyklotronHelper.login();
				logger.info("Execute rml test select query");
				cyklotronHelper.executeRml("test.rml");
				logger.info("Logout from cyklotron");
				cyklotronHelper.logout();
			}
			if ("postgres".equals(test) || "all".equals(test)) {
				logger.info("Connect to postgresql and execute test sql statements");
				psqlHelper.executeSQL("test.sql");
			}
			logger.info("Upgrade test finished");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.9.4
	 */
	public void upgrade_2_9_4() {
		try {
			logger.info("Start upgrade to Cyklotron 2.9.7");
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.9-b4.rml");
			cyklotronHelper.executeRml("upgrade_2.9-b5.rml");
			cyklotronHelper.executeRml("upgrade_2.9.1.rml");
			cyklotronHelper.logout();
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-webapp-2.9.7.war", "ledge.root",
					"true");
			tomcatHelper.start();
			cyklotronHelper.login();
			cyklotronHelper
					.executeFixMethod("fixes.Default?action=fixes.CYKLO622");
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.9.7 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.10.1
	 */
	public void upgrade_2_10_1() {
		try {
			logger.info("Start upgrade to Cyklotron 2.10.1");
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.10.1.war", "ledge.root",
					"false");
			tomcatHelper.start();
			logger.info("Upgrade to Cyklotron 2.10.1 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.11
	 */
	public void upgrade_2_11() {
		try {
			logger.info("Start upgrade to Cyklotron 2.11");
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper
					.setRootXml("cyklotron-2.11.war", "ledge.root", "false");
			tomcatHelper.start();
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.11.rml");
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.11 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.12
	 */
	public void upgrade_2_12() {
		try {
			logger.info("Start upgrade to Cyklotron 2.12");
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.12.rml");
			cyklotronHelper.logout();
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper
					.setRootXml("cyklotron-2.12.war", "ledge.root", "false");
			tomcatHelper.start();
			logger.info("Upgrade to Cyklotron 2.12 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.13.4
	 */
	public void upgrade_2_13_4() {
		try {
			logger.info("Start upgrade to Cyklotron 2.13.4");
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.13.rml");
			cyklotronHelper.logout();
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.13.4.war", "ledge.root",
					"false");
			tomcatHelper.start();
			cyklotronHelper.login();
			cyklotronHelper
					.executeFixMethod("fixes.ConvertCategoryQueryIdentifiers");
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.13.4 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.14
	 */
	public void upgrade_2_14() {
		try {
			logger.info("Start upgrade to Cyklotron 2.14");
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.14.rml");
			cyklotronHelper.logout();
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper
					.setRootXml("cyklotron-2.14.war", "ledge.root", "false");
			tomcatHelper.start();
			cyklotronHelper.login();
			cyklotronHelper
					.executeFixMethod("fixes.Default?action=fixes.DumpAddressFields");
			cyklotronHelper
					.executeFixMethod("fixes.Default?action=fixes.UpgradeDocumentMetadata214");
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.14 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.15.2
	 */
	public void upgrade_2_15_2() {
		try {
			logger.info("Start upgrade to Cyklotron 2.15.2");
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.15.rml");
			cyklotronHelper.logout();
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.15.2.war", "ledge.root",
					"false");
			tomcatHelper.start();
			cyklotronHelper.login();
			cyklotronHelper.executeFixMethod("fixes?action=fixes.CYKLO766");
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.15.2 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.16.5
	 */
	public void upgrade_2_16_5() {
		try {
			logger.info("Start upgrade to Cyklotron 2.16.5");
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.16.5.war", "ledge.root",
					"false");
			tomcatHelper.start();
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.16.rml");
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.16.5 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.17.2
	 */
	public void upgrade_2_17_2() {
		try {
			logger.info("Start upgrade to Cyklotron 2.17.2");
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.17.2.war", "ledge.root",
					"false");
			tomcatHelper.start();
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.17.rml");
			cyklotronHelper
					.executeFixMethod("fixes?action=fixes.ReorganizePollsResource");
			logger.info("Rebuilding all documents structure. It can take up to 30 minutes.");
			cyklotronHelper.executeFixMethod("fixes?action=fixes.CYKLO789");
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.17.2 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.18.1
	 */
	public void upgrade_2_18_1() {
		try {
			logger.info("Start upgrade to Cyklotron 2.18.1");
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.18.rml");
			cyklotronHelper.logout();
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.18.1.war", "ledge.root",
					"false");
			tomcatHelper.start();
			logger.info("Upgrade to Cyklotron 2.18.1 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.19.7
	 */
	public void upgrade_2_19_7() {
		try {
			logger.info("Start upgrade to Cyklotron 2.19.7");
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.19.7.war", "ledge.root",
					"false");
			tomcatHelper.start();
			logger.info("Upgrade to Cyklotron 2.19.7 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.21.2
	 */
	public void upgrade_2_21_2() {
		try {
			logger.info("Start upgrade to Cyklotron 2.21.2");
			tomcatHelper.stop();
			cyklotronHelper.setConfig("2_21_2",
					"org.objectledge.cache.CacheFactory.xml");
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.21.2.war", "ledge.root",
					"false");
			tomcatHelper.start();
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.20.rml");
			cyklotronHelper.executeRml("upgrade_2.21.rml");
			cyklotronHelper.executeFixMethod("fixes?action=fixes.CYKLO834");
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.21.2 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.22.5
	 */
	public void upgrade_2_22_5() {
		try {
			logger.info("Start upgrade to Cyklotron 2.22.5");
			tomcatHelper.stop();
			psqlHelper.executeSQL("upgrade_2.22.5.sql");
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.22.5.war", "root", "false");
			tomcatHelper.start();
			logger.info("Upgrade to Cyklotron 2.22.5 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.23.0
	 */
	public void upgrade_2_23_0() {
		try {
			logger.info("Start upgrade to Cyklotron 2.23.0");
			tomcatHelper.stop();
			psqlHelper.executeSQL("upgrade_2.23.0.sql");
			cyklotronHelper.setConfig("2_23_0",
					"org.objectledge.btm.BitronixTransactionManager.xml");
			cyklotronHelper
					.deleteConfig("org.objectledge.database.XaPoolDataSource.xml");
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.23.0.war", "root", "false");
			tomcatHelper.start();
			logger.info("Upgrade to Cyklotron 2.23.0 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.24.16
	 */
	public void upgrade_2_24_16() {
		try {
			logger.info("Start upgrade to Cyklotron 2.24.16");
			tomcatHelper.stop();
			psqlHelper.executeSQL("upgrade_2.24.16.sql");
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.24.16.war", "root", "false");
			tomcatHelper.startAndWait(25);
			logger.info("Upgrade to Cyklotron 2.24.16 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.25.0
	 */
	public void upgrade_2_25_0() {
		try {
			logger.info("Start upgrade to Cyklotron 2.25.0");
			tomcatHelper.stop();
			psqlHelper.executeSQL("upgrade_2.25.0.sql");
			cyklotronHelper.backupSearchIndexes();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.25.0.war", "root", "false");
			tomcatHelper.startAndWait(25);
			cyklotronHelper.login();
			logger.info("Reindexing all search. It can take up to 10 minutes");
			cyklotronHelper.executeReindexSearchMethod();
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.25.0 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.26.6
	 */
	public void upgrade_2_26_6() {
		try {
			logger.info("Start upgrade to Cyklotron 2.26.6");
			tomcatHelper.stop();
			tomcatHelper.startAndWait(25);
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.26-1.rml");
			cyklotronHelper.logout();
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.26.6.war", "root", "false");
			tomcatHelper.startAndWait(25);
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.26-2.rml");
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.26.6 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.27.6
	 */
	public void upgrade_2_27_6() {
		try {
			logger.info("Start upgrade to Cyklotron 2.27.6");
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.27.6.war", "root", "false");
			tomcatHelper.startAndWait(25);
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.27.rml");
			cyklotronHelper.logout();
			logger.info("Upgrade to Cyklotron 2.27.6 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.28.0
	 */
	public void upgrade_2_28_0() {
		try {
			logger.info("Start upgrade to Cyklotron 2.28.0");
			cyklotronHelper.login();
			cyklotronHelper.executeRml("upgrade_2.28.0.rml");
			cyklotronHelper.logout();
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.28.0.war", "root", "false");
			tomcatHelper.startAndWait(25);
			logger.info("Upgrade to Cyklotron 2.28.0 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Upgrade to Cyklotron 2.28.4
	 */
	public void upgrade_2_28_4() {
		try {
			logger.info("Start upgrade to Cyklotron 2.28.4");
			tomcatHelper.stop();
			tomcatHelper.deleteWebappRootDir();
			tomcatHelper.setRootXml("cyklotron-2.28.4.war", "root", "false");
			tomcatHelper.startAndWait(25);
			logger.info("Upgrade to Cyklotron 2.28.4 successful");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
