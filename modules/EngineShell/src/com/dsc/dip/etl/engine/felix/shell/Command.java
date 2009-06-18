package com.dsc.dip.etl.engine.felix.shell;

import java.io.PrintStream;

public interface Command {

	public String getName();

	public String getUsage();

	public String getShortDescription();

	public void execute(String line, PrintStream out, PrintStream err);
}
