package by.bsu.fami.etl.processing;

public class RuleException extends Exception {

	private static final long serialVersionUID = -1682454485752106386L;

	public RuleException() {
	}

	public RuleException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public RuleException(String arg0) {
		super(arg0);
	}

	public RuleException(Throwable arg0) {
		super(arg0);
	}

}
