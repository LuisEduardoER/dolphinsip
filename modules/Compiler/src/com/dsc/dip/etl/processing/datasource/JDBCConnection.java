package com.dsc.dip.etl.processing.datasource;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

public class JDBCConnection implements Connection {

	private JDBCConnectionPool pool;
	private Connection conn;
	private boolean inuse;
	private long timestamp;

	public JDBCConnection(Connection conn, JDBCConnectionPool pool) {
		this.conn = conn;
		this.pool = pool;
		this.inuse = false;
		this.timestamp = 0;
	}

	public synchronized boolean lease() {
		if (inuse) {
			return false;
		} else {
			inuse = true;
			timestamp = System.currentTimeMillis();
			return true;
		}
	}

	public boolean validate() {
		try {
			conn.getMetaData();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean inUse() {
		return inuse;
	}

	public long getLastUse() {
		return timestamp;
	}

	public void close() throws SQLException {
		pool.returnConnection(this);
	}

	protected void expireLease() {
		inuse = false;
	}

	protected Connection getConnection() {
		return conn;
	}

	public PreparedStatement prepareStatement(String sql) throws SQLException {
		return conn.prepareStatement(sql);
	}

	public CallableStatement prepareCall(String sql) throws SQLException {
		return conn.prepareCall(sql);
	}

	public Statement createStatement() throws SQLException {
		return conn.createStatement();
	}

	public String nativeSQL(String sql) throws SQLException {
		return conn.nativeSQL(sql);
	}

	public void setAutoCommit(boolean autoCommit) throws SQLException {
		conn.setAutoCommit(autoCommit);
	}

	public boolean getAutoCommit() throws SQLException {
		return conn.getAutoCommit();
	}

	public void commit() throws SQLException {
		conn.commit();
	}

	public void rollback() throws SQLException {
		conn.rollback();
	}

	public boolean isClosed() throws SQLException {
		return conn.isClosed();
	}

	public DatabaseMetaData getMetaData() throws SQLException {
		return conn.getMetaData();
	}

	public void setReadOnly(boolean readOnly) throws SQLException {
		conn.setReadOnly(readOnly);
	}

	public boolean isReadOnly() throws SQLException {
		return conn.isReadOnly();
	}

	public void setCatalog(String catalog) throws SQLException {
		conn.setCatalog(catalog);
	}

	public String getCatalog() throws SQLException {
		return conn.getCatalog();
	}

	public void setTransactionIsolation(int level) throws SQLException {
		conn.setTransactionIsolation(level);
	}

	public int getTransactionIsolation() throws SQLException {
		return conn.getTransactionIsolation();
	}

	public SQLWarning getWarnings() throws SQLException {
		return conn.getWarnings();
	}

	public void clearWarnings() throws SQLException {
		conn.clearWarnings();
	}

	public Array createArrayOf(String typeName, Object[] elements)
			throws SQLException {
		return conn.createArrayOf(typeName, elements);
	}

	public Blob createBlob() throws SQLException {
		return conn.createBlob();
	}

	public Clob createClob() throws SQLException {
		return conn.createClob();
	}

	public NClob createNClob() throws SQLException {
		return conn.createNClob();
	}

	public SQLXML createSQLXML() throws SQLException {
		return conn.createSQLXML();
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency)
			throws SQLException {
		return conn.createStatement();
	}

	public Statement createStatement(int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return conn.createStatement();
	}

	public Struct createStruct(String typeName, Object[] attributes)
			throws SQLException {
		return conn.createStruct(typeName, attributes);
	}

	public Properties getClientInfo() throws SQLException {
		return conn.getClientInfo();
	}

	public String getClientInfo(String name) throws SQLException {
		return conn.getClientInfo(name);
	}

	public int getHoldability() throws SQLException {
		return conn.getHoldability();
	}

	public Map<String, Class<?>> getTypeMap() throws SQLException {
		return conn.getTypeMap();
	}

	public boolean isValid(int timeout) throws SQLException {
		return conn.isValid(timeout);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
	}

	public CallableStatement prepareCall(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return conn.prepareCall(sql, resultSetType, resultSetConcurrency,
				resultSetHoldability);
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys)
			throws SQLException {
		return conn.prepareStatement(sql, autoGeneratedKeys);
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes)
			throws SQLException {
		return conn.prepareStatement(sql, columnIndexes);
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames)
			throws SQLException {
		return conn.prepareStatement(sql, columnNames);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency) throws SQLException {
		return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType,
			int resultSetConcurrency, int resultSetHoldability)
			throws SQLException {
		return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		conn.releaseSavepoint(savepoint);
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		conn.rollback(savepoint);
	}

	public void setClientInfo(Properties properties)
			throws SQLClientInfoException {
		conn.setClientInfo(properties);
	}

	public void setClientInfo(String name, String value)
			throws SQLClientInfoException {
		conn.setClientInfo(name, value);
	}

	public void setHoldability(int holdability) throws SQLException {
		conn.setHoldability(holdability);
	}

	public Savepoint setSavepoint() throws SQLException {
		return conn.setSavepoint();
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return conn.setSavepoint(name);
	}

	public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
		conn.setTypeMap(map);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return conn.isWrapperFor(iface);
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return conn.unwrap(iface);
	}
}
