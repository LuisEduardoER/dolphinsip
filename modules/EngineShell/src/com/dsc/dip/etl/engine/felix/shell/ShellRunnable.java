package com.dsc.dip.etl.engine.felix.shell;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ShellRunnable implements Runnable {

    private volatile boolean stop = false;

    protected ShellService shell;

    protected Object lock;

    public ShellRunnable(ShellService shell, Object lock) {
	this.shell = shell;
	this.lock = lock;
    }

    public void stop() {
	stop = true;
    }

    public void run() {
	String line = null;
	BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	// Check to see if we have stdin.
	try {
	    System.in.available();
	} catch (IOException ex) {
	    stop = true;
	}

	while (!stop) {
	    System.out.print("-> ");

	    try {
		line = in.readLine();
	    } catch (IOException ex) {
		System.err
			.println("EngineShell: Error reading from stdin...exiting.");
		break;
	    }

	    synchronized (lock) {
		if (line == null) {
		    System.err
			    .println("EngineShell: No standard input...exiting.");
		    break;
		}

		if (shell == null) {
		    System.out.println("No impl service available.");
		    continue;
		}

		line = line.trim();

		if (line.length() == 0) {
		    continue;
		}

		try {
		    shell.executeCommand(line, System.out, System.err);
		} catch (Exception ex) {
		    System.err.println("EngineShell: " + ex);
		    ex.printStackTrace();
		}
	    }
	}
    }
}