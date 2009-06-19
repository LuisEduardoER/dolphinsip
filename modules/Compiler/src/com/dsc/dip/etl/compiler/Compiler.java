package com.dsc.dip.etl.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.log4j.Logger;

import com.dsc.dip.etl.compiler.bcel.GenerateException;
import com.dsc.dip.etl.compiler.bcel.RuleGenerator;
import com.dsc.dip.etl.compiler.bean.Rule;
import com.dsc.dip.etl.compiler.jcc.ParseException;
import com.dsc.dip.etl.compiler.jcc.RuleCompiler;
import com.dsc.dip.etl.compiler.jcc.SimpleNode;

/**
 * Class to compile rule write on internal ETL language to java byte-code.
 * @author <a href="mailto:p.drobushevich@gmail.com">Pavel Drobushevich</a>
 */
public class Compiler {

    /**
     * log4j logger.
     */
    private static final Logger LOGGER = Logger.getLogger(Compiler.class);

    /**
     * Generator instance to generate java byte-code by syntax tree.
     */
    private RuleGenerator ruleGenerator;

    /**
     * Constructor to protect create instance only with initInstance method.
     */
    protected Compiler() {
    }

    /**
     * Create and configure new instance of Compiler.
     * @return instance of Compiler
     */
    public static Compiler initInstance() {
        Compiler c = new Compiler();
        c.ruleGenerator = new RuleGenerator();
        return c;
    }

    /**
     * Create new instance of Compiler.
     * @param typeMap
     *            - map with type synonym in rule and real class name
     * @return instance of Compiler
     */
    public static Compiler initInstance(final Map<String, String> typeMap) {
        Compiler c = new Compiler();
        c.ruleGenerator = new RuleGenerator(typeMap);
        return c;
    }

    /**
     * Compile source rule to java byte-code.
     * @param sourceDir
     *            - directory with rule source code
     * @param destDir
     *            - directory for java byte-code files
     * @param fileName
     *            - name of file, which contain rule
     * @return generated class name
     * @throws CompilerException
     *             some problem to create java byte-code
     */
    public final String compile(final String sourceDir, final String destDir,
            final String fileName) throws CompilerException {
        File ruleFile = null;
        try {
            ruleFile = new File(sourceDir + File.separator + fileName);
            LOGGER.info("Init compiler process for rule : " + ruleFile);
            RuleCompiler rc = RuleCompiler.compile(new InputStreamReader(
                    new FileInputStream(ruleFile)));
            int index = fileName.lastIndexOf(File.separator);
            String packageStr = "";
            if (index > 0) {
                packageStr = fileName.substring(0, index);
            }
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

    /**
     * Get name of method init component in rule.
     * @return init method name
     */
    public final String getInitRuleMethodName() {
        return ruleGenerator.getInitComponents();
    }

    /**
     * Get name of method to execute data process in rule.
     * @return execute method name
     */
    public final String getExecuteRuleMethodName() {
        return "execute";
    }

}
