package org.jstryker.exception;

/**
 * An exception that provides information on a jstryker operation. 
 */
public class JStrykerException extends RuntimeException {

	private static final long serialVersionUID = -4709007513472245637L;

	/**
	 * Constructs a new {@link JStrykerException} exception with the specified detail message and cause.
	 * @param message The detail message.
	 * @param exception The cause.
	 * @see {@link RuntimeException#RuntimeException(String, Throwable)}.
	 */
	public JStrykerException(String message, Throwable exception) {
		super(message, exception);
	}
	
	public JStrykerException(String message) {
		super(message);
	}
}