package com.dsc.dip.etl.processing.component.checker;

import java.util.HashMap;
import java.util.Map;

import com.dsc.dip.etl.processing.Property;
import com.dsc.dip.etl.processing.component.Component;


public abstract class Checker extends Component {

	protected String name;

	protected String condition;

	protected Map<String, Property> properties = new HashMap<String, Property>();

	public abstract boolean check();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public Map<String, Property> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Property> properties) {
		this.properties = properties;
	}

}
