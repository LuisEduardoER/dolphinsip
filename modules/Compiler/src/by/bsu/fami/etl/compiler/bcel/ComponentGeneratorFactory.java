package by.bsu.fami.etl.compiler.bcel;

import by.bsu.fami.etl.compiler.bean.Component;

public class ComponentGeneratorFactory {

	public static ComponentGenerator instanceComponentGenerator(
			Component component, String componentType) {
		return new ComponentGenerator(component, componentType);
	}
}
