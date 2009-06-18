package com.dsc.dip.etl.engine.felix.shell.impl;

import java.io.PrintStream;
import java.security.PrivilegedExceptionAction;

import com.dsc.dip.etl.engine.felix.shell.Command;
import com.dsc.dip.etl.engine.felix.shell.ShellException;

public class ExecutePrivileged implements PrivilegedExceptionAction<Command> {

	private Command command = null;

	private String commandLine = null;

	private PrintStream out = null;

	private PrintStream err = null;

	public ExecutePrivileged(Command command, String commandLine,
			PrintStream out, PrintStream err) throws ShellException {
		this.command = command;
		this.commandLine = commandLine;
		this.out = out;
		this.err = err;
	}

	public Command run() throws ShellException {
		command.execute(commandLine, out, err);
		return command;
	}
}
