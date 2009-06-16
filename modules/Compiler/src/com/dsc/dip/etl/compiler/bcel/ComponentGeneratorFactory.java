package com.dsc.dip.etl.compiler.bcel;

import com.dsc.dip.etl.compiler.bean.Component;

public class ComponentGeneratorFactory {

	public static ComponentGenerator instanceComponentGenerator(
			Component component, String componentType) {
		return new ComponentGenerator(component, componentType);
	}
}
