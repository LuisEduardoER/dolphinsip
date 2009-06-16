package com.dsc.dip.etl.processing.component;

import com.dsc.dip.etl.processing.datasource.DataSource;
import com.dsc.dip.etl.processing.datasource.DataSourceException;

public abstract class DataStorageComponent extends Component implements
		IInitComponent {

	protected DataSource dataSource;

	public boolean init() throws ComponentInitException {
		if (dataSource != null) {
			try {
				return dataSource.initConnect();
			} catch (DataSourceException e) {
				throw new ComponentInitException("Couldn't init component "
						+ name, e);
			}
		}
		return false;
	}

	public boolean complete() throws ComponentInitException {
		if (dataSource != null) {
			try {
				return dataSource.releaseConnect();
			} catch (DataSourceException e) {
				throw new ComponentInitException("Couldn't release component "
						+ name, e);
			}
		}
		return false;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
