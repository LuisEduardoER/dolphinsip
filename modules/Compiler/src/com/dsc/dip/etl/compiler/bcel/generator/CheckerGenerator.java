package com.dsc.dip.etl.compiler.bcel.generator;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;

import com.dsc.dip.etl.compiler.bcel.GenerateException;
import com.dsc.dip.etl.compiler.bean.Checker;

/**
 * Generate extension part of java byte-code for Checker component.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public class CheckerGenerator extends ComponentGenerator {

    /**
     * condition setter checker component name.
     */
    private static final String CONDITION_SETTER = "setCondition";

    /**
     * Constructor for chacker component generator class.
     * @param checker
     *            - bean which contains all information about parsed component
     * @param aComponentType
     *            - real java class name for generated component
     * @throws ClassNotFoundException
     *             - didn't find java component class
     */
    public CheckerGenerator(final Checker checker, final String aComponentType)
            throws ClassNotFoundException {
        super(checker, aComponentType);
    }

    @Override
    public final void generateExtention(
            final MethodGen initComponentMethod,
            final InstructionFactory iFactory) throws GenerateException {
        // Init variables
        InstructionList il = initComponentMethod.getInstructionList();
        ConstantPoolGen constantPool = initComponentMethod.getConstantPool();
        // Set condition
        int index = getComponentInitVar().getIndex();
        String condition = ((Checker) getComponent()).getCondition();
        componentCallStringSetter(constantPool, iFactory, il, index,
                CONDITION_SETTER, condition);
    }

}
