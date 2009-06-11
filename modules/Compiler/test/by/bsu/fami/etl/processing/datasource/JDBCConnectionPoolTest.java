package by.bsu.fami.etl.processing.datasource;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JDBCConnectionPoolTest {

	protected JDBCConnectionPool pool;

	@Before
	public void before() throws ClassNotFoundException {
		BasicConfigurator.configure();
		Class.forName("org.hsqldb.jdbcDriver");
		pool = new JDBCConnectionPool("jdbc:hsqldb:file:db/test", "SA", "", 5);
	}

	@Test
	public void oneConnection() throws SQLException {
		Connection conn = pool.getConnection();
		pool.returnConnection(conn);
		pool.closeConnections();
	}

	@Test
	public void twoConnections() throws SQLException {
		Connection conn1 = pool.getConnection();
		Connection conn2 = pool.getConnection();
		pool.returnConnection(conn1);
		pool.returnConnection(conn2);
		pool.closeConnections();
	}

	@Test
	public void oneConnectionNonClose() throws SQLException {
		Connection conn = pool.getConnection();
		pool.closeConnections();
		pool.returnConnection(conn);
	}

	@Test
	public void twoConnectionsNonClose() throws SQLException {
		Connection conn1 = pool.getConnection();
		Connection conn2 = pool.getConnection();
		pool.returnConnection(conn2);
		pool.closeConnections();
		pool.returnConnection(conn1);
	}

	@After
	public void after() {
		pool.closeConnections();
	}

}
