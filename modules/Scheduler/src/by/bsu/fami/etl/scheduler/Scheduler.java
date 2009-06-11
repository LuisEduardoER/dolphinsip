package by.bsu.fami.etl.scheduler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.log4j.Logger;

import by.bsu.fami.etl.checker.Checker;
import by.bsu.fami.etl.checker.CheckerException;
import by.bsu.fami.etl.engine.EngineException;
import by.bsu.fami.etl.engine.IEngine;
import by.bsu.fami.etl.engine.utils.FileSystemHelper;
import by.bsu.fami.etl.scheduler.jmx.EngineClientJMX;
import by.bsu.fami.etl.scheduler.job.Job;
import by.bsu.fami.etl.scheduler.job.Metadata;
import by.bsu.fami.etl.scheduler.log.ILogger;
import by.bsu.fami.etl.scheduler.log.LoggerException;
import by.bsu.fami.etl.scheduler.log.LoggerFactory;

public class Scheduler {

	private static final String REPOSITORY_BIN = "repository.bin";

	private static final String REPOSITORY_SRC = "repository.src";

	private static final String ENGINE_JMX_URL = "engine.jmx.url";

	private static final String ENGINE_JMX_BEAN = "engine.jmx.bean";

	private final static Logger LOGGER = Logger.getLogger(Scheduler.class);

	protected IEngine engine;

	protected Checker checker;

	protected ILogger logger;

	protected Map<String, Metadata> metadatas;

	protected ArrayBlockingQueue<String> checkers;

	protected ArrayBlockingQueue<String> checkToRun;

	protected Scheduler(IEngine engine, Checker checker) {
		this.engine = engine;
		this.checker = checker;
	}

	public static Scheduler initScheduler(String configFile)
			throws SchedulerException {
		Scheduler scheduler = null;
		LOGGER.info("Start init scheduler....");
		try {
			Properties config = new Properties();
			config.load(new FileInputStream(configFile));
			String url = config.getProperty(ENGINE_JMX_URL), bean = config
					.getProperty(ENGINE_JMX_BEAN);
			IEngine engine = EngineClientJMX.init(url, bean);
			LOGGER.info("Success init engine");
			String repositoryBin = config.getProperty(REPOSITORY_BIN);
			String repositorySrc = config.getProperty(REPOSITORY_SRC);
			Checker checker = new Checker(repositoryBin);
			LOGGER.info("Success init checker");
			scheduler = new Scheduler(engine, checker);
			LOGGER.info("Success init scheduler");
			scheduler.metadatas = new HashMap<String, Metadata>();
			scheduler.loadMetadata(repositorySrc);
			LOGGER.info("Success init metadata");
			scheduler.checkers = new ArrayBlockingQueue<String>(100);
			scheduler.initCheckers();
			LOGGER.info("Success init set of checkers");
			scheduler.checkToRun = new ArrayBlockingQueue<String>(100);
			try {
				scheduler.logger = LoggerFactory.newLoggerInstance(config);
				LOGGER.info("Success init logger");
			} catch (LoggerException e) {
				LOGGER.warn("Logger didn't initialize " + e.getMessage(), e);
			}
		} catch (FileNotFoundException e) {
			String message = "Didn't found scheduler config file";
			LOGGER.error(message, e);
			throw new SchedulerException(message, e);
		} catch (IOException e) {
			String message = "Couldn't configure scheduler properties";
			LOGGER.error(message, e);
			throw new SchedulerException(message, e);
		} catch (EngineException e) {
			String message = "Engine exception " + e.getMessage();
			LOGGER.error(message, e);
			throw new SchedulerException(message, e);
		}
		return scheduler;
	}

	public void loadMetadata(String sourceDir) throws SchedulerException {
		File srcDirectory = new File(sourceDir);
		List<File> files = FileSystemHelper.listFiles(srcDirectory,
				new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return "metadata.properties".equals(name);
					}
				}, true);
		for (File file : files) {
			try {
				Metadata metadata = new Metadata();
				metadata.load(new FileInputStream(file));
				metadatas.put(metadata.getName(), metadata);
			} catch (FileNotFoundException e) {
				String message = "Metadata file don't find "
						+ file.getAbsolutePath();
				LOGGER.error(message, e);
				throw new SchedulerException(message, e);
			} catch (IOException e) {
				String message = "Couldn't load metadata from file "
						+ file.getAbsolutePath();
				LOGGER.error(message, e);
				throw new SchedulerException(message, e);
			}
		}
	}

	public void initCheckers() {
		for (Metadata metadata : metadatas.values()) {
			for (Job job : metadata.getJobs().values()) {
				if (!checkers.contains(job.getChecker())) {
					checkers.add(job.getChecker());
				}
			}
		}
	}

	public void run() {
		try {
			LOGGER.info("Compile all rules....");
			LOGGER.info(engine.compileAllRules());
			LOGGER.info("Success compile all rules");
			synchronized (this) {
				CheckerThread checkerThread = new CheckerThread();
				// checkerThread.setDaemon(true);
				checkerThread.start();
				RuleExecuteThread ruleExecuteThread = new RuleExecuteThread();
				// ruleExecuteThread.setDaemon(true);
				ruleExecuteThread.start();
			}
		} catch (EngineException e) {
			LOGGER.error(e);
		}
	}

	public class CheckerThread extends Thread {

		protected int delay = 1000;

		protected boolean isRun = true;

		public CheckerThread() {
		}

		public void run() {
			while (isRun) {
				LOGGER.info("Start check .....");
				for (String checkerRule : checkers) {
					try {
						logRule(checkerRule, new Date(System
								.currentTimeMillis()), null, null,
								ILogger.RULE_STATUS_START,
								ILogger.RULE_TYPE_CHECK, null);
						boolean result = checker.check(checkerRule);
						if (result) {
							checkToRun.add(checkerRule);
							LOGGER.info("Success check " + checkerRule);
							logRule(checkerRule, new Date(System
									.currentTimeMillis()), null, null,
									ILogger.RULE_STATUS_SUCCESS,
									ILogger.RULE_TYPE_CHECK, null);
						} else {
							logRule(checkerRule, new Date(System
									.currentTimeMillis()), null, null,
									ILogger.RULE_STATUS_SKIP,
									ILogger.RULE_TYPE_CHECK, null);
						}
					} catch (CheckerException e) {
						LOGGER.error(e);
					}
				}
				try {
					LOGGER.info("Sleep " + delay + " ms before next check");
					sleep(delay);
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}
		}

	}

	public class RuleExecuteThread extends Thread {

		protected int delay = 1000;

		protected boolean isRun = true;

		public RuleExecuteThread() {
		}

		public void run() {
			while (isRun) {
				LOGGER.info("Start executer .....");
				for (String checkerRule : checkToRun) {
					for (String rule : findRules(checkerRule)) {
						try {
							LOGGER.info("Start execute rule " + rule);
							logRule(checkerRule, new Date(System
									.currentTimeMillis()), null, null,
									ILogger.RULE_STATUS_START,
									ILogger.RULE_TYPE_BASE, null);
							// engine.reCompileRule(rule);
							engine.executeRule(rule, false);
							logRule(checkerRule, new Date(System
									.currentTimeMillis()), null, null,
									ILogger.RULE_STATUS_SUCCESS,
									ILogger.RULE_TYPE_BASE, null);
						} catch (EngineException e) {
							logRule(checkerRule, new Date(System
									.currentTimeMillis()), null, null,
									ILogger.RULE_STATUS_FAILURE,
									ILogger.RULE_TYPE_BASE, e.getMessage());
							LOGGER.error(e);
						}
					}
					checkToRun.remove(checkerRule);
				}
				try {
					LOGGER.info("Sleep " + delay
							+ " ms before next run executer");
					sleep(delay);
				} catch (InterruptedException e) {
					LOGGER.error(e);
				}
			}
		}

		public Set<String> findRules(String checker) {
			Set<String> rules = new HashSet<String>();
			for (Metadata metadata : metadatas.values()) {
				for (Job job : metadata.getJobs().values()) {
					if (checker.equals(job.getChecker())) {
						rules.add(job.getRule());
					}
				}
			}
			return rules;
		}

	}

	public void logRule(String ruleName, Date date, String jobName,
			String metadataName, String status, String type, String failure) {
		if (logger != null) {
			try {
				logger.logRule(ruleName, date, jobName, metadataName, status,
						type, failure);
			} catch (LoggerException e) {
				LOGGER.warn(e);
			}
		}
	}

}
