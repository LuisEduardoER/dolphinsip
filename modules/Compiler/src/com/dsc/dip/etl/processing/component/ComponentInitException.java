package com.dsc.dip.etl.processing.component;

public class ComponentInitException extends Exception {

	private static final long serialVersionUID = 531719556363234634L;

	public ComponentInitException() {
	}

	public ComponentInitException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ComponentInitException(String arg0) {
		super(arg0);
	}

	public ComponentInitException(Throwable arg0) {
		super(arg0);
	}

}
