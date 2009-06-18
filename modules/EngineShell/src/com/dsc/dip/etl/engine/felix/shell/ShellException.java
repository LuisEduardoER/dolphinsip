package com.dsc.dip.etl.engine.felix.shell;

public class ShellException extends Exception {

	private static final long serialVersionUID = 4017351962524618522L;

	public ShellException() {
	}

	public ShellException(String message, Throwable cause) {
		super(message, cause);
	}

	public ShellException(String message) {
		super(message);
	}

	public ShellException(Throwable cause) {
		super(cause);
	}

}
