package by.bsu.fami.etl.checker;

public class CheckerException extends Exception {

	private static final long serialVersionUID = -4777450914609922193L;

	public CheckerException() {
	}

	public CheckerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CheckerException(String arg0) {
		super(arg0);
	}

	public CheckerException(Throwable arg0) {
		super(arg0);
	}

}
