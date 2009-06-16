package com.dsc.dip.etl.compiler.bean;

import java.util.ArrayList;
import java.util.List;

public class Rule {

	protected String name;

	protected Checker checker;

	protected List<Property> properties = new ArrayList<Property>();

	protected List<Component> components = new ArrayList<Component>();

	protected List<DataSource> dataSources = new ArrayList<DataSource>();

	protected List<Function> functions = new ArrayList<Function>();

	public Rule() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

	public List<Component> getComponents() {
		return components;
	}

	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public List<DataSource> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	public Checker getChecker() {
		return checker;
	}

	public void setChecker(Checker checker) {
		this.checker = checker;
	}

	public List<Function> getFunctions() {
		return functions;
	}

	public void setFunctions(List<Function> functions) {
		this.functions = functions;
	}

	public Function findFunction(String name) {
		for (Function function : functions) {
			if (function.getName().equals(name.replace("\"", ""))) {
				return function;
			}
		}
		return null;
	}

	public Component findComponent(String name) {
		for (Component component : components) {
			if (component.getName().equals(name.replace("\"", ""))) {
				return component;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder prs = new StringBuilder();
		for (Property property : properties) {
			prs.append(property + "|");
		}
		return "Rule:" + name;
	}
}
