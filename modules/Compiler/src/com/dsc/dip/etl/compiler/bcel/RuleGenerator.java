package com.dsc.dip.etl.compiler.bcel;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.Type;
import org.apache.log4j.Logger;

import com.dsc.dip.etl.compiler.bcel.generator.ComponentGenerator;
import com.dsc.dip.etl.compiler.bcel.generator.ComponentGeneratorFactory;
import com.dsc.dip.etl.compiler.bcel.generator.DataSourceGenerator;
import com.dsc.dip.etl.compiler.bean.Checker;
import com.dsc.dip.etl.compiler.bean.Component;
import com.dsc.dip.etl.compiler.bean.DataSource;
import com.dsc.dip.etl.compiler.bean.Rule;

/**
 * Class generate rule java byte-code by syntax tree from compiler.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public class RuleGenerator {

    /**
     * log4j logger.
     */
    private static final Logger LOGGER = Logger.getLogger(RuleGenerator.class);

    /**
     * stack max size to java byte-code.
     */
    private static final int STACK_MAX_SIZE = 1000;

    /**
     * java class name of BaseRule class.
     */
    private String baseRuleClass = "by.bsu.fami.etl.processing." + "BaseRule";

    /**
     * java class name of CheckRule class.
     */
    private String checkRuleClass = "by.bsu.fami.etl.processing." + "CheckRule";

    /**
     * java class name of Base component class.
     */
    private String baseComponentClass =
            "by.bsu.fami.etl." + "processing.component.Component";

    /**
     * java class name of datasource class.
     */
    private String baseDataSourceClass =
            "by.bsu.fami.etl." + "processing.datasource.DataSource";

    /**
     * java class name of Checker component class.
     */
    private String baseCheckerComponentClass =
            "by.bsu.fami.etl.processing." + "component.checker.Checker";

    /**
     * Rule init method name.
     */
    private String initComponents = "initComponents";

    /**
     * map: component alias - component java class name.
     */
    private Map<String, String> typeMap = new HashMap<String, String>();

    /**
     * Constructor: init type map.
     */
    public RuleGenerator() {
        typeMap.put("SimpleReader",
                "by.bsu.fami.etl.processing.component.reader.StubReader");
        typeMap.put("SimpleWriter",
                "by.bsu.fami.etl.processing.component.writer.StubWriter");
        typeMap.put("JdbcReader",
                "by.bsu.fami.etl.processing.component.reader.JdbcReader");
        typeMap.put("XmlReader",
                "by.bsu.fami.etl.processing.component.reader.XmlReader");
        typeMap.put("WebServiceReader",
                "by.bsu.fami.etl.processing.component.reader.WebServiceReader");
        typeMap.put("JdbcWriter",
                "by.bsu.fami.etl.processing.component.writer.JdbcWriter");
        typeMap.put("XmlWriter",
                "by.bsu.fami.etl.processing.component.writer.XmlWriter");
        typeMap.put("JdbcDataSource",
                "by.bsu.fami.etl.processing.datasource.JdbcDataSource");
        typeMap.put("LocalFileDataSource",
                "by.bsu.fami.etl.processing.datasource.LocalFileDataSource");
        typeMap.put("WebServiceDataSource",
                "by.bsu.fami.etl.processing.datasource.WebServiceDataSource");
        typeMap.put("SimpleChecker",
                "by.bsu.fami.etl.processing.component.checker.SimpleChecker");
    }

    /**
     * Constructor to init type map.
     * @param aTypeMap
     *            - map: component alias - component java class name.
     */
    public RuleGenerator(final Map<String, String> aTypeMap) {
        this.typeMap = aTypeMap;
    }

    /**
     * Generate rule java byte-code by syntax tree.
     * @param destDir
     *            - folder to class file
     * @param rulePackage
     *            - rule package name.
     * @param rule
     *            - Rule bean contains syntax tree
     * @return rule class name
     * @throws GenerateException
     *             error in generate java byte-code by parsing syntax tree
     */
    public final String generateRule(
            final String destDir,
            final String rulePackage,
            final Rule rule) throws GenerateException {
        if (rule != null) {
            try {
                String rPackage = "";
                if (rulePackage != null) {
                    rPackage = rulePackage.replace("/", ".");
                }
                if (rPackage.length() == 0) {
                    rPackage = rPackage + ".";
                }
                LOGGER.info("Start genrate class file for rule "
                        + rule.getName()
                        + " in package "
                        + rulePackage);
                ClassGen ruleStubClass;
                if (rule.getChecker() == null) {
                    ruleStubClass =
                            new ClassGen(rulePackage + rule.getName(),
                                    baseRuleClass, "generated",
                                    Constants.ACC_PUBLIC, null);
                    // initialize components method
                    ruleStubClass.addMethod(generateInitComponentMethod(
                            ruleStubClass, rule.getComponents(), rule
                                    .getDataSources()));
                } else {
                    ruleStubClass =
                            new ClassGen(rulePackage + rule.getName(),
                                    checkRuleClass, "generated",
                                    Constants.ACC_PUBLIC, null);
                    // initialize components method with checker
                    ruleStubClass.addMethod(generateInitComponentMethod(
                            ruleStubClass, rule.getComponents(), rule
                                    .getDataSources(), rule.getChecker()));
                }
                // constructor
                ruleStubClass.addEmptyConstructor(Constants.ACC_PUBLIC);

                JavaClass clazz = ruleStubClass.getJavaClass();
                LOGGER.debug("Genarated class for rule "
                        + rule.getName()
                        + " in package "
                        + rulePackage
                        + " : "
                        + clazz.toString());
                File file =
                        new File(destDir
                                + "/"
                                + clazz.getClassName().replace(".", "/")
                                + ".class");
                clazz.dump(file);
                LOGGER.info("Success create rule class file : "
                        + file.getAbsolutePath());
                return clazz.getClassName();
            } catch (Exception e) {
                throw new GenerateException("Cann't generate rule "
                        + e.getMessage(), e);
            }
        } else {
            throw new GenerateException("Rule cann't be null!");
        }
    }

    /**
     * Generate for rule init component method for CheckRule.
     * @param ruleStubClass
     *            - BCEL rule class
     * @param components
     *            - list of rule components
     * @param dataSources
     *            - list of rule datasource
     * @param checker
     *            - checker component
     * @return generated intit component method
     * @throws GenerateException
     *             error in generate java byte-code by parsing syntax tree
     */
    protected final Method generateInitComponentMethod(
            final ClassGen ruleStubClass,
            final List<Component> components,
            final List<DataSource> dataSources,
            final Checker checker) throws GenerateException {
        InstructionFactory iFactory =
                new InstructionFactory(ruleStubClass, ruleStubClass
                        .getConstantPool());
        MethodGen initComponentMethod =
                initComponentMethod(ruleStubClass, components, dataSources);
        // Add checker
        ComponentGenerator ch =
                initComponent(initComponentMethod, checker, iFactory);
        InstructionList il = initComponentMethod.getInstructionList();
        try {
            String type = typeMap.get(checker.getType());
            il.append(InstructionFactory.createLoad(Type.getType(Class
                    .forName(checkRuleClass)), 0));
            il.append(InstructionFactory.createLoad(Type.getType(Class
                    .forName(type)), ch.getComponentInitVar().getIndex()));
            il.append(iFactory.createInvoke(checkRuleClass, "setChecker",
                    Type.VOID, new Type[] {Type.getType(Class
                            .forName(baseCheckerComponentClass))},
                    Constants.INVOKEVIRTUAL));
        } catch (ClassNotFoundException e) {
            throw new GenerateException(e);
        }
        initComponentMethod.getInstructionList().append(new RETURN());
        return initComponentMethod.getMethod();
    }

    /**
     * Generate for rule init component method for BaseRule.
     * @param ruleStubClass
     *            - BCEL rule class
     * @param components
     *            - list of rule components
     * @param dataSources
     *            - list of rule datasource
     * @return generated intit component method
     * @throws GenerateException
     *             error in generate java byte-code by parsing syntax tree
     */
    protected final Method generateInitComponentMethod(
            final ClassGen ruleStubClass,
            final List<Component> components,
            final List<DataSource> dataSources) throws GenerateException {
        MethodGen initComponentMethod =
                initComponentMethod(ruleStubClass, components, dataSources);
        initComponentMethod.getInstructionList().append(new RETURN());
        return initComponentMethod.getMethod();
    }

    /**
     * Inner method to generate for rule init component method.
     * @param ruleStubClass
     *            - BCEL rule class
     * @param components
     *            - list of rule components
     * @param dataSources
     *            - list of rule datasource
     * @return generated intit component method
     * @throws GenerateException
     *             error in generate java byte-code by parsing syntax tree
     */
    private MethodGen initComponentMethod(
            final ClassGen ruleStubClass,
            final List<Component> components,
            final List<DataSource> dataSources) throws GenerateException {
        InstructionList insList = new InstructionList();
        ConstantPoolGen ruleCp = ruleStubClass.getConstantPool();
        InstructionFactory iFactory =
                new InstructionFactory(ruleStubClass, ruleCp);
        MethodGen initComponentMethod =
                new MethodGen((int) Constants.ACC_PUBLIC, Type.VOID, null,
                        null, initComponents, ruleStubClass.getClassName(),
                        insList, ruleCp);
        initComponentMethod.setMaxStack(STACK_MAX_SIZE);
        for (DataSource dataSource : dataSources) {
            initDataSource(initComponentMethod, dataSource, iFactory);
        }
        Map<ComponentGenerator, Component> comps =
                new HashMap<ComponentGenerator, Component>();
        for (Component component : components) {
            comps.put(initComponent(initComponentMethod, component, iFactory),
                    component);
        }
        initCalledsComponent(initComponentMethod, comps, iFactory);
        return initComponentMethod;
    }

    /**
     * Create init local variable for component.
     * @param initComponentMethod
     *            - rule init component method
     * @param component
     *            - bean of component
     * @param iFactory
     *            - class is used to generate typed versions of instructions
     * @return generated intit component method
     * @throws GenerateException
     *             error in generate java byte-code by parsing syntax tree
     */
    protected final ComponentGenerator initComponent(
            final MethodGen initComponentMethod,
            final Component component,
            final InstructionFactory iFactory) throws GenerateException {
        String type = typeMap.get(component.getType());
        if (type == null) {
            String message =
                    "Incorrect type of component : " + component.getType();
            LOGGER.error(message);
            throw new GenerateException(message);
        }
        try {
            InstructionList il = initComponentMethod.getInstructionList();
            // Create component
            ComponentGenerator compGen =
                    ComponentGeneratorFactory.instanceComponentGenerator(
                            component, type);
            compGen.generateInitComponent(initComponentMethod, iFactory);
            // Add component to list
            il.append(InstructionFactory.createLoad(Type.getType(Class
                    .forName(baseRuleClass)), 0));
            il.append(iFactory.createGetField(baseRuleClass, "components", Type
                    .getType(Class.forName("java.util.List"))));
            il.append(InstructionFactory.createLoad(Type.getType(Class
                    .forName(type)), compGen.getComponentInitVar().getIndex()));
            il.append(iFactory.createInvoke("java.util.List", "add",
                    Type.BOOLEAN, new Type[] {Type.OBJECT},
                    Constants.INVOKEINTERFACE));
            il.append(InstructionConstants.POP);
            return compGen;
        } catch (ClassNotFoundException e) {
            LOGGER.error("Couldn't find component of class : "
                    + component.getType(), e);
            throw new GenerateException("Couldn't find component of class : "
                    + component.getType(), e);
        }
    }

    /**
     * Create init local variable for datasource.
     * @param initComponentMethod
     *            - rule init component method
     * @param dataSource
     *            - bean of dataSource
     * @param iFactory
     *            - class is used to generate typed versions of instructions
     * @throws GenerateException
     *             error in generate java byte-code by parsing syntax tree
     */
    protected final void initDataSource(
            final MethodGen initComponentMethod,
            final DataSource dataSource,
            final InstructionFactory iFactory) throws GenerateException {
        String type = typeMap.get(dataSource.getType());
        if (type == null) {
            String message =
                    "Incorrect type of datasource : " + dataSource.getType();
            LOGGER.error(message);
            throw new GenerateException(message);
        }
        try {
            InstructionList il = initComponentMethod.getInstructionList();
            // Create datasource
            DataSourceGenerator dsGen =
                    new DataSourceGenerator(dataSource, type);
            dsGen.generateInitDataSource(initComponentMethod, iFactory);
            // Add datasource to list
            il.append(InstructionFactory.createLoad(Type.getType(Class
                    .forName(baseRuleClass)), 0));
            il.append(iFactory.createGetField(baseRuleClass, "dataSources",
                    Type.getType(Class.forName("java.util.List"))));
            il.append(InstructionFactory.createLoad(Type.getType(Class
                    .forName(type)), dsGen.getDataSourceInitVar().getIndex()));
            il.append(iFactory.createInvoke("java.util.List", "add",
                    Type.BOOLEAN, new Type[] {Type.OBJECT},
                    Constants.INVOKEINTERFACE));
            il.append(InstructionConstants.POP);
        } catch (ClassNotFoundException e) {
            String message =
                    "Couldn't find datasource of class : "
                            + dataSource.getType();
            LOGGER.error(message, e);
            throw new GenerateException(message, e);
        }
    }

    /**
     * Init calleds componenst for all rule components.
     * @param initComponentMethod
     *            - rule init component method
     * @param comps
     *            - map: component local variable - component bean
     * @param iFactory
     *            - class is used to generate typed versions of instructions
     * @throws GenerateException
     *             error in generate java byte-code by parsing syntax tree
     */
    protected final void initCalledsComponent(
            final MethodGen initComponentMethod,
            final Map<ComponentGenerator, Component> comps,
            final InstructionFactory iFactory) throws GenerateException {
        InstructionList il = initComponentMethod.getInstructionList();
        for (ComponentGenerator compGen : comps.keySet()) {
            Component component = comps.get(compGen);
            String type = typeMap.get(component.getType());
            for (String comp : component.getCalleds()) {
                try {
                    il.append(InstructionFactory.createLoad(Type.getType(Class
                            .forName(type)), compGen.getComponentInitVar()
                            .getIndex()));
                    il.append(InstructionFactory.createLoad(Type.getType(Class
                            .forName(baseRuleClass)), 0));
                    il.append(new LDC(initComponentMethod.getConstantPool()
                            .addString(comp)));
                    il.append(iFactory.createInvoke(baseRuleClass,
                            "findComponent", Type.getType(Class
                                    .forName(baseComponentClass)),
                            new Type[] {Type.STRING}, Constants.INVOKEVIRTUAL));
                    il.append(iFactory.createInvoke(type, "addCall",
                            Type.BOOLEAN, new Type[] {Type.getType(Class
                                    .forName(baseComponentClass))},
                            Constants.INVOKEVIRTUAL));
                    il.append(InstructionConstants.POP);
                } catch (ClassNotFoundException e) {
                    String message =
                            "Couldn't find component of class : "
                                    + component.getType();
                    LOGGER.error(message, e);
                    throw new GenerateException(message, e);
                }
            }
        }
    }

    /**
     * Rule init method name.
     * @return rule init method nam
     */
    public final String getInitComponents() {
        return initComponents;
    }

    /**
     * java class name of datasource class.
     * @return java class name
     */
    public final String getBaseDataSourceClass() {
        return baseDataSourceClass;
    }
}
