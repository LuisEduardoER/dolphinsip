package by.bsu.fami.etl.scheduler.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.log4j.Logger;

import by.bsu.fami.etl.engine.EngineException;
import by.bsu.fami.etl.engine.IEngine;

public class EngineClientJMX implements IEngine {

	private final static Logger LOGGER = Logger
			.getLogger(EngineClientJMX.class);

	protected IEngine engineProxy;

	private EngineClientJMX() {
	}

	public static EngineClientJMX init(String url, String engineBean)
			throws EngineException {
		EngineClientJMX client = new EngineClientJMX();
		try {
			LOGGER.info("Init engine client " + url);
			JMXServiceURL jmxURL = new JMXServiceURL(url);
			JMXConnector jmxc = JMXConnectorFactory.connect(jmxURL, null);
			MBeanServerConnection mbsc = jmxc.getMBeanServerConnection();
			ObjectName mbeanName = new ObjectName(engineBean);
			client.engineProxy = JMX.newMBeanProxy(mbsc, mbeanName,
					IEngine.class, true);
			LOGGER.info("Success init engine client " + url);
		} catch (MalformedURLException e) {
			String message = "Incorect url to jmx engine service " + url;
			LOGGER.error(message, e);
			throw new EngineException(message, e);
		} catch (IOException e) {
			String message = "Couldn't connect to jmx engine service " + url;
			LOGGER.error(message, e);
			throw new EngineException(message, e);
		} catch (MalformedObjectNameException e) {
			String message = "Couldn't create engine MBean object "
					+ engineBean + " to jmx engine service " + url;
			LOGGER.error(message, e);
			throw new EngineException(message, e);
		}
		return client;
	}

	public List<String> compileAllRules() throws EngineException {
		LOGGER.info("Compile all rules ");
		if (engineProxy != null) {
			return engineProxy.compileAllRules();
		}
		return null;
	}

	public String compileRule(String ruleName) throws EngineException {
		LOGGER.info("Compile rule " + ruleName);
		if (engineProxy != null) {
			return engineProxy.compileRule(ruleName);
		}
		return null;
	}

	public void executeRule(String ruleName) throws EngineException {
		LOGGER.info("Execute rule " + ruleName);
		if (engineProxy != null) {
			engineProxy.executeRule(ruleName);
		}
	}

	public void executeRule(String ruleName, boolean isNeedCompile)
			throws EngineException {
		LOGGER.info("Execute rule " + ruleName);
		if (engineProxy != null) {
			engineProxy.executeRule(ruleName, isNeedCompile);
		}
	}

	public String reCompileRule(String ruleName) throws EngineException {
		LOGGER.info("Recompile rule " + ruleName);
		if (engineProxy != null) {
			return engineProxy.reCompileRule(ruleName);
		}
		return null;
	}

}
