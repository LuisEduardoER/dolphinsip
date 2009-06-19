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
import com.dsc.dip.etl.compiler.bean.Component;
import com.dsc.dip.etl.compiler.bean.Field;
import com.dsc.dip.etl.compiler.bean.Property;

/**
 * Generate java byte-code for base component local variable in rule init
 * method. <br/> Generate:
 * <ul>
 * <li>new directive</li>
 * <li>set component name</li>
 * <li>set component output scheme</li>
 * <li>calleds components</li>
 * <li>component fields</li>
 * <li>component properties</li>
 * </ul>
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public class ComponentGenerator implements IComponentGenerator {

    /**
     * log4j logger.
     */
    protected static final Logger LOGGER =
            Logger.getLogger(ComponentGenerator.class);

    /**
     * Name of method to add properties to component.
     */
    private static final String ADD_PROPERTY_METHOD = "addProperty";

    /**
     * Name of method to add fields to component.
     */
    private static final String ADD_FIELD_METHOD = "addField";

    // private static final String JAVA_UTIL_MAP = "java.util.Map";
    //
    // private static final String JAVA_UTIL_HASH_MAP = "java.util.HashMap";

    /**
     * Name of component setter for output scheme.
     */
    private static final String OUTPUT_SCHEME_SETTER = "setOutputScheme";

    /**
     * Name of component setter for component name.
     */
    private static final String NAME_SETTER = "setName";

    /**
     * Component bean which contains all information about parsed component.
     */
    private Component component;

    /**
     * Real java class name for generated component.
     */
    private String componentType;

    /**
     * BCEL type by java class for generated component.
     */
    private Type componentClass;

    /**
     * BCEL class to provide local variable of generated component.
     */
    private LocalVariableGen componentInitVar;

    /**
     * Constructor for component generator class.
     * @param aComponent
     *            - bean which contains all information about parsed component
     * @param aComponentType
     *            - real java class name for generated component
     * @throws ClassNotFoundException
     *             - didn't find java component class
     */
    public ComponentGenerator(final Component aComponent,
            final String aComponentType) throws ClassNotFoundException {
        this.component = aComponent;
        this.componentType = aComponentType;
        this.componentClass = Type.getType(Class.forName(aComponentType));
    }

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
     *             exception if not possible generate java byte-code by syntax
     *             tree
     */
    public final void generateInitComponent(
            final MethodGen initComponentMethod,
            final InstructionFactory iFactory) throws GenerateException {
        if (StringUtils.isEmpty(componentType)) {
            LOGGER
                    .error("Incorrect type of component : "
                            + component.getType());
            throw new GenerateException("Incorrect type of component : "
                    + component.getType());
        }
        InstructionList il = initComponentMethod.getInstructionList();
        ConstantPoolGen constantPool = initComponentMethod.getConstantPool();
        // Create component
        String compName = component.getName();
        componentInitVar =
                initComponentMethod.addLocalVariable(compName, componentClass,
                        null, null);
        il.append(iFactory.createNew(componentType));
        il.append(InstructionConstants.DUP);
        il.append(iFactory.createInvoke(componentType, "<init>", Type.VOID,
                Type.NO_ARGS, Constants.INVOKESPECIAL));
        il.append(InstructionFactory.createStore(componentClass,
                componentInitVar.getIndex()));

        // Set component name
        componentCallStringSetter(constantPool, iFactory, il, componentInitVar
                .getIndex(), NAME_SETTER, compName);
        // Set component output scheme
        componentCallStringSetter(constantPool, iFactory, il, componentInitVar
                .getIndex(), OUTPUT_SCHEME_SETTER, component.getOutputScheme());

        // Set component properties
        generateInitProperties(iFactory, il, constantPool);

        // Set component fields
        generateInitField(iFactory, il, constantPool);
        // if (component.getProperties().size() > 0) {
        // String compPropsName = compName + "Props";
        // LocalVariableGen compProps = initComponentMethod
        // .addLocalVariable(compPropsName, Type.getType(Class
        // .forName(JAVA_UTIL_HASH_MAP)), null, null);
        // il.append(iFactory.createNew(JAVA_UTIL_HASH_MAP));
        // il.append(InstructionConstants.DUP);
        // il.append(iFactory.createInvoke(JAVA_UTIL_HASH_MAP, "<init>",
        // Type.VOID, Type.NO_ARGS, Constants.INVOKESPECIAL));
        // il.append(InstructionFactory.createStore(Type.getType(Class
        // .forName(JAVA_UTIL_HASH_MAP)), compProps.getIndex()));
        // for (Property property : component.getProperties()) {
        // il.append(InstructionFactory
        // .createLoad(Type.getType(Class
        // .forName(JAVA_UTIL_HASH_MAP)), compProps
        // .getIndex()));
        // il.append(new LDC(constantPool
        // .addString(property.getName())));
        // il.append(iFactory.createNew(RuleGenerator.PROPERTY_CLASS));
        // il.append(InstructionConstants.DUP);
        // il.append(new LDC(constantPool
        // .addString(property.getName())));
        // il.append(new LDC(constantPool.addString(property
        // .getValue())));
        // il.append(iFactory.createInvoke(
        // RuleGenerator.PROPERTY_CLASS, "<init>", Type.VOID,
        // new Type[] { Type.STRING, Type.STRING },
        // Constants.INVOKESPECIAL));
        // il.append(iFactory.createInvoke(JAVA_UTIL_MAP, "put",
        // Type.OBJECT,
        // new Type[] { Type.OBJECT, Type.OBJECT },
        // Constants.INVOKEINTERFACE));
        // il.append(InstructionConstants.POP);
        // }

        // add extension attributes and etc.
        generateExtention(initComponentMethod, iFactory);
    }

    /**
     * Generate extension directives: add some other specify attributes to
     * component variable.
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
    protected void generateExtention(
            final MethodGen initComponentMethod,
            final InstructionFactory iFactory) throws GenerateException {
    }

    /**
     * Generate and add to component properties.
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
        for (Property property : component.getProperties()) {
            il.append(InstructionFactory.createLoad(componentClass,
                    componentInitVar.getIndex()));
            il.append(new LDC(constantPool.addString(property.getName())));
            il.append(new LDC(constantPool.addString(property.getValue())));
            il.append(iFactory.createInvoke(componentType, ADD_PROPERTY_METHOD,
                    Type.BOOLEAN, new Type[] {Type.STRING, Type.STRING},
                    Constants.INVOKEVIRTUAL));
            il.append(InstructionConstants.POP);
        }
    }

    /**
     * Generate and add to component fields.
     * @param iFactory
     *            - instances BCEL helper class to generate typed versions of
     *            instructions
     * @param il
     *            - container for a list of Instruction objects
     * @param constantPool
     *            - constant pool object used to build up a constant pool
     */
    protected final void generateInitField(
            final InstructionFactory iFactory,
            final InstructionList il,
            final ConstantPoolGen constantPool) {
        for (Field field : component.getFields()) {
            il.append(InstructionFactory.createLoad(componentClass,
                    componentInitVar.getIndex()));
            il.append(new LDC(constantPool.addString(field.getName())));
            il.append(new LDC(constantPool.addString(field.getType())));
            il.append(new LDC(constantPool.addString(field.getValue())));
            il.append(iFactory.createInvoke(componentType, ADD_FIELD_METHOD,
                    Type.BOOLEAN, new Type[] {
                            Type.STRING,
                            Type.STRING,
                            Type.STRING}, Constants.INVOKEVIRTUAL));
            il.append(InstructionConstants.POP);
        }
    }

    /**
     * Generated setter call to component to set some component attribute.
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
        if (StringUtils.isNotEmpty(setter) && StringUtils.isNotEmpty(value)) {
            il.append(InstructionFactory.createLoad(componentClass,
                    componentIndex));
            il.append(new LDC(constantPool.addString(value)));
            il.append(iFactory.createInvoke(componentType, setter, Type.VOID,
                    new Type[] {Type.STRING}, Constants.INVOKEVIRTUAL));
        }
    }

    /**
     * BCEL class to provide local variable of generated component.
     * @return generated component local variable.
     */
    public final LocalVariableGen getComponentInitVar() {
        return componentInitVar;
    }

    /**
     *Component bean which contains all information about parsed component.
     * @return component infomation bean.
     */
    public final Component getComponent() {
        return component;
    }

    /**
     * Real java class name for generated component.
     * @return java class name.
     */
    public final String getComponentType() {
        return componentType;
    }

    /**
     * BCEL type by java class for generated component.
     * @return BCEL type.
     */
    public final Type getComponentClass() {
        return componentClass;
    }
}
