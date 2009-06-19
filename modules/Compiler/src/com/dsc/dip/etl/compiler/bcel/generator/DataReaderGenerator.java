package com.dsc.dip.etl.compiler.bcel.generator;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.log4j.Logger;

import com.dsc.dip.etl.compiler.bcel.GenerateException;
import com.dsc.dip.etl.compiler.bean.DataReader;

/**
 * Generate extension code for DataReader component.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public class DataReaderGenerator extends ComponentWithDSGenerator {

    /**
     * log4j logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(DataReaderGenerator.class);

    /**
     * name of setter for map attribute.
     */
    private static final String MAP_SETTER = "setMap";

    /**
     * Constructor for DataReader component generator class.
     * @param dataReader
     *            - bean which contains all information about parsed DataReader
     *            component
     * @param aComponentType
     *            - real java class name for generated component
     * @throws ClassNotFoundException
     *             - didn't find java component class
     */
    public DataReaderGenerator(final DataReader dataReader,
            final String aComponentType) throws ClassNotFoundException {
        super(dataReader, aComponentType);
    }

    @Override
    public final void generateExtention(
            final MethodGen initComponentMethod,
            final InstructionFactory iFactory) throws GenerateException {
        super.generateInitComponent(initComponentMethod, iFactory);
        // Init variables
        try {
            InstructionList il = initComponentMethod.getInstructionList();
            ConstantPoolGen constantPool =
                    initComponentMethod.getConstantPool();
            // Set reader map
            int index = getComponentInitVar().getIndex();
            componentCallStringSetter(constantPool, iFactory, il, index,
                    MAP_SETTER, ((DataReader) getComponent()).getMap());
            // Set reader datasource
            generateSetDataSource(iFactory, il, constantPool, index,
                    ((DataReader) getComponent()).getDataSource());
        } catch (ClassNotFoundException e) {
            String message =
                    "Couldn't find component of class : "
                            + getComponent().getType();
            LOGGER.error(message, e);
            throw new GenerateException(message, e);
        }
    }

}
