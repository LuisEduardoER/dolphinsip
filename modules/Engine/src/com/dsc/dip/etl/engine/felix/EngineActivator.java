package com.dsc.dip.etl.engine.felix;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.dsc.dip.etl.engine.Engine;
import com.dsc.dip.etl.engine.EngineException;

public class EngineActivator implements BundleActivator {

    private final static Logger LOGGER = Logger
	    .getLogger(EngineActivator.class);

    protected EngineService engineService;

    public void start(BundleContext context) throws Exception {
	PropertyConfigurator.configure(context.getBundle().getResource(
		"log4j.properties"));
	LOGGER.info("Success init log4j.");

	try {
	    engineService = new EngineService(Engine.initInstance(context
		    .getProperty(EngineService.ENGINE_REPOSITORY), context
		    .getProperty(EngineService.ENGINE_CONFIG_DIR), context
		    .getProperty(EngineService.ENGINE_BASE_CONFIG)));

	    context.registerService(EngineService.class.getName(),
		    engineService, null);
	    LOGGER.info("Success init core Engine service.");
	} catch (EngineException e) {
	    LOGGER.error("Couldn't initialize Engine service. "
		    + e.getMessage(), e);
	}

    }

    public void stop(BundleContext context) throws Exception {
	engineService = null;
    }
}
