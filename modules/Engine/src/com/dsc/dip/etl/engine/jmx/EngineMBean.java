package com.dsc.dip.etl.engine.jmx;

import java.util.List;

import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import com.dsc.dip.etl.engine.Engine;
import com.dsc.dip.etl.engine.EngineException;
import com.dsc.dip.etl.engine.IEngine;


public class EngineMBean extends StandardMBean implements IEngine {

	protected Engine engine;

	public EngineMBean(String repositoryPath, String configDirPath,
			String baseConfigFile) throws NotCompliantMBeanException,
			EngineException {
		super(IEngine.class);
		engine = Engine.initInstance(repositoryPath, configDirPath,
				baseConfigFile);
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
		executeRule(ruleName, true);
	}

	public void executeRule(String ruleName, boolean isNeedCompile)
			throws EngineException {
		if (engine != null) {
			engine.executeRule(ruleName, isNeedCompile);
		}
	}

	public String reCompileRule(String ruleName) throws EngineException {
		if (engine != null) {
			engine.reCompileRule(ruleName);
		}
		return null;
	}

}
