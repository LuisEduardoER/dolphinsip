package by.bsu.fami.etl.processing.component;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import by.bsu.fami.etl.processing.datasource.DataSourceException;
import by.bsu.fami.etl.processing.datasource.JdbcDataSource;

public class BaseJdbcComponentTest {

	private static final String HSQLDB_JDBC_USER = "";

	private static final String HSQLDB_JDBC_URL = "jdbc:derby:db;create=true";

	private static final String HSQLDB_JDBC_DRIVER = "org.apache.derby.jdbc.EmbeddedDriver";

	private final static Logger LOGGER = Logger
			.getLogger(BaseJdbcComponentTest.class);

	protected JdbcDataSource ds;

	public void before() throws ComponentInitException {
		BasicConfigurator.configure();
		ds = new JdbcDataSource();
		ds.setName("jdbcDataSource");
		ds.addProperty(JdbcDataSource.PROPERTY_DRIVER_CLASS, HSQLDB_JDBC_DRIVER);
		ds.addProperty(JdbcDataSource.PROPERTY_DATBASE_URL, HSQLDB_JDBC_URL);
		ds.addProperty(JdbcDataSource.PROPERTY_DATBASE_USER, HSQLDB_JDBC_USER);
		ds.addProperty(JdbcDataSource.PROPERTY_DATBASE_PASSWORD, "");
		try {
			ds.initConnect();
			Connection connection = ds.getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate("DROP TABLE Test IF EXISTS;" +
					"CREATE TABLE Test(id INT IDENTITY, key INT, data VARCHAR(10));");
			statement.executeUpdate("INSERT INTO Test (id, key, data) VALUES (1, 1, 'Test1');" +
					"INSERT INTO Test (id, key, data) VALUES (2, 2, 'Test2');" +
					"INSERT INTO Test (id, key, data) VALUES (3, 3, 'Test3');");
			LOGGER.info("Success init and fill table 'Test'");
			statement.close();
			ds.releaseConnect();
		} catch (SQLException e) {
			LOGGER.error(e);
		} catch (DataSourceException e) {
			LOGGER.error(e);
		}
		LOGGER.info("Success init jdbc datasource");
	}

}
