package by.bsu.fami.etl.compiler.bean;

import java.util.ArrayList;
import java.util.List;

public abstract class Component{

	protected String name;

	protected String outputScheme;

	protected String type;

	protected List<String> calleds = new ArrayList<String>();

	protected List<Property> properties = new ArrayList<Property>();

	protected List<Field> fields = new ArrayList<Field>();

	protected Rule rule;

	public Component() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOutputScheme() {
		return outputScheme;
	}

	public void setOutputScheme(String outputScheme) {
		this.outputScheme = outputScheme;
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

	public List<Field> getFields() {
		return fields;
	}

	public void setFields(List<Field> fields) {
		this.fields = fields;
	}

	public List<String> getCalleds() {
		return calleds;
	}

	public void setCalleds(List<String> calleds) {
		this.calleds = calleds;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	@Override
	public String toString() {
		return "Component:" + name;
	}
}
