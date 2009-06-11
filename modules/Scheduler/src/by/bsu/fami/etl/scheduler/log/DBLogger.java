package by.bsu.fami.etl.scheduler.log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

public class DBLogger implements ILogger {

	public final static String SCHEDULE_LOGGER_DB_DRIVER = "scheduler.logger.db.driver";

	public final static String SCHEDULE_LOGGER_DB_URL = "scheduler.logger.db.url";

	public final static String SCHEDULE_LOGGER_DB_USER = "scheduler.logger.db.user";

	public final static String SCHEDULE_LOGGER_DB_PASSWORD = "scheduler.logger.db.password";

	public final static String SCHEDULE_LOGGER_DB_TABEL = "scheduler.logger.db.table";

	protected String prepareQuery;

	protected Connection connection;

	protected PreparedStatement statement;

	public void init(Properties config) throws LoggerException {
		if (config != null) {
			String jdbcDriver = config.getProperty(SCHEDULE_LOGGER_DB_DRIVER);
			if (StringUtils.isEmpty(jdbcDriver)) {
				throw new LoggerException("Property "
						+ SCHEDULE_LOGGER_DB_DRIVER + "for DBLogger must be "
						+ "initialize by jdbc driver class name");
			}
			String url = config.getProperty(SCHEDULE_LOGGER_DB_URL), user = config
					.getProperty(SCHEDULE_LOGGER_DB_USER), password = config
					.getProperty(SCHEDULE_LOGGER_DB_PASSWORD);
			if (StringUtils.isEmpty(url)) {
				throw new LoggerException("Property " + SCHEDULE_LOGGER_DB_URL
						+ "for DBLogger must be "
						+ "initialize by url to database class name");
			}
			String table = config.getProperty(SCHEDULE_LOGGER_DB_TABEL);
			if (StringUtils.isEmpty(table)) {
				throw new LoggerException("Property "
						+ SCHEDULE_LOGGER_DB_TABEL + "for DBLogger must be "
						+ "initialize by database table");
			}
			try {
				Class.forName(jdbcDriver);
				if (StringUtils.isNotEmpty(user)) {
					connection = DriverManager.getConnection(url, user,
							password);
				} else {
					connection = DriverManager.getConnection(url);
				}
				prepareQuery = "INSERT INTO " + table
						+ " (ruleName, event_date, jobName, metadataName, "
						+ "status, rule_type, failure) VALUES (?, ?, ?, ?, ?, ?, ?)";
				statement = connection.prepareStatement(prepareQuery);
			} catch (ClassNotFoundException e) {
				throw new LoggerException("Couldn't find jdb driver classs "
						+ jdbcDriver + " for DBLogger");
			} catch (SQLException e) {
				throw new LoggerException("Couldn't connect to database " + url
						+ " for DBLogger");
			}
		}
	}

	public void logRule(String ruleName, Date date, String jobName,
			String metadataName, String status, String type, String failure)
			throws LoggerException {
		if (statement != null) {
			try {
				statement.setString(1, StringEscapeUtils.escapeSql(ruleName));
				statement.setDate(2, new java.sql.Date(date.getTime()));
				statement.setString(3, StringEscapeUtils.escapeSql(jobName));
				statement.setString(4, StringEscapeUtils
						.escapeSql(metadataName));
				statement.setString(5, StringEscapeUtils.escapeSql(status));
				statement.setString(6, StringEscapeUtils.escapeSql(type));
				statement.setString(7, StringEscapeUtils.escapeSql(failure));
				statement.executeUpdate();
			} catch (SQLException e) {
				throw new LoggerException(
						"Couldn't log rule " + e.getMessage(), e);
			}
		}
	}

}
