package com.dsc.dip.etl.processing.datasource;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.dsc.dip.etl.processing.Property;


public abstract class DataSource {

	protected String name;

	protected String type;

	protected Map<String, Property> properties = new HashMap<String, Property>();

	public abstract boolean initConnect() throws DataSourceException;

	public abstract boolean releaseConnect() throws DataSourceException;

	public boolean addProperty(Property property) {
		if (property != null && StringUtils.isNotEmpty(property.getName())) {
			Property old = properties.get(property.getName());
			if (old == null) {
				properties.put(property.getName(), property);
			} else {
				old.setValue(property.getValue());
			}
			return true;
		}
		return false;
	}

	public boolean addProperty(String name, String value) {
		if (StringUtils.isNotEmpty(name)) {
			return addProperty(new Property(name, value));
		}
		return false;
	}

	public Property getProperty(String name) {
		return properties.get(name);
	}

	public String getPropertyValue(String name) {
		Property property = properties.get(name);
		return property == null ? null : property.getValue();
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public Map<String, Property> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Property> properties) {
		this.properties = properties;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

}
