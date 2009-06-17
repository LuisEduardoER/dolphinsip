package com.dsc.dip.etl.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.dsc.dip.etl.compiler.CompilerException;
import com.dsc.dip.etl.engine.utils.FileSystemHelper;
import com.dsc.dip.etl.processing.BaseRule;


/**
 * 
 * Base class to run Rule class file.
 * 
 * @author Pavel_Drabushevich
 * 
 */
public class Engine {

	private final static Logger LOGGER = Logger.getLogger(Engine.class);

	public final static String REPOSITORY_BIN = "repository.bin";

	public final static String REPOSITORY_SRC = "repository.src";

	public final static String TYPE_ALIAS_PREFIX = "compiler.component.type";

	protected String repositoryPath;

	protected String configDirPath;

	protected String engineConfig;

	protected String baseConfig;

	protected Properties baseProperties;

	protected String repositoryBin;

	protected String repositorySrc;

	protected com.dsc.dip.etl.compiler.Compiler compiler;

	private Engine() {
	}

	/**
	 * 
	 * Create new Engine instance.
	 * 
	 * @param repositoryPath
	 * @param configDirPath
	 * @param engineConfig
	 * @return
	 * @throws EngineException
	 */
	public static Engine initInstance(String repositoryPath,
			String configDirPath, String baseConfig) throws EngineException {
		Engine e = new Engine();
		File repository;
		if (repositoryPath == null
				|| !(repository = new File(repositoryPath)).exists()
				|| !repository.isDirectory()) {
			LOGGER.error("RepositoryPath property = " + repositoryPath
					+ " isn't valid path to exist directory!");
			throw new IllegalArgumentException("RepositoryPath property = "
					+ repositoryPath + " isn't valid path to exist directory!");
		}
		e.repositoryPath = repositoryPath;
		File configDir;
		e.configDirPath = e.repositoryPath + File.separator + configDirPath;
		if (configDirPath == null
				|| !(configDir = new File(e.configDirPath)).exists()
				|| !configDir.isDirectory()) {
			LOGGER.error("ConfigDirPath property = " + e.configDirPath
					+ " isn't valid path to exist directory!");
			throw new IllegalArgumentException("ConfigDirPath property = "
					+ e.configDirPath + " isn't valid path to exist directory!");
		}
		File baseConfigFile;
		e.baseConfig = e.configDirPath + File.separator + baseConfig;
		if (baseConfig == null
				|| !(baseConfigFile = new File(e.baseConfig)).exists()
				|| !baseConfigFile.isFile()) {
			LOGGER.error("BaseConfig property = " + e.baseConfig
					+ " isn't valid path to exist file!");
			throw new IllegalArgumentException("BaseConfig property = "
					+ e.baseConfig + " isn't valid path to exist file!");
		}
		e.initBaseProperties();
		e.initCompiler(repositoryPath, configDirPath);
		return e;
	}

	public void executeRule(String ruleName) throws EngineException {
		executeRule(ruleName, true);
	}

	/**
	 * 
	 * Execute rule
	 * 
	 * @param ruleName
	 * @throws EngineException
	 */
	public void executeRule(String ruleName, boolean isNeedCompile)
			throws EngineException {
		try {
			LOGGER.info("Start execute rule" + ruleName + "....");
			String className;
			if (isNeedCompile) {
				className = compileRule(ruleName);
			} else {
				className = ruleName;
			}
			Class<?> ruleClass = loadClass(className);
			LOGGER.info("Rule success compile, create new instance...");
			BaseRule rule = (BaseRule) ruleClass.newInstance();
			Method initMethod = ruleClass.getMethod(compiler
					.getInitRuleMethodName());
			initMethod.invoke(rule);
			LOGGER
					.info("Success init new instance of rule, start execute rule...");
			Method executeMethod = ruleClass.getMethod(compiler
					.getExecuteRuleMethodName());
			executeMethod.invoke(rule);
			LOGGER.info("Success execute rule " + ruleClass);
		} catch (InstantiationException e) {
			LOGGER.error("Couldn't create new instance of rule.", e);
			throw new EngineException("Couldn't create new instance of rule.",
					e);
		} catch (IllegalAccessException e) {
			LOGGER.error("Couldn't get access to rule class.", e);
			throw new EngineException("Couldn't get access to rule class.", e);
		} catch (SecurityException e) {
			LOGGER.error("Couldn't get access to rule method.", e);
			throw new EngineException("Couldn't get access to rule method.", e);
		} catch (NoSuchMethodException e) {
			LOGGER.error("Couldn't find rule init method.", e);
			throw new EngineException("Couldn't find rule method.", e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Couldn't inccorect rule method arguments.", e);
			throw new EngineException(
					"Couldn't inccorect rule method arguments.", e);
		} catch (InvocationTargetException e) {
			LOGGER.error("Couldn't execute rule method.", e);
			throw new EngineException("Couldn't execute rule method.", e);
		}
	}

	public String compileRule(String fileName) throws EngineException {
		String className;
		try {
			className = compiler
					.compile(repositorySrc, repositoryBin, fileName);
			return className;
		} catch (CompilerException e) {
			String message = "Couldn't compiler rule " + fileName;
			LOGGER.error(message, e);
			throw new EngineException(message, e);
		}
	}

	public List<String> compileAll() throws EngineException {
		List<String> rules = new ArrayList<String>();
		File srcDirectory = new File(repositorySrc);
		List<File> files = FileSystemHelper.listFiles(srcDirectory,
				new FilenameFilter() {

					protected Pattern source = Pattern.compile(".*\\.rule");

					public boolean accept(File dir, String name) {
						return source.matcher(name).matches();
					}

				}, true);
		for (File file : files) {
			String ruleName = file.getAbsolutePath().substring(
					srcDirectory.getAbsolutePath().length() + 1);
			String ruleClass = compileRule(ruleName);
			if (ruleClass != null) {
				rules.add(ruleClass);
			}
		}
		return rules;
	}

	public String reCompileRule(String fileName) throws EngineException {
		File src = new File(repositorySrc + File.separator + fileName), dest = new File(
				repositoryBin + File.separator + fileName);
		if (dest.exists() && dest.lastModified() >= src.lastModified()) {
			return fileName.substring(0, fileName.lastIndexOf(".")).replace(
					"\\", ".");
		}
		return compileRule(fileName);
	}

	protected Class<?> loadClass(String className) throws EngineException {
		File file = new File(repositoryBin);
		try {
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			ClassLoader cl = new URLClassLoader(urls);
			return cl.loadClass(className);
		} catch (MalformedURLException e) {
			LOGGER.error("Incorrect bin repository.", e);
			throw new EngineException("Incorrect bin repository.", e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("Couldn't class file " + className
					+ " in bin repository.", e);
			throw new EngineException("Couldn't class file " + className
					+ " in bin repository.", e);
		}
	}

	/**
	 * 
	 * Init base properties from config file. <br>
	 * If property miss in config, set default value.
	 * 
	 * @param c
	 *            - instance of Compiler to init properties.
	 * @param baseConfigFile
	 *            - file with base properties.
	 */
	protected void initBaseProperties() {
		baseProperties = new Properties();
		try {
			baseProperties.load(new FileInputStream(baseConfig));
		} catch (FileNotFoundException e) {
			LOGGER.warn("Didn't find base property file! "
					+ "All property initialize with default values!", e);
		} catch (IOException e) {
			LOGGER.warn("Couldn't read base property file! "
					+ "All property initialize with default values!", e);
		}
		repositorySrc = nvl(REPOSITORY_SRC, repositoryPath, repositoryPath
				+ File.separator);
		if (!new File(repositorySrc).exists()
				|| !new File(repositorySrc).isDirectory()) {
			LOGGER
					.warn("Repository src property isn't valid path to exist directory!");
			repositorySrc = repositoryPath;
		}
		repositoryBin = nvl(REPOSITORY_BIN, repositoryPath, repositoryPath
				+ File.separator);
		if (!new File(repositoryBin).exists()
				|| !new File(repositoryBin).isDirectory()) {
			LOGGER
					.warn("Repository bin property isn't valid path to exist directory!");
			repositoryBin = repositoryPath;
		}

	}

	/**
	 * 
	 * Init link between component alias in rule and real class name, which
	 * implements this component.
	 * 
	 */
	protected Map<String, String> initComponentTypes() {
		Map<String, String> typeMap = new HashMap<String, String>();
		for (Map.Entry<Object, Object> property : baseProperties.entrySet()) {
			String key = property.getKey().toString();
			if (key.startsWith(TYPE_ALIAS_PREFIX)) {
				key = key.substring(TYPE_ALIAS_PREFIX.length() + 1, key
						.length());
				String value = property.getValue().toString();
				if (StringUtils.isNotEmpty(key)
						&& StringUtils.isNotEmpty(value)) {
					typeMap.put(key, value);
				}
			}
		}
		return typeMap;
	}

	protected void initCompiler(String repositoryPath, String configDirPath)
			throws EngineException {
		try {
			compiler = com.dsc.dip.etl.compiler.Compiler
					.initInstance(initComponentTypes());
		} catch (Exception e) {
			LOGGER.error("Couldn't init compiler for engine.", e);
			throw new EngineException("Couldn't init compiler for engine.", e);
		}
	}

	protected String nvl(String property, String defaultValue, String prefix) {
		String tmp = baseProperties.getProperty(property);
		tmp = StringUtils.isEmpty(tmp) ? defaultValue : prefix + tmp;
		return tmp;
	}

	protected String nvl(String property, String defaultValue) {
		return nvl(property, defaultValue, "");
	}
}
