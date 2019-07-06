package org.usfirst.lib6647.util;

/**
 * {@link Exception} for errors while initializing Super/HyperComponents.
 */
public class ComponentInitException extends Exception {

	/**
	 * Serial Version UID.
	 */
	private static final long serialVersionUID = 7668209987656891489L;

	/**
	 * {@link Exception} for errors while initializing Super/HyperComponents.
	 * 
	 * @param message
	 */
	public ComponentInitException(String message) {
		super(message);
	}
}