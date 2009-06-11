package by.bsu.fami.etl.engine;

import java.util.List;


public interface IEngine {

	public List<String> compileAllRules() throws EngineException;

	public String compileRule(String ruleName) throws EngineException;

	public String reCompileRule(String ruleName) throws EngineException;

	public void executeRule(String ruleName) throws EngineException;

	public void executeRule(String ruleName, boolean isNeedCompile)
			throws EngineException;

}
