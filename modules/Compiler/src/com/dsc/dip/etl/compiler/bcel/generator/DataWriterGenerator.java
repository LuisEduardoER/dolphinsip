package com.dsc.dip.etl.compiler.bcel.generator;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.log4j.Logger;

import com.dsc.dip.etl.compiler.bcel.GenerateException;
import com.dsc.dip.etl.compiler.bean.DataWriter;


public class DataWriterGenerator extends ComponentWithDSGenerator {

	private static final String ROOT_ID_SETTER = "setRootId";

	private static final String ID_SETTER = "setId";

	private static final String KEYS_SETTER = "setKeys";

	private final static Logger LOGGER = Logger
			.getLogger(DataWriterGenerator.class);

	public DataWriterGenerator(DataWriter dataWriter, String componentType) {
		super(dataWriter, componentType);
	}

	public void generateInitComponent(MethodGen initComponentMethod,
			InstructionFactory iFactory) throws GenerateException {
		super.generateInitComponent(initComponentMethod, iFactory);
		// Init variables
		try {
			InstructionList il = initComponentMethod.getInstructionList();
			ConstantPoolGen constantPool = initComponentMethod
					.getConstantPool();
			// Set writer rootId
			int index = componentInitVar.getIndex();
			componentCallStringSetter(constantPool, iFactory, il, index,
					ROOT_ID_SETTER, ((DataWriter) component).getRootId());
			// Set writer field id
			componentCallStringSetter(constantPool, iFactory, il, index,
					ID_SETTER, ((DataWriter) component).getFieldId());
			// Set writer field key
			componentCallStringSetter(constantPool, iFactory, il, index,
					KEYS_SETTER, ((DataWriter) component).getFieldKeys());
			// Set reader datasource
			generateSetDataSource(iFactory, il, constantPool, index,
					((DataWriter) component).getDataSource());
		} catch (ClassNotFoundException e) {
			String message = "Couldn't find component of class : "
					+ component.getType();
			LOGGER.error(message, e);
			throw new GenerateException(message, e);
		}
	}

}
