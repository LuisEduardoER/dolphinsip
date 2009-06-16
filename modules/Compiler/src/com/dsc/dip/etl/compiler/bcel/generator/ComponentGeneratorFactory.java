package com.dsc.dip.etl.compiler.bcel.generator;

import com.dsc.dip.etl.compiler.bean.Checker;
import com.dsc.dip.etl.compiler.bean.Component;
import com.dsc.dip.etl.compiler.bean.DataReader;
import com.dsc.dip.etl.compiler.bean.DataWriter;

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
