package by.bsu.fami.etl.scheduler.log;

public class LoggerException extends Exception {

	private static final long serialVersionUID = 1187193662679306437L;

	public LoggerException() {
	}

	public LoggerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public LoggerException(String arg0) {
		super(arg0);
	}

	public LoggerException(Throwable arg0) {
		super(arg0);
	}

}
