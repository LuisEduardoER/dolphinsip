package com.dsc.dip.etl.compiler.bean;

import java.util.ArrayList;
import java.util.List;

public class Checker extends Component{
	
	protected String condition;
	
	protected String name;
	
	protected String type;
	
	protected List<Property> properties = new ArrayList<Property>();
	
	public Checker(){
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getType() {
		return type;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}
	
	@Override
	public String toString() {
		return "Checker:" + name;
	}
}
