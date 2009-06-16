package com.dsc.dip.etl.processing.component.reader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dsc.dip.etl.processing.component.ComponentInitException;
import com.dsc.dip.etl.processing.datasource.DataSourceException;
import com.dsc.dip.etl.processing.datasource.JdbcDataSource;
import com.dsc.dip.etl.processing.document.Document;
import com.dsc.dip.etl.processing.document.Field;


public class JdbcReader extends DataReader {

	private final static Logger LOGGER = Logger.getLogger(JdbcReader.class);

	protected Statement stmt;

	protected ResultSet rs;

	protected Field[] fields;

	protected Connection connection;

	public synchronized boolean init() throws ComponentInitException {
		boolean init = super.init();
		if (init) {
			if (dataSource instanceof JdbcDataSource) {
				try {
					LOGGER.debug("Start init jdc reader....");
					connection = ((JdbcDataSource) dataSource).getConnection();
					stmt = connection.createStatement();
					if (StringUtils.isNotEmpty(map)) {
						rs = stmt.executeQuery(map);
						ResultSetMetaData rsmd = rs.getMetaData();
						int columnCount = rsmd.getColumnCount();
						fields = new Field[columnCount];
						for (int i = 0; i < columnCount; i++) {
							fields[i] = new Field(rsmd.getColumnName(i + 1),
									rsmd.getColumnTypeName(i + 1));
						}
						LOGGER.info("Success init jdbc reader component");
						LOGGER.info("Load " + rs.getFetchSize() + " documents");
					} else {
						String message = "Map for jdbc reader component "
								+ name + " must be non empty";
						LOGGER.error(message);
						throw new ComponentInitException(message);
					}
				} catch (SQLException e) {
					String message = "Couldn't map data for jdbc reader component "
							+ name;
					LOGGER.error(message, e);
					throw new ComponentInitException(message, e);
				}
			} else {
				String message = "Datasource " + dataSource.getName()
						+ " for jdbc reader component " + name
						+ " isn't JdbcDataSource";
				LOGGER.error(message);
				throw new ComponentInitException(message);
			}
		}
		return init;
	}

	public boolean hasDocument() {
		if (rs != null) {
			synchronized (rs) {
				try {
					return rs.next();
				} catch (SQLException e) {
					LOGGER.warn(
							"Couldn't check documents for jdbc reader component "
									+ name, e);
				}
			}
		}
		return false;
	}

	public Document readDocument(Document document) {
		synchronized (rs) {
			if (fields != null) {
				for (int i = 0; i < fields.length; i++) {
					String value;
					try {
						value = rs.getString(i + 1);
						fields[i].setValue(value);
						document.addField(fields[i]);
					} catch (SQLException e) {
						LOGGER.warn(
								"Couldn't load next document for jdbc reader component "
										+ name, e);
					}
				}
				initSeqNumber(document);
			}
		}
		return document;
	}

	protected void initSeqNumber(Document document) {
		try {
			if (StringUtils.isEmpty(document.getSeqNumber())) {
				document.setSeqNumber(Integer.toString(rs.getRow()));
			} else {
				document.setSeqNumber(document.getSeqNumber() + "."
						+ Integer.toString(rs.getRow()));
			}
		} catch (SQLException ex) {
			LOGGER.warn(ex);
		}
	}

	public synchronized boolean complete() throws ComponentInitException {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (connection != null) {
				((JdbcDataSource) dataSource).releaseConnect(connection);
			}
		} catch (SQLException e) {
			LOGGER.warn("Couldn't release resource for jdbc reader component "
					+ name, e);
			return false;
		} catch (DataSourceException e) {
			LOGGER.warn("Couldn't release resource for jdbc reader component "
					+ name, e);
			return false;
		}
		return super.complete();
	}

}
