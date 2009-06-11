package by.bsu.fami.etl.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.log4j.Logger;

import by.bsu.fami.etl.compiler.bcel.GenerateException;
import by.bsu.fami.etl.compiler.bcel.RuleGenerator;
import by.bsu.fami.etl.compiler.bean.Rule;
import by.bsu.fami.etl.compiler.jcc.ParseException;
import by.bsu.fami.etl.compiler.jcc.RuleCompiler;
import by.bsu.fami.etl.compiler.jcc.SimpleNode;

/**
 * 
 * Class to compile rule on internal ETL language to java byte-code.
 * 
 * @author Pavel_Drabushevich
 * 
 */
public class Compiler {

	private final static Logger LOGGER = Logger.getLogger(Compiler.class);

	protected RuleGenerator ruleGenerator;

	protected Compiler() {
	}

	/**
	 * 
	 * Create new instance of Compiler
	 * 
	 */
	public static Compiler initInstance() {
		Compiler c = new Compiler();
		c.ruleGenerator = new RuleGenerator();
		return c;
	}

	/**
	 * 
	 * Create new instance of Compiler
	 * 
	 * @param typeMap
	 *            - map with type synonym in rule and real class name
	 * 
	 */
	public static Compiler initInstance(Map<String, String> typeMap) {
		Compiler c = new Compiler();
		c.ruleGenerator = new RuleGenerator(typeMap);
		return c;
	}

	/**
	 * 
	 * Compile source rule to java byte-code
	 * 
	 * @param sourceDir - directory with rule source code
	 * @param destDir - directory for java byte-code files
	 * @param fileName - name of file, which contain rule 
	 * @return
	 * @throws CompilerException
	 */
	public String compile(String sourceDir, String destDir, String fileName)
			throws CompilerException {
		File ruleFile = null;
		try {
			ruleFile = new File(sourceDir + File.separator + fileName);
			LOGGER.info("Init compiler process for rule : " + ruleFile);
			RuleCompiler rc = RuleCompiler.compile(new InputStreamReader(
					new FileInputStream(ruleFile)));
			int index = 0;
			String packageStr = fileName.substring(0, (index = fileName
					.lastIndexOf(File.separator)) == -1 ? 0 : index);
			SimpleNode rule = rc.Rule();
			LOGGER.info("Success create rule tree : " + ruleFile);
			RuleCompiler.dump(rule, ">>");
			return ruleGenerator.generateRule(destDir, packageStr, (Rule) rule
					.jjtGetValue());
		} catch (FileNotFoundException e) {
			LOGGER.error("Couldn't file rule source file = "
					+ ruleFile.getAbsolutePath() + " to compile this rule!", e);
			throw new CompilerException("Couldn't file rule source file = "
					+ ruleFile.getAbsolutePath() + " to compile this rule!", e);
		} catch (GenerateException e) {
			LOGGER.error("Couldn't generate class file to rule" + fileName, e);
			throw new CompilerException("Couldn't generate class file to rule"
					+ fileName, e);
		} catch (ParseException e) {
			LOGGER.error("Couldn't parse source file of rule" + fileName, e);
			throw new CompilerException("Couldn't parse source file of rule"
					+ fileName, e);
		}
	}

	public String getInitRuleMethodName() {
		return ruleGenerator.getInitComponents();
	}

	public String getExecuteRuleMethodName() {
		return "execute";
	}

}
