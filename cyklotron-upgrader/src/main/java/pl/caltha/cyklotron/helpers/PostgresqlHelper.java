package pl.caltha.cyklotron.helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import pl.caltha.cyklotron.upgrader.UpgraderConfig;

/**
 * Posgresql class
 * 
 * @author lukasz
 *
 */
public class PostgresqlHelper {

	static final Logger logger = Logger.getLogger(PostgresqlHelper.class);

	private String host;

	private String port;

	private String database;

	private String user;

	private String password;

	private String connString = "jdbc:postgresql://{host}{port}/{database}";

	public PostgresqlHelper(UpgraderConfig config) {
		host = config.getPostgresqlHost();
		port = !config.getPostgresqlPort().isEmpty() ? ":"
				+ config.getPostgresqlPort() : "";
		database = config.getPostgresqlDatabase();
		user = config.getPostgresqlUser();
		password = config.getPostgresqlPassword();
		connString = connString.replace("{host}", host).replace("{port}", port)
				.replace("{database}", database);
	}

	/**
	 * Execute SQLs from resource file. File must be located under 'files/sql'
	 * 
	 * @param filename
	 */
	public void executeSQL(String filename) {
		String statements = ResourceHelper.getResourceFileContent("files/sql/"
				+ filename);
		if (statements != null) {
			Connection conn = getConnection();
			executeSQL(conn, statements);
			closeConnection(conn);
			logger.debug("execute sql statement:" + statements);
		} else {
			logger.debug("file 'files/sql/" + filename + "' not found");
		}
	}

	/**
	 * execute statement
	 * 
	 * @param conn
	 * @param statement
	 */
	private void executeSQL(Connection conn, String statements) {
		try {
			Statement stmt = conn.createStatement();
			conn.setAutoCommit(false);
			stmt.execute(statements);
			stmt.close();
			conn.commit();
			logger.debug("Postgresql statement executed: " + statements);
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException ex) {
				logger.error(ex.getMessage(), ex);
			}
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Create connection
	 */
	private Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(connString, user, password);
			return conn;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return conn;
		}
	}

	/**
	 * Create connection
	 */
	private void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getDatabase() {
		return database;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
}
