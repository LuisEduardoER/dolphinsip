package by.bsu.fami.etl.engine.felix;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

public class EngineActivator implements BundleActivator {

	private final static Logger LOGGER = Logger
			.getLogger(EngineActivator.class);

	protected LogService loggerService;

	public void start(BundleContext context) throws Exception {
		PropertyConfigurator.configure(context.getBundle().getResource(
				"log4j.properties"));
		LOGGER.info("Success init log4j.");
		ServiceReference ref = context.getServiceReference(LogService.class
				.getName());
		if (ref != null) {
			loggerService = (LogService) context.getService(ref);
			if (loggerService != null) {
				loggerService.log(LogService.LOG_INFO,
						"Success init logging service.");
			} else {
				LOGGER.warn("Couldn't init Logger Service.");
			}
		} else {
			LOGGER.warn("Couldn't init Logger ServiceReference.");
		}

	}

	public void stop(BundleContext context) throws Exception {
	}
}
