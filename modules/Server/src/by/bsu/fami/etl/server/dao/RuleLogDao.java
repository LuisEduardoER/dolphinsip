package by.bsu.fami.etl.server.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Singleton;

import by.bsu.fami.etl.server.bean.RuleLogItem;

@Singleton
public class RuleLogDao implements IRuleLogDao {

	protected final static Log LOGGER = LogFactory.getLog(RuleLogDao.class);

	protected String jdbcDriver = "org.apache.derby.jdbc.EmbeddedDriver";

	protected String url = "jdbc:derby:scheduler_db;";

	protected String username = "";

	protected String password = "";

	protected String table = "RuleLog";

	protected Connection connection;

	private RuleLogDao() {
		try {
			Class.forName(jdbcDriver);
			connection = DriverManager.getConnection(url);
			LOGGER.info("Success init RuleLogDao");
		} catch (ClassNotFoundException e) {
			LOGGER.error("Couldn't load jdbc driver " + jdbcDriver, e);
		} catch (SQLException e) {
			LOGGER.error("Couldn't connect to database " + url, e);
		}
	}

	public List<RuleLogItem> baseRules(int first, int count,
			String sortProperty, boolean sortAsc) {
		return loadRules(BASE_RULE_TYPE, first, count, sortProperty, sortAsc);
	}

	public List<RuleLogItem> checkRules(int first, int count,
			String sortProperty, boolean sortAsc) {
		return loadRules(CHECK_RULE_TYPE, first, count, sortProperty, sortAsc);
	}

	protected List<RuleLogItem> loadRules(String ruleType, int first,
			int count, String sortProperty, boolean sortAsc) {
		String ruleQuery = "SELECT ruleName, event_date, "
				+ "jobName, metadataName," + "status, rule_type, failure FROM "
				+ table + " WHERE rule_type = '" + ruleType + "' ";
		if (StringUtils.isNotEmpty(sortProperty)) {
			ruleQuery += " ORDER BY " + sortProperty
					+ (sortAsc ? " ASC " : " DESC ");
		}
		LOGGER.debug(ruleQuery);
		List<RuleLogItem> items = new ArrayList<RuleLogItem>();
		if (connection != null) {
			Statement smtp = null;
			try {
				smtp = connection.createStatement();
				ResultSet rs = smtp.executeQuery(ruleQuery);
				while (rs.next()) {
					RuleLogItem logItem = new RuleLogItem();
					logItem.setRuleName(rs.getString(1));
					logItem.setDate(rs.getDate(2));
					logItem.setJobName(rs.getString(3));
					logItem.setMetadataName(rs.getString(4));
					logItem.setStatus(rs.getString(5));
					logItem.setType(rs.getString(6));
					logItem.setFailure(rs.getString(7));
					items.add(logItem);
				}
			} catch (SQLException e) {
				LOGGER.error(e);
			} finally {
				if (smtp != null) {
					try {
						smtp.close();
					} catch (SQLException e) {
						LOGGER.warn(e);
					}
				}
			}
		}
		return items;
	}

	public int baseRulesSize() {
		return ruleSize(BASE_RULE_TYPE);
	}

	public int checkRulesSize() {
		return ruleSize(CHECK_RULE_TYPE);
	}

	private int ruleSize(String ruleType) {
		String ruleQuery = "SELECT COUNT(*) FROM " + table
				+ " WHERE rule_type = '" + ruleType + "'";
		LOGGER.debug(ruleQuery);
		if (connection != null) {
			Statement smtp = null;
			try {
				smtp = connection.createStatement();
				ResultSet rs = smtp.executeQuery(ruleQuery);
				rs.next();
				return rs.getInt(1);
			} catch (SQLException e) {
				LOGGER.error(e);
			} finally {
				if (smtp != null) {
					try {
						smtp.close();
					} catch (SQLException e) {
						LOGGER.warn(e);
					}
				}
			}
		}
		return 0;
	}

}
