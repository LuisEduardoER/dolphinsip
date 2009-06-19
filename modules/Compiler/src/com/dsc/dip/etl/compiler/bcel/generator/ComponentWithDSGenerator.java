package com.dsc.dip.etl.compiler.bcel.generator;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.Type;

import com.dsc.dip.etl.compiler.bean.Component;

/**
 * Base abstract generator for component with datsource local variable for rule
 * class.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public abstract class ComponentWithDSGenerator extends ComponentGenerator {

    /**
     * java class name of BaseRule class.
     */
    private String baseRuleClass = "by.bsu.fami.etl.processing." + "BaseRule";

    /**
     * java class name of datasource class.
     */
    private String baseDataSourceClass =
            "by.bsu.fami.etl." + "processing.datasource.DataSource";

    /**
     * Constructor for datasource generator class.
     * @param aComponent
     *            - bean which contains all information about parsed component
     * @param aComponentType
     *            - real java class name for generated component
     * @throws ClassNotFoundException
     *             - didn't find java component class
     */
    public ComponentWithDSGenerator(final Component aComponent,
            final String aComponentType) throws ClassNotFoundException {
        super(aComponent, aComponentType);
    }

    /**
     * Generated set datasource value for component.
     * @param iFactory
     *            - instances BCEL helper class to generate typed versions of
     *            instructions
     * @param il
     *            - container for a list of Instruction objects
     * @param constantPool
     *            - constant pool object used to build up a constant pool
     * @param index
     *            - id of component local variable in local variables table
     * @param dataSource
     *            - name of datasource for component
     * @throws ClassNotFoundException
     *             - didn't find rule or datasource java class
     */
    protected final void generateSetDataSource(
            final InstructionFactory iFactory,
            final InstructionList il,
            final ConstantPoolGen constantPool,
            final int index,
            final String dataSource) throws ClassNotFoundException {
        il.append(InstructionFactory.createLoad(getComponentClass(), index));
        il.append(InstructionFactory.createLoad(Type.getType(Class
                .forName(baseRuleClass)), 0));
        il.append(new LDC(constantPool.addString((dataSource))));
        il.append(iFactory.createInvoke(baseRuleClass, "findDataSource", Type
                .getType(Class.forName(baseDataSourceClass)),
                new Type[] {Type.STRING}, Constants.INVOKEVIRTUAL));
        il.append(iFactory.createInvoke(getComponentType(), "setDataSource",
                Type.VOID, new Type[] {Type.getType(Class
                        .forName(baseDataSourceClass))},
                Constants.INVOKEVIRTUAL));
    }
}
