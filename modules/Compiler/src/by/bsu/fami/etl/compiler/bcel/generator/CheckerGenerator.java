package by.bsu.fami.etl.compiler.bcel.generator;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.log4j.Logger;

import by.bsu.fami.etl.compiler.bcel.GenerateException;
import by.bsu.fami.etl.compiler.bean.Checker;

public class CheckerGenerator extends ComponentGenerator {

	private static final String CONDITION_SETTER = "setCondition";

	private final static Logger LOGGER = Logger
			.getLogger(CheckerGenerator.class);

	public CheckerGenerator(Checker checker, String componentType) {
		super(checker, componentType);
	}

	public void generateInitComponent(MethodGen initComponentMethod,
			InstructionFactory iFactory) throws GenerateException {
		super.generateInitComponent(initComponentMethod, iFactory);
		// Init variables
		try {
			InstructionList il = initComponentMethod.getInstructionList();
			ConstantPoolGen constantPool = initComponentMethod
					.getConstantPool();
			// Set condition
			int index = componentInitVar.getIndex();
			String condition = ((Checker) component).getCondition();
			componentCallStringSetter(constantPool, iFactory, il, index,
					CONDITION_SETTER, condition);
		} catch (ClassNotFoundException e) {
			String message = "Couldn't find component of class : "
					+ component.getType();
			LOGGER.error(message, e);
			throw new GenerateException(message, e);
		}
	}

}
