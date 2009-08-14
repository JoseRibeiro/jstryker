package stryker.exception;

public class StrykerException extends RuntimeException {

	private static final long serialVersionUID = -4709007513472245637L;

	public StrykerException(String message, Throwable exception) {
		super(message, exception);
	}
}