package by.bsu.fami.etl.compiler.bean;

import java.util.ArrayList;
import java.util.List;

public class DataSource {
	
	protected String name;

	protected String type;

	protected List<Property> properties = new ArrayList<Property>();

	public DataSource() {
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
	@Override
	public String toString() {
		return "Datasource:" + name;
	}
	
}
