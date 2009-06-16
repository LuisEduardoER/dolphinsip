package com.dsc.dip.etl.compiler.bean;

public class DataWriter extends Component {

	protected String dataSource;

	protected String rootId;

	protected String fieldId;

	protected String fieldKeys;

	public DataWriter() {
	}

	public String getDataSource() {
		return dataSource;
	}

	public String getRootId() {
		return rootId;
	}

	public void setRootId(String rootId) {
		this.rootId = rootId;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getFieldKeys() {
		return fieldKeys;
	}

	public void setFieldKeys(String fieldKeys) {
		this.fieldKeys = fieldKeys;
	}
}
