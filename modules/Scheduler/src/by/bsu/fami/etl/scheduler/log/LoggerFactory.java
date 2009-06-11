package by.bsu.fami.etl.scheduler.log;

import java.util.Properties;

import org.apache.commons.lang.StringUtils;

public class LoggerFactory {

	public static final String SYSTEM_PROPERTY_LOGGER_NAME = "scheduler.log.Logger";

	@SuppressWarnings("unchecked")
	public static ILogger newLoggerInstance(Properties config)
			throws LoggerException {
		String className = System.getProperty(SYSTEM_PROPERTY_LOGGER_NAME);
		if (StringUtils.isNotEmpty(className)) {
			try {
				Class<ILogger> loggerClass = (Class<ILogger>) Class
						.forName(className);
				ILogger logger = loggerClass.newInstance();
				logger.init(config);
				return logger;
			} catch (ClassNotFoundException e) {
				throw new LoggerException("Couldn't find Loger class "
						+ className, e);
			} catch (InstantiationException e) {
				throw new LoggerException("Couldn't create new inctance "
						+ "of Loger class " + className, e);
			} catch (IllegalAccessException e) {
				throw new LoggerException("Couldn't get access to"
						+ " Loger class " + className, e);
			}
		}
		throw new LoggerException("System property "
				+ SYSTEM_PROPERTY_LOGGER_NAME
				+ " need initialisation for use Logger");
	}

}
