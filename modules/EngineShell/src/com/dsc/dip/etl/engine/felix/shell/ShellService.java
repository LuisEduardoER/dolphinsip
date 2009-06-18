package com.dsc.dip.etl.engine.felix.shell;

import java.io.PrintStream;

import org.osgi.framework.ServiceReference;

public interface ShellService {

	public String[] getCommands();

	public String getCommandUsage(String arg0);

	public String getCommandDescription(String arg0);

	public ServiceReference getCommandReference(String arg0);

	public void executeCommand(String arg0, PrintStream arg1, PrintStream arg2)
			throws ShellException;
}
