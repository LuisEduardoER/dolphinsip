package by.bsu.fami.etl.compiler;

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

public class ConsoleCompiler {

	private static final String DESTDIR_OPTION = "destdir";
	
	private static final String SOURCEDIR_OPTION = "sourcedir";
	
	private static final String HELP_OPTION = "help";
	
	private final static Logger LOGGER = Logger.getLogger(ConsoleCompiler.class);

	public static void main(String[] args) {
		if (!new File("log4j.properties").exists()) {
			try {
				BasicConfigurator.configure(new FileAppender(new PatternLayout(
						"%-5r %-5p [%t] %c{2} - %m%n"), "compiler.log"));
			} catch (IOException e) {
				BasicConfigurator.configure();
			}
		} else {
			PropertyConfigurator.configure("log4j.properties");
		}

		CommandLineParser parser = new PosixParser();

		try {
			CommandLine line = parser.parse(getCompilerOption(), args);
			if (line.hasOption(HELP_OPTION)) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("compiler [options] ruleSourceFile",
						getCompilerOption());
				return;
			}
			args = line.getArgs();
			if (args.length != 1) {
				System.err.println("Please spicify rule file name.");
				return;
			}
			String sourceDir = getDirOptionValue(line, SOURCEDIR_OPTION), destDir = getDirOptionValue(
					line, DESTDIR_OPTION), fileName = args[0];

			if (StringUtils.isNotEmpty(fileName)) {
				Compiler c = Compiler.initInstance();
				c.compile(sourceDir, destDir, fileName);
			}
		} catch (ParseException exp) {
			LOGGER.error("Unexpected exception when parse compile options:", exp);
		} catch (CompilerException exp) {
			LOGGER.error("Couldn't compile rule:", exp);
		}
	}

	private static String getDirOptionValue(CommandLine line, String optionName) {
		return line.getOptionValue(optionName, new File("").getAbsolutePath());
	}

	@SuppressWarnings("static-access")
	public static Options getCompilerOption() {
		Options options = new Options();
		options.addOption(new Option(HELP_OPTION, "print this message"));
		options.addOption(OptionBuilder.withArgName("directory").hasArg()
				.withDescription("use given directory with rule source files")
				.create(SOURCEDIR_OPTION));
		options.addOption(OptionBuilder.withArgName("directory").hasArg()
				.withDescription("use given directory for generated class files")
				.create(DESTDIR_OPTION));
		return options;
	}
}
