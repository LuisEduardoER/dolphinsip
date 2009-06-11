package by.bsu.fami.etl.scheduler;

import java.io.File;
import java.io.IOException;

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

public class ConsoleScheduler {

	private final static Logger LOGGER = Logger
			.getLogger(ConsoleScheduler.class);

	private static final String HELP_OPTION = "help";

	private static final String CONFIG_OPTION = "conf";

	private static final String LOG_CONFIG_OPTION = "log4j";

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
			String log4jPropertiesFile = getMandatoryOptions(line,
					LOG_CONFIG_OPTION), configFile = getMandatoryOptions(line,
					CONFIG_OPTION);

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

			Scheduler scheduler = Scheduler.initScheduler(configFile);
			scheduler.run();
		} catch (ParseException exp) {
			System.err
					.println("Unexpected exception when parse compile options.");
			exp.printStackTrace();
		} catch (SchedulerException e) {
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
		options.addOption(OptionBuilder.withArgName("file").hasArg()
				.withDescription(
						"mandatory option, use given file for log4j config")
				.create(LOG_CONFIG_OPTION));
		options.addOption(OptionBuilder.withArgName("file").hasArg()
				.withDescription("mandatory option, scheduler config file")
				.create(CONFIG_OPTION));
		return options;
	}

}
