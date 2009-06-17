package com.dsc.dip.etl.engine.felix;

import java.util.List;

import com.dsc.dip.etl.engine.Engine;
import com.dsc.dip.etl.engine.EngineException;
import com.dsc.dip.etl.engine.IEngine;

public class EngineService implements IEngine {
	
	public final static String ENGINE_REPOSITORY = "engine.repository";
	
	public final static String ENGINE_CONFIG_DIR = "engine.config.dir";
	
	public final static String ENGINE_BASE_CONFIG = "engine.base.config";

	protected Engine engine;

	public EngineService(Engine engine) {
		this.engine = engine;
	}

	public List<String> compileAllRules() throws EngineException {
		if (engine != null) {
			return engine.compileAll();
		}
		return null;
	}

	public String compileRule(String ruleName) throws EngineException {
		if (engine != null) {
			return engine.compileRule(ruleName);
		}
		return null;
	}

	public void executeRule(String ruleName) throws EngineException {
		if (engine != null) {
			engine.executeRule(ruleName);
		}
	}

	public void executeRule(String ruleName, boolean isNeedCompile)
			throws EngineException {
		if (engine != null) {
			engine.executeRule(ruleName, isNeedCompile);
		}
	}

	public String reCompileRule(String ruleName) throws EngineException {
		if (engine != null) {
			return engine.reCompileRule(ruleName);
		}
		return null;
	}

}
