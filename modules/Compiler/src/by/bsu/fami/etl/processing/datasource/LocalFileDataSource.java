package by.bsu.fami.etl.processing.datasource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class LocalFileDataSource extends DataSource {

	private final static Logger LOGGER = Logger
			.getLogger(LocalFileDataSource.class);

	public final static String PROPERTY_FILE_NAME = "FileName";

	protected String fileName;

	public synchronized boolean initConnect() throws DataSourceException {
		if (StringUtils
				.isNotEmpty(fileName = getPropertyValue(PROPERTY_FILE_NAME))) {
			LOGGER.info("Success init local file datasource " + name);
			return true;
		} else {
			String message = "DataSource 'FileName' property must be initialize";
			LOGGER.error(message);
			throw new DataSourceException(message);
		}
	}

	public synchronized boolean releaseConnect() throws DataSourceException {
		LOGGER.info("Success release local file datasource " + name);
		return true;
	}

	public FileInputStream getReadDataSource() throws DataSourceException {
		try {
			return new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			LOGGER.error("Failed  init local file datasource " + name, e);
			throw new DataSourceException("Failed  init local file datasource "
					+ name, e);
		}
	}

	public FileOutputStream getWriteDataSource() throws DataSourceException {
		try {
			return new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			LOGGER.error("Failed  init local file datasource " + name, e);
			throw new DataSourceException("Failed  init local file datasource "
					+ name, e);
		}
	}

}
