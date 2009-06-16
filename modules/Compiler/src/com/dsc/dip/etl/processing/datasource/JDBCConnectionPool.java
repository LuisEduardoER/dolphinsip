package com.dsc.dip.etl.processing.datasource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

//class ConnectionReaper extends Thread {
//
//	private final static Logger LOGGER = Logger
//			.getLogger(ConnectionReaper.class);
//
//	private JDBCConnectionPool pool;
//
//	private final long delay = 30000;
//
//	protected boolean isRun = true;
//
//	ConnectionReaper(JDBCConnectionPool pool) {
//		this.pool = pool;
//	}
//
//	public void run() {
//		while (isRun) {
//			try {
//				sleep(delay);
//			} catch (InterruptedException e) {
//			}
//			LOGGER.debug("Start reap connections...");
//			pool.reapConnections();
//			LOGGER.debug("Reap connections ended.");
//		}
//	}
//
//	public boolean isRun() {
//		return isRun;
//	}
//
//	public void setRun(boolean isRun) {
//		this.isRun = isRun;
//	}
//
//}

public class JDBCConnectionPool {

	private final static Logger LOGGER = Logger
			.getLogger(JDBCConnectionPool.class);

	private List<JDBCConnection> connections;

	private String url, user, password;

	final private long timeout = 6000;

	// private ConnectionReaper reaper;

	public JDBCConnectionPool(String url, String user, String password,
			int poolsize) {
		this.url = url;
		this.user = user;
		this.password = password;
		connections = new ArrayList<JDBCConnection>(poolsize);
		// reaper = new ConnectionReaper(this);
		// reaper.start();
	}

	// public synchronized void reapConnections() {
	//
	// LOGGER.info("Reap connections");
	//
	// long stale = System.currentTimeMillis() - timeout;
	// Iterator<JDBCConnection> connlist = connections.iterator();
	//
	// while ((connlist != null) && (connlist.hasNext())) {
	// JDBCConnection conn = connlist.next();
	//
	// if ((conn.inUse()) && (stale > conn.getLastUse())
	// && (!conn.validate())) {
	// }
	// }
	// }

	public synchronized void closeConnections() {
		new ConnectionCloser().start();
	}

	public synchronized Connection getConnection() throws SQLException {

		JDBCConnection c;
		for (int i = 0; i < connections.size(); i++) {
			c = connections.get(i);
			if (c.lease()) {
				LOGGER.info("Get exist connection from pool" + c);
				return c;
			}
		}

		Connection conn;
		if (StringUtils.isNotEmpty(user)) {
			conn = DriverManager.getConnection(url, user, password);
		} else {
			conn = DriverManager.getConnection(url);
		}
		c = new JDBCConnection(conn, this);
		c.lease();
		connections.add(c);
		LOGGER.info("Create new connection and add it to pool " + c);
		LOGGER.debug(connections.size() + " connections in pool");
		return c;
	}

	public synchronized void returnConnection(Connection conn) {
		LOGGER.info("Connection " + conn + " is returned");
		if (conn instanceof JDBCConnection) {
			((JDBCConnection) conn).expireLease();
		} else {
			try {
				conn.close();
			} catch (SQLException e) {
				LOGGER.warn("Couldn't close connection by return", e);
			}
		}
	}

	private class ConnectionCloser extends Thread {

		private final long delay = 300;

		protected boolean isRun = true;

		protected ConnectionCloser() {
		}

		public void run() {
			while (isRun) {
				LOGGER.debug("Start close all connections in thread....");

				synchronized (connections) {
					LOGGER.debug("Pool has " + connections.size()
							+ " connections");
					Iterator<JDBCConnection> connlist = connections.iterator();
					long stale = System.currentTimeMillis() - timeout;
					List<JDBCConnection> forRemove = new ArrayList<JDBCConnection>();
					while ((connlist != null) && (connlist.hasNext())) {
						JDBCConnection conn = connlist.next();
						if (!conn.inUse() || stale > conn.getLastUse()) {
							try {
								conn.close();
							} catch (SQLException e) {
							}
							forRemove.add(conn);
						}
					}
					if (connections.size() == forRemove.size()) {
						isRun = false;
						connections.clear();
						LOGGER.info("All connections in pool are closed");
					} else {
						connections.removeAll(forRemove);
						LOGGER.debug(connections.size()
								+ " connections in pool are used, sleep on "
								+ delay + " ms");
						try {
							sleep(delay);
						} catch (InterruptedException e) {
						}
					}
				}
			}
		}

	}
}
