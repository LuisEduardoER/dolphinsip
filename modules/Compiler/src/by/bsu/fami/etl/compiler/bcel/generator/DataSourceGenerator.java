package by.bsu.fami.etl.compiler.bcel.generator;

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

import by.bsu.fami.etl.compiler.bcel.GenerateException;
import by.bsu.fami.etl.compiler.bean.DataSource;
import by.bsu.fami.etl.compiler.bean.Property;

public class DataSourceGenerator {

	private static final String ADD_PROPERTY_METHOD = "addProperty";

	private static final String NAME_SETTER = "setName";

	private static final String TYPE_SETTER = "setType";

	private final static Logger LOGGER = Logger
			.getLogger(DataSourceGenerator.class);

	public DataSource dataSource;

	public String dataSourceType;

	public LocalVariableGen dataSourceInitVar;

	public DataSourceGenerator(DataSource dataSource, String dataSourceType) {
		this.dataSource = dataSource;
		this.dataSourceType = dataSourceType;
	}

	public void generateInitComponent(MethodGen initComponentMethod,
			InstructionFactory iFactory) throws GenerateException {
		if (StringUtils.isEmpty(dataSourceType)) {
			String message = "Incorrect type of datasource : "
					+ dataSource.getType();
			LOGGER.error(message);
			throw new GenerateException(message);
		}
		try {
			InstructionList il = initComponentMethod.getInstructionList();
			ConstantPoolGen constantPool = initComponentMethod
					.getConstantPool();
			// Create component
			String compName = dataSource.getName();
			dataSourceInitVar = initComponentMethod.addLocalVariable(compName,
					Type.getType(Class.forName(dataSourceType)), null, null);
			il.append(iFactory.createNew(dataSourceType));
			il.append(InstructionConstants.DUP);
			il.append(iFactory.createInvoke(dataSourceType, "<init>",
					Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
			il.append(InstructionFactory.createStore(Type.getType(Class
					.forName(dataSourceType)), dataSourceInitVar.getIndex()));

			// Set datasource name
			componentCallStringSetter(constantPool, iFactory, il,
					dataSourceInitVar.getIndex(), NAME_SETTER, compName);
			// Set datasource type
			componentCallStringSetter(constantPool, iFactory, il,
					dataSourceInitVar.getIndex(), TYPE_SETTER, compName);

			// Set datasource properties
			generateInitProperties(iFactory, il, constantPool);
		} catch (ClassNotFoundException e) {
			String message = "Couldn't find component of class : "
					+ dataSource.getType();
			LOGGER.error(message, e);
			throw new GenerateException(message, e);
		}
	}

	protected void generateInitProperties(InstructionFactory iFactory,
			InstructionList il, ConstantPoolGen constantPool)
			throws ClassNotFoundException {
		for (Property property : dataSource.getProperties()) {
			il.append(InstructionFactory.createLoad(Type.getType(Class
					.forName(dataSourceType)), dataSourceInitVar.getIndex()));
			il.append(new LDC(constantPool.addString(property.getName())));
			il.append(new LDC(constantPool.addString(property.getValue())));
			il.append(iFactory
					.createInvoke(dataSourceType, ADD_PROPERTY_METHOD,
							Type.BOOLEAN,
							new Type[] { Type.STRING, Type.STRING },
							Constants.INVOKEVIRTUAL));
			il.append(InstructionConstants.POP);
		}
	}

	protected void componentCallStringSetter(ConstantPoolGen constantPool,
			InstructionFactory iFactory, InstructionList il,
			int componentIndex, String setter, String value)
			throws ClassNotFoundException {
		il.append(InstructionFactory.createLoad(Type.getType(Class
				.forName(dataSourceType)), componentIndex));
		il.append(new LDC(constantPool.addString(value)));
		il.append(iFactory.createInvoke(dataSourceType, setter, Type.VOID,
				new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));
	}

	public LocalVariableGen getDataSourceInitVar() {
		return dataSourceInitVar;
	}

}
