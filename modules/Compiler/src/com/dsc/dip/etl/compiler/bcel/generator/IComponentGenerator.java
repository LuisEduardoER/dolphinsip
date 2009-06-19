package com.dsc.dip.etl.compiler.bcel.generator;

import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.MethodGen;

import com.dsc.dip.etl.compiler.bcel.GenerateException;

/**
 * Base interface to Component generators classes.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public interface IComponentGenerator {

    /**
     * Method generate instance of BCEL local variable initialize with instance
     * of configure component class.
     * @param initComponentMethod
     *            - BCEL method class, provide rule init method where created
     *            component local variable.
     * @param iFactory
     *            - instances BCEL helper class to generate typed versions of
     *            instructions
     * @throws GenerateException
     *             exception if not possible generate java byte-code by sintax
     *             tree
     */
    void generateInitComponent(final MethodGen initComponentMethod,
            final InstructionFactory iFactory) throws GenerateException;

}
