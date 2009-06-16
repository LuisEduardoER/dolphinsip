package by.bsu.fami.etl.checker;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.log4j.Logger;

import com.dsc.dip.etl.processing.CheckRule;


public class Checker {

	private final static Logger LOGGER = Logger.getLogger(Checker.class);

	protected String classPath;

	public Checker(String classPath) {
		this.classPath = classPath;
	}

	public boolean check(String ruleClassName) throws CheckerException {
		try {
			LOGGER.info("Start execute check rule " + ruleClassName + "....");
			Class<?> ruleClass = loadClass(ruleClassName);
			LOGGER.info("Create new instance of check rule...");
			CheckRule rule = (CheckRule) ruleClass.newInstance();
			Method initMethod = ruleClass.getMethod("initComponents");
			initMethod.invoke(rule);
			LOGGER.info("Success init new instance of check rule,"
					+ " start check rule...");
			Method executeMethod = ruleClass.getMethod("check");
			Object checkResult = executeMethod.invoke(rule);
			LOGGER.info("Success check rule " + ruleClass + " with result "
					+ checkResult);
			return (Boolean) checkResult;
		} catch (InstantiationException e) {
			LOGGER.error("Couldn't create new instance of rule.", e);
			throw new CheckerException("Couldn't create new instance of rule.",
					e);
		} catch (IllegalAccessException e) {
			LOGGER.error("Couldn't get access to rule class.", e);
			throw new CheckerException("Couldn't get access to rule class.", e);
		} catch (SecurityException e) {
			LOGGER.error("Couldn't get access to rule method.", e);
			throw new CheckerException("Couldn't get access to rule method.", e);
		} catch (NoSuchMethodException e) {
			LOGGER.error("Couldn't find rule init method.", e);
			throw new CheckerException("Couldn't find rule method.", e);
		} catch (IllegalArgumentException e) {
			LOGGER.error("Couldn't inccorect rule method arguments.", e);
			throw new CheckerException(
					"Couldn't inccorect rule method arguments.", e);
		} catch (InvocationTargetException e) {
			LOGGER.error("Couldn't execute rule method.", e);
			throw new CheckerException("Couldn't execute rule method.", e);
		}
	}

	protected Class<?> loadClass(String className) throws CheckerException {
		File file = new File(classPath);
		try {
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			ClassLoader cl = new URLClassLoader(urls);
			return cl.loadClass(className);
		} catch (MalformedURLException e) {
			LOGGER.error("Incorrect classPath.", e);
			throw new CheckerException("Incorrect classPath.", e);
		} catch (ClassNotFoundException e) {
			LOGGER.error("Couldn't class file " + className
					+ " in bin repository.", e);
			throw new CheckerException("Couldn't class file " + className
					+ " in classPath.", e);
		}
	}
}
