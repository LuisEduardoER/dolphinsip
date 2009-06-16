package com.dsc.dip.etl.processing.document;

public class Field {

	protected String name;

	protected String type;

	protected String value;

	public Field(String name) {
		if (name == null) {
			throw new IllegalArgumentException("Field name cann't be null.");
		}
		this.name = name;
	}

	public Field(String name, String type) {
		this(name);
		this.type = type;
	}

	public Field(String name, String type, String value) {
		this(name, type);
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Field name:" + name + "; type:" + type + ";value:" + value + ".";
	}

	@Override
	public boolean equals(Object field) {
		if (field instanceof Field) {
			return name.equals(((Field) field).name);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public Field copy() {
		return new Field(this.name, this.type, this.value);
	}
}
