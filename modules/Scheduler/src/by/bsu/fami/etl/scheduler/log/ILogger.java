package by.bsu.fami.etl.scheduler.log;

import java.util.Date;
import java.util.Properties;

public interface ILogger {

	public final static String RULE_TYPE_BASE = "BaseRule";

	public final static String RULE_TYPE_CHECK = "CheckRule";

	public final static String RULE_STATUS_WAIT = "Wait";

	public final static String RULE_STATUS_START = "Start";

	public final static String RULE_STATUS_SUCCESS = "Success";

	public final static String RULE_STATUS_SKIP = "Skip";

	public final static String RULE_STATUS_FAILURE = "Failure";

	public void init(Properties config) throws LoggerException;

	public void logRule(String ruleName, Date date, String jobName,
			String metadataName, String status, String type, String failure)
			throws LoggerException;
	
}
