package com.dsc.dip.etl.processing.component.writer;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.dsc.dip.etl.processing.component.ComponentInitException;
import com.dsc.dip.etl.processing.datasource.DataSourceException;
import com.dsc.dip.etl.processing.datasource.JdbcDataSource;
import com.dsc.dip.etl.processing.document.Document;


public class JdbcWriter extends DataWriter {

	private final static Logger LOGGER = Logger.getLogger(JdbcWriter.class);

	protected Statement stmt;

	protected StatementBuilder sb;

	protected Connection connection;

	public synchronized boolean init() throws ComponentInitException {
		boolean init = super.init();
		if (init) {
			if (dataSource instanceof JdbcDataSource) {
				try {
					connection = ((JdbcDataSource) dataSource).getConnection();
					stmt = connection.createStatement();
					sb = StatementBuilder.newInstance();
				} catch (SQLException e) {
					String message = "Couldn't create statement for jdrc writers component "
							+ name;
					LOGGER.error(message, e);
					throw new ComponentInitException(message, e);
				}
			} else {
				String message = "Datasource " + dataSource.getName()
						+ " for jdbc writer component " + name
						+ " isn't JdbcDataSource";
				LOGGER.error(message);
				throw new ComponentInitException(message);
			}
		}
		return init;
	}

	public synchronized void writeDocuments() {
		if (stmt != null && sb != null) {
			try {
				stmt.clearBatch();
				LOGGER.debug("Write batch documents: " + documents);
				for (Document doc : documents) {
					String buildStatement = sb.buildStatement(rootId, id, keys,
							doc);
					LOGGER.debug("Add to batch: " + buildStatement);
					stmt.addBatch(buildStatement);
				}
				stmt.executeBatch();
				stmt.clearBatch();
			} catch (SQLException e) {
				LOGGER.error("Documents batch didn't write,"
						+ " problem with access to database", e);
			} catch (StatementException e) {
				LOGGER.error("Documents batch didn't write,"
						+ " problem with create statement for document ", e);
			}
		}
	}

	public synchronized boolean complete() throws ComponentInitException {
		boolean complete = super.complete();
		try {
			if (stmt != null) {
				stmt.close();
			}
			if (connection != null) {
				((JdbcDataSource) dataSource).releaseConnect(connection);
			}
		} catch (SQLException e) {
			LOGGER.warn("Couldn't release resource for jdbc writer component "
					+ name, e);
			return false;
		} catch (DataSourceException e) {
			LOGGER.warn("Couldn't release resource for jdbc writer component "
					+ name, e);
			return false;
		}
		return complete;
	}

}
