package com.dsc.dip.etl.compiler.bcel;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dsc.dip.etl.compiler.bean.Component;
import com.dsc.dip.etl.compiler.bean.Field;
import com.dsc.dip.etl.compiler.bean.Property;


public class ComponentGenerator {

	private static final String ADD_PROPERTY_METHOD = "addProperty";

	private static final String ADD_FIELD_METHOD = "addField";

	// private static final String JAVA_UTIL_MAP = "java.util.Map";
	//
	// private static final String JAVA_UTIL_HASH_MAP = "java.util.HashMap";

	private static final String OUTPUT_SCHEME_SETTER = "setOutputScheme";

	private static final String NAME_SETTER = "setName";

	private final static Logger LOGGER = Logger.getLogger(RuleGenerator.class);

	public Component component;

	public String componentType;

	public LocalVariableGen componentInitVar;

	public ComponentGenerator(Component component, String componentType) {
		this.component = component;
		this.componentType = componentType;
	}

	public void generateInitComponent(MethodGen initComponentMethod,
			InstructionFactory iFactory) throws GenerateException {
		if (StringUtils.isEmpty(componentType)) {
			LOGGER
					.error("Incorrect type of component : "
							+ component.getType());
			throw new GenerateException("Incorrect type of component : "
					+ component.getType());
		}
		try {
			InstructionList il = initComponentMethod.getInstructionList();
			ConstantPoolGen constantPool = initComponentMethod
					.getConstantPool();
			// Create component
			String compName = component.getName();
			componentInitVar = initComponentMethod.addLocalVariable(compName,
					Type.getType(Class.forName(componentType)), null, null);
			il.append(iFactory.createNew(componentType));
			il.append(InstructionConstants.DUP);
			il.append(iFactory.createInvoke(componentType, "<init>", Type.VOID,
					Type.NO_ARGS, Constants.INVOKESPECIAL));
			il.append(InstructionFactory.createStore(Type.getType(Class
					.forName(componentType)), componentInitVar.getIndex()));

			// Set component name
			componentCallStringSetter(constantPool, iFactory, il,
					componentInitVar.getIndex(), NAME_SETTER, compName);
			// Set component output scheme
			componentCallStringSetter(constantPool, iFactory, il,
					componentInitVar.getIndex(), OUTPUT_SCHEME_SETTER,
					component.getOutputScheme());

			// Set component properties
			generateInitProperties(iFactory, il, constantPool);

			// Set component fields
			generateInitField(iFactory, il, constantPool);
			// if (component.getProperties().size() > 0) {
			// String compPropsName = compName + "Props";
			// LocalVariableGen compProps = initComponentMethod
			// .addLocalVariable(compPropsName, Type.getType(Class
			// .forName(JAVA_UTIL_HASH_MAP)), null, null);
			// il.append(iFactory.createNew(JAVA_UTIL_HASH_MAP));
			// il.append(InstructionConstants.DUP);
			// il.append(iFactory.createInvoke(JAVA_UTIL_HASH_MAP, "<init>",
			// Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
			// il.append(InstructionFactory.createStore(Type.getType(Class
			// .forName(JAVA_UTIL_HASH_MAP)), compProps.getIndex()));
			// for (Property property : component.getProperties()) {
			// il.append(InstructionFactory
			// .createLoad(Type.getType(Class
			// .forName(JAVA_UTIL_HASH_MAP)), compProps
			// .getIndex()));
			// il.append(new LDC(constantPool
			// .addString(property.getName())));
			// il.append(iFactory.createNew(RuleGenerator.PROPERTY_CLASS));
			// il.append(InstructionConstants.DUP);
			// il.append(new LDC(constantPool
			// .addString(property.getName())));
			// il.append(new LDC(constantPool.addString(property
			// .getValue())));
			// il.append(iFactory.createInvoke(
			// RuleGenerator.PROPERTY_CLASS, "<init>", Type.VOID,
			// new Type[] { Type.STRING, Type.STRING },
			// Constants.INVOKESPECIAL));
			// il.append(iFactory.createInvoke(JAVA_UTIL_MAP, "put",
			// Type.OBJECT,
			// new Type[] { Type.OBJECT, Type.OBJECT },
			// Constants.INVOKEINTERFACE));
			// il.append(InstructionConstants.POP);
			// }
		} catch (ClassNotFoundException e) {
			String message = "Couldn't find component of class : "
					+ component.getType();
			LOGGER.error(message, e);
			throw new GenerateException(message, e);
		}
	}

	protected void generateInitProperties(InstructionFactory iFactory,
			InstructionList il, ConstantPoolGen constantPool)
			throws ClassNotFoundException {
		for (Property property : component.getProperties()) {
			il.append(InstructionFactory.createLoad(Type.getType(Class
					.forName(componentType)), componentInitVar.getIndex()));
			il.append(new LDC(constantPool.addString(property.getName())));
			il.append(new LDC(constantPool.addString(property.getValue())));
			il.append(iFactory.createInvoke(componentType, ADD_PROPERTY_METHOD,
					Type.BOOLEAN, new Type[] { Type.STRING, Type.STRING },
					Constants.INVOKEVIRTUAL));
			il.append(InstructionConstants.POP);
		}
	}

	protected void generateInitField(InstructionFactory iFactory,
			InstructionList il, ConstantPoolGen constantPool)
			throws ClassNotFoundException {
		for (Field field : component.getFields()) {
			il.append(InstructionFactory.createLoad(Type.getType(Class
					.forName(componentType)), componentInitVar.getIndex()));
			il.append(new LDC(constantPool.addString(field.getName())));
			il.append(new LDC(constantPool.addString(field.getType())));
			il.append(new LDC(constantPool.addString(field.getValue())));
			il.append(iFactory.createInvoke(componentType, ADD_FIELD_METHOD,
					Type.BOOLEAN, new Type[] { Type.STRING, Type.STRING,
							Type.STRING }, Constants.INVOKEVIRTUAL));
			il.append(InstructionConstants.POP);
		}
	}

	protected void componentCallStringSetter(ConstantPoolGen constantPool,
			InstructionFactory iFactory, InstructionList il,
			int componentIndex, String setter, String value)
			throws ClassNotFoundException {
		il.append(InstructionFactory.createLoad(Type.getType(Class
				.forName(componentType)), componentIndex));
		il.append(new LDC(constantPool.addString(value)));
		il.append(iFactory.createInvoke(componentType, setter, Type.VOID,
				new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));
	}

	public LocalVariableGen getComponentInitVar() {
		return componentInitVar;
	}
}
