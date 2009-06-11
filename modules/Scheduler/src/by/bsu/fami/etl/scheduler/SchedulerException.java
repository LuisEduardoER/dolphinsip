package by.bsu.fami.etl.scheduler;

public class SchedulerException extends Exception {

	private static final long serialVersionUID = -6869945534761944296L;

	public SchedulerException() {
	}

	public SchedulerException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SchedulerException(String arg0) {
		super(arg0);
	}

	public SchedulerException(Throwable arg0) {
		super(arg0);
	}

}
