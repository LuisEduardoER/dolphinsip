package by.bsu.fami.etl.processing;

public class Property {

	protected String name;

	protected String value;

	public Property() {
	}

	public Property(String name, String value) {
		this.name = name;
		this.value = value;
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
		return "property " + name + " = " + value;
	}

}
