package org.usfirst.lib6647.subsystem;

/**
 * {@link Exception} for errors thrown while initializing Super/HyperComponents.
 */
public class ComponentInitException extends Exception {
	/** Serial version UID, required by compiler. */
	private static final long serialVersionUID = 7668209987656891489L;

	/**
	 * {@link Exception} for errors thrown while initializing Super/HyperComponents.
	 * 
	 * @param message The error message
	 */
	public ComponentInitException(String message) {
		super(message);
	}
}