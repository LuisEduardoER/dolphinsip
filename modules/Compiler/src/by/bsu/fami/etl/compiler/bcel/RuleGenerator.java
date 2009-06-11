package by.bsu.fami.etl.compiler.bcel;

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

import by.bsu.fami.etl.compiler.bcel.generator.ComponentGenerator;
import by.bsu.fami.etl.compiler.bcel.generator.ComponentGeneratorFactory;
import by.bsu.fami.etl.compiler.bcel.generator.DataSourceGenerator;
import by.bsu.fami.etl.compiler.bean.Checker;
import by.bsu.fami.etl.compiler.bean.Component;
import by.bsu.fami.etl.compiler.bean.DataSource;
import by.bsu.fami.etl.compiler.bean.Rule;

public class RuleGenerator {

	private final static Logger LOGGER = Logger.getLogger(RuleGenerator.class);

	public static String BASE_RULE_CLASS = "by.bsu.fami.etl.processing.BaseRule";

	public static String CHECK_RULE_CLASS = "by.bsu.fami.etl.processing.CheckRule";

	public static String BASE_COMPONENT_CLASS = "by.bsu.fami.etl.processing.component.Component";

	public static String BASE_DATASOURCE_CLASS = "by.bsu.fami.etl.processing.datasource.DataSource";

	public static String BASE_CHECKER_COMPONENT_CLASS = "by.bsu.fami.etl.processing.component.checker.Checker";

	protected String initComponents = "initComponents";

	protected Map<String, String> typeMap = new HashMap<String, String>();

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

	public RuleGenerator(Map<String, String> typeMap) {
		this.typeMap = typeMap;
	}

	public String generateRule(String destDir, String rulePackage, Rule rule)
			throws GenerateException {
		if (rule != null) {
			try {
				rulePackage = rulePackage == null ? "" : rulePackage.replace(
						"/", ".");
				rulePackage = rulePackage.length() == 0 ? rulePackage
						: rulePackage + ".";
				LOGGER.info("Start genrate class file for rule "
						+ rule.getName() + " in package " + rulePackage);
				ClassGen ruleStubClass;
				if (rule.getChecker() == null) {
					ruleStubClass = new ClassGen(rulePackage + rule.getName(),
							BASE_RULE_CLASS, "generated", Constants.ACC_PUBLIC,
							null);
					// initialize components method
					ruleStubClass.addMethod(generateInitComponentMethod(
							ruleStubClass, rule.getComponents(), rule
									.getDataSources()));
				} else {
					ruleStubClass = new ClassGen(rulePackage + rule.getName(),
							CHECK_RULE_CLASS, "generated",
							Constants.ACC_PUBLIC, null);
					// initialize components method with checker
					ruleStubClass.addMethod(generateInitComponentMethod(
							ruleStubClass, rule.getComponents(), rule
									.getDataSources(), rule.getChecker()));
				}
				// constructor
				ruleStubClass.addEmptyConstructor(Constants.ACC_PUBLIC);

				JavaClass clazz = ruleStubClass.getJavaClass();
				LOGGER.debug("Genarated class for rule " + rule.getName()
						+ " in package " + rulePackage + " : "
						+ clazz.toString());
				File file = new File(destDir + "/"
						+ clazz.getClassName().replace(".", "/") + ".class");
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

	protected Method generateInitComponentMethod(ClassGen ruleStubClass,
			List<Component> components, List<DataSource> dataSources,
			Checker checker) throws GenerateException {
		InstructionFactory iFactory = new InstructionFactory(ruleStubClass,
				ruleStubClass.getConstantPool());
		MethodGen initComponentMethod = initComponentMethod(ruleStubClass,
				components, dataSources);
		// Add checker
		ComponentGenerator ch = initComponent(initComponentMethod, checker,
				iFactory);
		InstructionList il = initComponentMethod.getInstructionList();
		try {
			String type = typeMap.get(checker.getType());
			il.append(InstructionFactory.createLoad(Type.getType(Class
					.forName(RuleGenerator.CHECK_RULE_CLASS)), 0));
			il.append(InstructionFactory.createLoad(Type.getType(Class
					.forName(type)), ch.getComponentInitVar().getIndex()));
			il.append(iFactory.createInvoke(CHECK_RULE_CLASS, "setChecker",
					Type.VOID, new Type[] { Type.getType(Class
							.forName(BASE_CHECKER_COMPONENT_CLASS)) },
					Constants.INVOKEVIRTUAL));
		} catch (ClassNotFoundException e) {
			throw new GenerateException(e);
		}
		initComponentMethod.getInstructionList().append(new RETURN());
		return initComponentMethod.getMethod();
	}

	protected Method generateInitComponentMethod(ClassGen ruleStubClass,
			List<Component> components, List<DataSource> dataSources)
			throws GenerateException {
		MethodGen initComponentMethod = initComponentMethod(ruleStubClass,
				components, dataSources);
		initComponentMethod.getInstructionList().append(new RETURN());
		return initComponentMethod.getMethod();
	}

	private MethodGen initComponentMethod(ClassGen ruleStubClass,
			List<Component> components, List<DataSource> dataSources)
			throws GenerateException {
		InstructionList insList = new InstructionList();
		ConstantPoolGen ruleCp = ruleStubClass.getConstantPool();
		InstructionFactory iFactory = new InstructionFactory(ruleStubClass,
				ruleCp);
		MethodGen initComponentMethod = new MethodGen(
				(int) Constants.ACC_PUBLIC, Type.VOID, null, null,
				initComponents, ruleStubClass.getClassName(), insList, ruleCp);
		initComponentMethod.setMaxStack(1000);
		for (DataSource dataSource : dataSources) {
			initDataSource(initComponentMethod, dataSource, iFactory);
		}
		Map<ComponentGenerator, Component> comps = new HashMap<ComponentGenerator, Component>();
		for (Component component : components) {
			comps.put(initComponent(initComponentMethod, component, iFactory),
					component);
		}
		initCalledsComponent(initComponentMethod, comps, iFactory);
		return initComponentMethod;
	}

	protected ComponentGenerator initComponent(MethodGen initComponentMethod,
			Component component, InstructionFactory iFactory)
			throws GenerateException {
		String type = typeMap.get(component.getType());
		if (type == null) {
			String message = "Incorrect type of component : "
					+ component.getType();
			LOGGER.error(message);
			throw new GenerateException(message);
		}
		try {
			InstructionList il = initComponentMethod.getInstructionList();
			// Create component
			ComponentGenerator compGen = ComponentGeneratorFactory
					.instanceComponentGenerator(component, type);
			compGen.generateInitComponent(initComponentMethod, iFactory);
			// Add component to list
			il.append(InstructionFactory.createLoad(Type.getType(Class
					.forName(RuleGenerator.BASE_RULE_CLASS)), 0));
			il.append(iFactory
					.createGetField(RuleGenerator.BASE_RULE_CLASS,
							"components", Type.getType(Class
									.forName("java.util.List"))));
			il.append(InstructionFactory.createLoad(Type.getType(Class
					.forName(type)), compGen.getComponentInitVar().getIndex()));
			il.append(iFactory.createInvoke("java.util.List", "add",
					Type.BOOLEAN, new Type[] { Type.OBJECT },
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

	protected void initDataSource(MethodGen initComponentMethod,
			DataSource dataSource, InstructionFactory iFactory)
			throws GenerateException {
		String type = typeMap.get(dataSource.getType());
		if (type == null) {
			String message = "Incorrect type of datasource : "
					+ dataSource.getType();
			LOGGER.error(message);
			throw new GenerateException(message);
		}
		try {
			InstructionList il = initComponentMethod.getInstructionList();
			// Create datasource
			DataSourceGenerator dsGen = new DataSourceGenerator(dataSource,
					type);
			dsGen.generateInitComponent(initComponentMethod, iFactory);
			// Add datasource to list
			il.append(InstructionFactory.createLoad(Type.getType(Class
					.forName(RuleGenerator.BASE_RULE_CLASS)), 0));
			il.append(iFactory.createGetField(RuleGenerator.BASE_RULE_CLASS,
					"dataSources", Type
							.getType(Class.forName("java.util.List"))));
			il.append(InstructionFactory.createLoad(Type.getType(Class
					.forName(type)), dsGen.getDataSourceInitVar().getIndex()));
			il.append(iFactory.createInvoke("java.util.List", "add",
					Type.BOOLEAN, new Type[] { Type.OBJECT },
					Constants.INVOKEINTERFACE));
			il.append(InstructionConstants.POP);
		} catch (ClassNotFoundException e) {
			String message = "Couldn't find datasource of class : "
					+ dataSource.getType();
			LOGGER.error(message, e);
			throw new GenerateException(message, e);
		}
	}

	protected void initCalledsComponent(MethodGen initComponentMethod,
			Map<ComponentGenerator, Component> comps,
			InstructionFactory iFactory) throws GenerateException {
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
							.forName(BASE_RULE_CLASS)), 0));
					il.append(new LDC(initComponentMethod.getConstantPool()
							.addString(comp)));
					il.append(iFactory
							.createInvoke(BASE_RULE_CLASS, "findComponent",
									Type.getType(Class
											.forName(BASE_COMPONENT_CLASS)),
									new Type[] { Type.STRING },
									Constants.INVOKEVIRTUAL));
					il.append(iFactory.createInvoke(type, "addCall",
							Type.BOOLEAN, new Type[] { Type.getType(Class
									.forName(BASE_COMPONENT_CLASS)) },
							Constants.INVOKEVIRTUAL));
					il.append(InstructionConstants.POP);
				} catch (ClassNotFoundException e) {
					String message = "Couldn't find component of class : "
							+ component.getType();
					LOGGER.error(message, e);
					throw new GenerateException(message, e);
				}
			}
		}
	}

	public String getInitComponents() {
		return initComponents;
	}
}
