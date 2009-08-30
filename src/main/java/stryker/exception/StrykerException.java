package stryker.exception;

/**
 * An exception that provides information on a stryker operation. 
 */
public class StrykerException extends RuntimeException {

	private static final long serialVersionUID = -4709007513472245637L;

	/**
	 * Constructs a new {@link StrykerException} exception with the specified detail message and cause.
	 * @param message The detail message.
	 * @param exception The cause.
	 * @see {@link RuntimeException#RuntimeException(String, Throwable)}.
	 */
	public StrykerException(String message, Throwable exception) {
		super(message, exception);
	}
}