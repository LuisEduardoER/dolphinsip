package com.dsc.dip.etl.compiler.bcel.generator;

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

import com.dsc.dip.etl.compiler.bcel.GenerateException;
import com.dsc.dip.etl.compiler.bean.DataSource;
import com.dsc.dip.etl.compiler.bean.Property;

/**
 * Generate java byte-code to create datasource local variable.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public class DataSourceGenerator {

    /**
     * log4j logger.
     */
    private static final Logger LOGGER =
            Logger.getLogger(DataSourceGenerator.class);

    /**
     * Name of method to add properties to component.
     */
    private static final String ADD_PROPERTY_METHOD = "addProperty";

    /**
     * Name of component setter for datasource name.
     */
    private static final String NAME_SETTER = "setName";

    /**
     * Name of component setter for datasource type.
     */
    private static final String TYPE_SETTER = "setType";

    /**
     * datasource bean which contains all information about parsed datasource.
     */
    private DataSource dataSource;

    /**
     * Real java class name for generated datasource.
     */
    private String dataSourceType;

    /**
     * BCEL type by java class for generated datasource.
     */
    private Type dataSourceClass;

    /**
     * BCEL class to provide local variable of generated datasource.
     */
    private LocalVariableGen dataSourceInitVar;

    /**
     * Constructor for component generator class.
     * @param aDataSource
     *            - bean which contains all information about parsed component
     * @param aDataSourceType
     *            - real java class name for generated component
     * @throws ClassNotFoundException
     * @throws ClassNotFoundException
     *             - didn't find java component class
     */
    public DataSourceGenerator(final DataSource aDataSource,
            final String aDataSourceType) throws ClassNotFoundException {
        this.dataSource = aDataSource;
        this.dataSourceType = aDataSourceType;
        this.dataSourceClass = Type.getType(Class.forName(aDataSourceType));
    }

    /**
     * Method generate instance of BCEL local variable initialize with instance
     * of configure datasource class.
     * @param initComponentMethod
     *            - BCEL method class, provide rule init method where created
     *            component local variable.
     * @param iFactory
     *            - instances BCEL helper class to generate typed versions of
     *            instructions
     * @throws GenerateException
     *             exception if not possible generate java byte-code by syntax
     *             tree
     */
    public final void generateInitDataSource(
            final MethodGen initComponentMethod,
            final InstructionFactory iFactory) throws GenerateException {
        if (StringUtils.isEmpty(dataSourceType)) {
            String message =
                    "Incorrect type of datasource : " + dataSource.getType();
            LOGGER.error(message);
            throw new GenerateException(message);
        }
        InstructionList il = initComponentMethod.getInstructionList();
        ConstantPoolGen constantPool = initComponentMethod.getConstantPool();
        // Create component
        String compName = dataSource.getName();
        dataSourceInitVar =
                initComponentMethod.addLocalVariable(compName, dataSourceClass,
                        null, null);
        il.append(iFactory.createNew(dataSourceType));
        il.append(InstructionConstants.DUP);
        il.append(iFactory.createInvoke(dataSourceType, "<init>", Type.VOID,
                Type.NO_ARGS, Constants.INVOKESPECIAL));
        il.append(InstructionFactory.createStore(dataSourceClass,
                dataSourceInitVar.getIndex()));

        // Set datasource name
        componentCallStringSetter(constantPool, iFactory, il, dataSourceInitVar
                .getIndex(), NAME_SETTER, compName);
        // Set datasource type
        componentCallStringSetter(constantPool, iFactory, il, dataSourceInitVar
                .getIndex(), TYPE_SETTER, compName);

        // Set datasource properties
        generateInitProperties(iFactory, il, constantPool);
    }

    /**
     * Generate and add to datasource properties.
     * @param iFactory
     *            - instances BCEL helper class to generate typed versions of
     *            instructions
     * @param il
     *            - container for a list of Instruction objects
     * @param constantPool
     *            - constant pool object used to build up a constant pool
     */
    protected final void generateInitProperties(
            final InstructionFactory iFactory,
            final InstructionList il,
            final ConstantPoolGen constantPool) {
        for (Property property : dataSource.getProperties()) {
            il.append(InstructionFactory.createLoad(dataSourceClass,
                    dataSourceInitVar.getIndex()));
            il.append(new LDC(constantPool.addString(property.getName())));
            il.append(new LDC(constantPool.addString(property.getValue())));
            il.append(iFactory.createInvoke(dataSourceType,
                    ADD_PROPERTY_METHOD, Type.BOOLEAN, new Type[] {
                            Type.STRING,
                            Type.STRING}, Constants.INVOKEVIRTUAL));
            il.append(InstructionConstants.POP);
        }
    }

    /**
     * Generated setter call to datasource to set some component attribute.
     * @param constantPool
     *            - constant pool object used to build up a constant pool
     * @param iFactory
     *            - instances BCEL helper class to generate typed versions of
     *            instructions
     * @param il
     *            - container for a list of Instruction objects
     * @param componentIndex
     *            - id of component local variable in local variables table
     * @param setter
     *            - name of component setter method
     * @param value
     *            - value to set component attribute
     */
    protected final void componentCallStringSetter(
            final ConstantPoolGen constantPool,
            final InstructionFactory iFactory,
            final InstructionList il,
            final int componentIndex,
            final String setter,
            final String value) {
        il.append(InstructionFactory
                .createLoad(dataSourceClass, componentIndex));
        il.append(new LDC(constantPool.addString(value)));
        il.append(iFactory.createInvoke(dataSourceType, setter, Type.VOID,
                new Type[] {Type.STRING}, Constants.INVOKEVIRTUAL));
    }

    /**
     * BCEL class to provide local variable of generated datasource.
     * @return LocalVariableGen local variable of generated datasource
     */
    public final LocalVariableGen getDataSourceInitVar() {
        return dataSourceInitVar;
    }

}
