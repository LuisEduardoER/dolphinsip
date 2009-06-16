package com.dsc.dip.etl.compiler.bean;

public class DataReader extends Component {

	protected String map;

	protected String dataSource;

	public DataReader() {
	}

	public String getMap() {
		return map;
	}

	public void setMap(String map) {
		this.map = map;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

}
