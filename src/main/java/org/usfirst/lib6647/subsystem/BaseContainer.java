package org.usfirst.lib6647.subsystem;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.lib6647.oi.JController;

/**
 * This class is a template Container for other template Containers, it only
 * holds a {@link #joysticks} HashMap, and a method to register
 * {@link JController joysticks}.
 */
public abstract class BaseContainer {
	/** HashMap holding initialized {@link JController joysticks}. */
	private final Map<String, JController> joysticks = new HashMap<>();

	/**
	 * Get specified {@link JController Joystick} from {@link #joysticks}.
	 * 
	 * @param name The name of the {@link JController}
	 * @return A {@link JController} instance, if found
	 */
	public synchronized JController getJoystick(String name) {
		return joysticks.get(name);
	}

	/**
	 * Register a given {@link JController joystick}.
	 * 
	 * @param joystick The joystick to be registered
	 * @param name     The key to store the registered {@link JController joystick}
	 *                 at in the {@link #joysticks} HashMap
	 */
	public synchronized void registerJoystick(JController joystick, String name) {
		joysticks.putIfAbsent(name, joystick);
	}
}