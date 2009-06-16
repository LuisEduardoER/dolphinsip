package com.dsc.dip.etl.engine;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;

import com.dsc.dip.etl.engine.jmx.EngineMBean;


public class ConsoleEngine {

	private static final String JMX_OPTION = "jmx";

	private static final String BASECONF_OPTION = "baseconf";

	private static final String CONFIG_OPTION = "config";

	private static final String REPOSITORY_OPTION = "repository";

	private static final String HELP_OPTION = "help";

	private final static Logger LOGGER = Logger.getLogger(ConsoleEngine.class);

	public static void main(String[] args) {
		CommandLineParser parser = new PosixParser();

		try {
			CommandLine line = parser.parse(getEngineOption(), args);
			if (line.hasOption(HELP_OPTION)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("engine [options] ruleSourceFile",
						getEngineOption());
				return;
			}
			String repository = getMandatoryOptions(line, REPOSITORY_OPTION), configDir = getMandatoryOptions(
					line, CONFIG_OPTION), baseConfigFile = getMandatoryOptions(
					line, BASECONF_OPTION);

			String log4jPropertiesFile = repository + File.separator
					+ configDir + File.separator + "log4j.properties";
			if (!new File(log4jPropertiesFile).exists()) {
				try {
					BasicConfigurator.configure(new FileAppender(
							new PatternLayout("%-5r %-5p [%t] %c{2} - %m%n"),
							"engine.log"));
				} catch (IOException e) {
					BasicConfigurator.configure();
				}
			} else {
				PropertyConfigurator.configure(log4jPropertiesFile);
			}

			if (line.hasOption(JMX_OPTION)) {
				MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
				ObjectName mbeanName = new ObjectName(
						"by.bsu.fami.etl.engine.jmx:" + "type=EngineMBean");
				EngineMBean engine = new EngineMBean(repository, configDir,
						baseConfigFile);
				LOGGER.info("Create engine MBean");
				mbs.registerMBean(engine, mbeanName);
				LOGGER.info("Start JMX Engine MBean service");
				Thread.sleep(Long.MAX_VALUE);
			} else {
				args = line.getArgs();
				if (args.length != 1) {
					System.err.println("Please spicify rule file name.");
					return;
				}
				if (StringUtils.isNotEmpty(args[0])) {
					Engine e = Engine.initInstance(repository, configDir,
							baseConfigFile);
					e.executeRule(args[0]);
				}
			}
		} catch (ParseException exp) {
			System.err
					.println("Unexpected exception when parse compile options.");
			exp.printStackTrace();
		} catch (EngineException e) {
			LOGGER.error("Couldn't execute rule.", e);
		} catch (MalformedObjectNameException e) {
			LOGGER.error("Couldn't start engine jmx server, "
					+ "not found MBean class", e);
			return;
		} catch (InstanceAlreadyExistsException e) {
			LOGGER.error("Couldn't registar engine jmx server, "
					+ "already exist instance", e);
			return;
		} catch (MBeanRegistrationException e) {
			LOGGER.error("Couldn't registar engine jmx server", e);
			return;
		} catch (NotCompliantMBeanException e) {
			LOGGER.error("Couldn't registar engine jmx server, "
					+ "not a MBean class", e);
			return;
		} catch (InterruptedException e) {
			LOGGER.error(e);
		}
	}

	private static String getMandatoryOptions(CommandLine line,
			String optionName) {
		String tmp = line.getOptionValue(optionName, "");
		if (StringUtils.isNotEmpty(tmp)) {
			return tmp;
		}
		throw new IllegalArgumentException(optionName + " option is mandatory.");
	}

	@SuppressWarnings("static-access")
	public static Options getEngineOption() {
		Options options = new Options();
		options.addOption(new Option(HELP_OPTION, "print this message"));
		options
				.addOption(OptionBuilder
						.withArgName("directory")
						.hasArg()
						.withDescription(
								"mandatory option, use given directory with config, source and class files")
						.create(REPOSITORY_OPTION));
		options
				.addOption(OptionBuilder
						.withArgName("directory")
						.hasArg()
						.withDescription(
								"mandatory option, use given directory in repository with config files")
						.create(CONFIG_OPTION));
		options
				.addOption(OptionBuilder
						.withArgName("file")
						.hasArg()
						.withDescription(
								"mandatory option, use given file with base config options")
						.create(BASECONF_OPTION));
		options.addOption(new Option(JMX_OPTION, "start engine jmx server"));
		return options;
	}
}
