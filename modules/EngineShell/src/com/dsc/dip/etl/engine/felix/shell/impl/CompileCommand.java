package com.dsc.dip.etl.engine.felix.shell.impl;

import java.io.PrintStream;

import com.dsc.dip.etl.engine.EngineException;
import com.dsc.dip.etl.engine.felix.EngineService;
import com.dsc.dip.etl.engine.felix.shell.Command;

public class CompileCommand implements Command {

    public EngineService engine;

    public CompileCommand(EngineService engine) {
	this.engine = engine;
    }

    public void execute(String line, PrintStream out, PrintStream err) {
	if (line != null) {
	    String[] args = line.split("[ ]");
	    if (engine != null) {
		try {
		    if (args.length > 1) {
			engine.compileRule(args[1]);
		    }
		} catch (EngineException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace(err);
		}
	    }
	}
    }

    public String getName() {
	return "compile";
    }

    public String getShortDescription() {
	return "compile rule";
    }

    public String getUsage() {
	return "compile <path to rule>";
    }

}
