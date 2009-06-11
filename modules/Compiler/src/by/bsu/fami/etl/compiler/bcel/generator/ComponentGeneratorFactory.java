package by.bsu.fami.etl.compiler.bcel.generator;

import by.bsu.fami.etl.compiler.bean.Checker;
import by.bsu.fami.etl.compiler.bean.Component;
import by.bsu.fami.etl.compiler.bean.DataReader;
import by.bsu.fami.etl.compiler.bean.DataWriter;

public class ComponentGeneratorFactory {

	public static ComponentGenerator instanceComponentGenerator(
			Component component, String componentType) {
		if (component instanceof DataReader) {
			return new DataReaderGenerator((DataReader) component,
					componentType);
		} else {
			if (component instanceof DataWriter) {
				return new DataWriterGenerator((DataWriter) component,
						componentType);
			} else {
				if (component instanceof Checker) {
					return new CheckerGenerator((Checker) component,
							componentType);
				} else {
					return new ComponentGenerator(component, componentType);
				}
			}
		}
	}
}
