package com.dsc.dip.etl.engine.felix.shell.impl;

import java.io.PrintStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.dsc.dip.etl.engine.felix.shell.Command;
import com.dsc.dip.etl.engine.felix.shell.ShellException;
import com.dsc.dip.etl.engine.felix.shell.ShellService;

public class ShellServiceImpl implements ShellService {

	private Map<ServiceReference, Command> commandRefMap = new HashMap<ServiceReference, Command>();

	private Map<String, Command> commandNameMap = new HashMap<String, Command>();

	public synchronized String[] getCommands() {
		Set<String> ks = commandNameMap.keySet();
		String[] cmds = (ks == null) ? new String[0] : (String[]) ks
				.toArray(new String[ks.size()]);
		return cmds;
	}

	public synchronized String getCommandUsage(String name) {
		Command command = (Command) commandNameMap.get(name);
		return (command == null) ? null : command.getUsage();
	}

	public synchronized String getCommandDescription(String name) {
		Command command = commandNameMap.get(name);
		return (command == null) ? null : command.getShortDescription();
	}

	public synchronized ServiceReference getCommandReference(String name) {
		ServiceReference ref = null;
		Iterator<Entry<ServiceReference, Command>> itr = commandRefMap
				.entrySet().iterator();
		while (itr.hasNext()) {
			Entry<ServiceReference, Command> entry = itr.next();
			if (entry.getValue().getName().equals(name)) {
				ref = entry.getKey();
				break;
			}
		}
		return ref;
	}

	public synchronized void removeCommand(ServiceReference ref) {
		Command command = (Command) commandRefMap.remove(ref);
		if (command != null) {
			commandNameMap.remove(command.getName());
		}
	}

	public synchronized void executeCommand(String commandLine,
			PrintStream out, PrintStream err) throws ShellException {
		commandLine = commandLine.trim();
		String commandName = (commandLine.indexOf(' ') >= 0) ? commandLine
				.substring(0, commandLine.indexOf(' ')) : commandLine;
		Command command = getCommand(commandName);
		if (command != null) {
			if (System.getSecurityManager() != null) {
				try {
					AccessController.doPrivileged(new ExecutePrivileged(
							command, commandLine, out, err));
				} catch (PrivilegedActionException ex) {
					throw new ShellException(ex.getException());
				}
			} else {
				try {
					command.execute(commandLine, out, err);
				} catch (Throwable ex) {
					err.println("Unable to execute command: " + ex);
					ex.printStackTrace(err);
				}
			}
		} else {
			err.println("Command not found.");
		}
	}

	protected synchronized Command getCommand(String name) {
		Command command = (Command) commandNameMap.get(name);
		return (command == null) ? null : command;
	}

	public synchronized void addCommand(BundleContext context,
			ServiceReference ref) {
		if (ref != null) {
			Object cmdObj = context.getService(ref);
			if (cmdObj != null && cmdObj instanceof Command) {
				Command command = (Command) cmdObj;
				commandRefMap.put(ref, command);
				commandNameMap.put(command.getName(), command);
			}
		}
	}

	public synchronized void clearCommands() {
		commandRefMap.clear();
		commandNameMap.clear();
	}

}
