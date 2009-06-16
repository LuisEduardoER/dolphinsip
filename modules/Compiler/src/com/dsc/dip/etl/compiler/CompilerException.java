package com.dsc.dip.etl.compiler;

public class CompilerException extends Exception{

	private static final long serialVersionUID = 3988134782684360576L;

	public CompilerException() {
	}

	public CompilerException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompilerException(String message) {
		super(message);
	}

	public CompilerException(Throwable cause) {
		super(cause);
	}

}
