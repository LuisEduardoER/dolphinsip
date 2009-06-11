package by.bsu.fami.etl.processing.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class JdbcDataSource extends DataSource {

	private final static Logger LOGGER = Logger.getLogger(JdbcDataSource.class);

	public final static String PROPERTY_DRIVER_CLASS = "DriverClass";

	public final static String PROPERTY_DATBASE_URL = "DatabaseUrl";

	public final static String PROPERTY_DATBASE_USER = "DatabaseUser";

	public final static String PROPERTY_DATBASE_PASSWORD = "DatabasePassword";

	public final static String PROPERTY_POOL_SIZE = "PoolSize";

	protected JDBCConnectionPool pool;

	public synchronized boolean initConnect() throws DataSourceException {
		LOGGER.debug("Start init jdbc datasource " + name + " ......");
		String driverClass, url;
		if (StringUtils
				.isNotEmpty(driverClass = getPropertyValue(PROPERTY_DRIVER_CLASS))
				&& StringUtils
						.isNotEmpty(url = getPropertyValue(PROPERTY_DATBASE_URL))) {
			try {
				LOGGER.debug("Try init driver class...");
				Class.forName(driverClass);
				LOGGER.debug("Try create connection...");
				String username = "";
//				if (StringUtils
//						.isNotEmpty(username = getPropertyValue(PROPERTY_DATBASE_USER))) {
					int poolsize = 5;
					if (StringUtils
							.isNumeric(getPropertyValue(PROPERTY_POOL_SIZE))) {
						poolsize = Integer
								.parseInt(getPropertyValue(PROPERTY_POOL_SIZE));
					}
					pool = new JDBCConnectionPool(url, username,
							getPropertyValue(PROPERTY_DATBASE_PASSWORD),
							poolsize);
//				}
				LOGGER.info("Success init jdbc datasource " + name);
				return true;
			} catch (ClassNotFoundException e) {
				LOGGER.error("Couldn't find jdbc driver class " + driverClass,
						e);
				throw new DataSourceException(
						"Couldn't find jdbc driver class " + driverClass, e);
			}
		}
		return false;
	}

	public boolean releaseConnect(Connection conn) throws DataSourceException {
		if (pool != null) {
			synchronized (pool) {
				pool.returnConnection(conn);
			}
			LOGGER.info("Success release jdbc datasource " + name);
			return true;
		}
		return false;
	}

	public synchronized boolean releaseConnect() throws DataSourceException {
		return true;
	}

	public boolean releasePool() {
		if (pool != null) {
			synchronized (pool) {
				pool.closeConnections();
			}
			LOGGER.info("Success release jdbc connection pool for datasource "
					+ name);
			return true;
		}
		return false;
	}

	public Connection getConnection() throws SQLException {
		synchronized (pool) {
			return pool.getConnection();
		}
	}

}
