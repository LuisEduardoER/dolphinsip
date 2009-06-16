package com.dsc.dip.etl.engine.felix;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class EngineActivator implements BundleActivator {

	private final static Logger LOGGER = Logger
			.getLogger(EngineActivator.class);

	public void start(BundleContext context) throws Exception {
		PropertyConfigurator.configure(context.getBundle().getResource(
				"log4j.properties"));
		LOGGER.info("Success init log4j.");

	}

	public void stop(BundleContext context) throws Exception {
	}
}
