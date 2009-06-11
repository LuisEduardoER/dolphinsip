package by.bsu.fami.etl.compiler.bcel.generator;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.log4j.Logger;

import by.bsu.fami.etl.compiler.bcel.GenerateException;
import by.bsu.fami.etl.compiler.bean.DataReader;

public class DataReaderGenerator extends ComponentWithDSGenerator {

	private static final String MAP_SETTER = "setMap";

	private final static Logger LOGGER = Logger
			.getLogger(DataReaderGenerator.class);

	public DataReaderGenerator(DataReader dataReader, String componentType) {
		super(dataReader, componentType);
	}

	public void generateInitComponent(MethodGen initComponentMethod,
			InstructionFactory iFactory) throws GenerateException {
		super.generateInitComponent(initComponentMethod, iFactory);
		// Init variables
		try {
			InstructionList il = initComponentMethod.getInstructionList();
			ConstantPoolGen constantPool = initComponentMethod
					.getConstantPool();
			// Set reader map
			int index = componentInitVar.getIndex();
			componentCallStringSetter(constantPool, iFactory, il, index,
					MAP_SETTER, ((DataReader) component).getMap());
			// Set reader datasource
			generateSetDataSource(iFactory, il, constantPool, index,
					((DataReader) component).getDataSource());
		} catch (ClassNotFoundException e) {
			String message = "Couldn't find component of class : "
					+ component.getType();
			LOGGER.error(message, e);
			throw new GenerateException(message, e);
		}
	}

}
