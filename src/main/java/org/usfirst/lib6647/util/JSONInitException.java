package org.usfirst.lib6647.util;

/**
 * {@link Exception} for errors thrown while initializing JSON files.
 */
public class JSONInitException extends Exception {
	/** Serial version UID, required by compiler. */
	private static final long serialVersionUID = 6402258453078908362L;

	/**
	 * {@link Exception} for errors thrown while initializing JSON files.
	 * 
	 * @param message The error message
	 */
	public JSONInitException(String message) {
		super(message);
	}
}