package com.dsc.dip.etl.compiler.bean;

import java.util.ArrayList;
import java.util.List;

public class Function {

	protected String name;

	protected String block;
	
	protected String returnType;

	protected List<FunctionParameter> parametrs = new ArrayList<FunctionParameter>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public List<FunctionParameter> getParametrs() {
		return parametrs;
	}

	public void setParametrs(List<FunctionParameter> parametrs) {
		this.parametrs = parametrs;
	}

	@Override
	public String toString() {
		return "function : " + name;
	}

}
