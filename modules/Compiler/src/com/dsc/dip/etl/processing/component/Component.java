package com.dsc.dip.etl.processing.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.dsc.dip.etl.processing.IExecuter;
import com.dsc.dip.etl.processing.Property;
import com.dsc.dip.etl.processing.document.Document;
import com.dsc.dip.etl.processing.document.Field;


public abstract class Component implements IExecuter {

	protected final static String THIS = "This";

	protected final static String NONE = "None";

	protected String name;

	protected String outputScheme;

	protected List<Component> calleds = new ArrayList<Component>();

	protected Map<String, Property> properties = new HashMap<String, Property>();

	protected Set<Field> fields = new HashSet<Field>();

	public List<Document> callAll(Document document) {
		if (THIS.equals(outputScheme)) {
			for (Field field : document.getFields()) {
				if (!fields.contains(field)) {
					document.removeField(field.getName());
				}
			}
		}
		List<Document> docs = new ArrayList<Document>();
		for (Component component : calleds) {
			if (component != null) {
				docs.addAll(component.execute(document));

			}
		}
		if (docs.isEmpty()) {
			document.setStatus(Document.DOCUMENT_STATUS_COMPLETE);
		}
		return docs;
	}

	public boolean addCall(Component component) {
		return calleds.add(component);
	}

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

	public boolean addField(Field field) {
		if (field != null && StringUtils.isNotEmpty(field.getName())) {
			Field old = getField(field.getName());
			if (old == null) {
				fields.add(field);
			} else {
				old.setValue(field.getValue());
			}
			return true;
		}
		return false;
	}

	public boolean addField(String name, String type, String value) {
		if (StringUtils.isNotEmpty(name)) {
			return addField(new Field(name, type, value));
		}
		return false;
	}

	public Field getField(String name) {
		for (Field field : fields) {
			if (StringUtils.equals(name, field.getName())) {
				return field;
			}
		}
		return null;
	}

	public String getFieldValue(String name) {
		Field field = getField(name);
		return field == null ? null : field.getValue();
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

	public List<Component> getCalleds() {
		return calleds;
	}

	public void setCalleds(List<Component> calleds) {
		this.calleds = calleds;
	}

	public Set<Field> getFields() {
		return fields;
	}

	public void setFields(Set<Field> fields) {
		this.fields = fields;
	}

}
