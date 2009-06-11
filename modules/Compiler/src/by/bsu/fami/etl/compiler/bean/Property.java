package by.bsu.fami.etl.compiler.bean;

public class Property {

	protected String name;

	protected String value;

	public Property() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Property " + name + " = " + value + ";";
	}

}
