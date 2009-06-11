package by.bsu.fami.etl.processing.datasource;

public class DataSourceException extends Exception {

	private static final long serialVersionUID = -4843588092786422321L;

	public DataSourceException() {
		super();
	}

	public DataSourceException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataSourceException(String message) {
		super(message);
	}

	public DataSourceException(Throwable cause) {
		super(cause);
	}

}
