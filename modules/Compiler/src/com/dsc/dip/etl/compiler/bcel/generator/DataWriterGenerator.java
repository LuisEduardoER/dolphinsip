package com.dsc.dip.etl.compiler.bcel.generator;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.log4j.Logger;

import com.dsc.dip.etl.compiler.bcel.GenerateException;
import com.dsc.dip.etl.compiler.bean.DataWriter;

/**
 * Generate extension code for DataWriter component.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public class DataWriterGenerator extends ComponentWithDSGenerator {

    /**
     * log4j logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(DataWriterGenerator.class);

    /**
     * name of setter for id attribute.
     */
    private static final String ID_SETTER = "setId";

    /**
     * name of setter for rootId attribute.
     */
    private static final String ROOT_ID_SETTER = "setRootId";

    /**
     * name of setter for keys attribute.
     */
    private static final String KEYS_SETTER = "setKeys";

    /**
     * Constructor for DataReader component generator class.
     * @param dataWriter
     *            - bean which contains all information about parsed DataWriter
     *            component
     * @param aComponentType
     *            - real java class name for generated component
     * @throws ClassNotFoundException
     *             - didn't find java component class
     */
    public DataWriterGenerator(final DataWriter dataWriter,
            final String aComponentType) throws ClassNotFoundException {
        super(dataWriter, aComponentType);
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
            // Set writer rootId
            int index = getComponentInitVar().getIndex();
            componentCallStringSetter(constantPool, iFactory, il, index,
                    ROOT_ID_SETTER, ((DataWriter) getComponent()).getRootId());
            // Set writer field id
            componentCallStringSetter(constantPool, iFactory, il, index,
                    ID_SETTER, ((DataWriter) getComponent()).getFieldId());
            // Set writer field key
            componentCallStringSetter(constantPool, iFactory, il, index,
                    KEYS_SETTER, ((DataWriter) getComponent()).getFieldKeys());
            // Set reader datasource
            generateSetDataSource(iFactory, il, constantPool, index,
                    ((DataWriter) getComponent()).getDataSource());
        } catch (ClassNotFoundException e) {
            String message =
                    "Couldn't find component of class : "
                            + getComponent().getType();
            LOGGER.error(message, e);
            throw new GenerateException(message, e);
        }
    }

}
