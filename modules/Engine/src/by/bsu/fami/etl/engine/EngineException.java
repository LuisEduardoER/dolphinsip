package by.bsu.fami.etl.engine;

public class EngineException extends Exception{

	private static final long serialVersionUID = 6599071731072097366L;

	public EngineException() {
		super();
	}

	public EngineException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public EngineException(String arg0) {
		super(arg0);
	}

	public EngineException(Throwable arg0) {
		super(arg0);
	}

}
