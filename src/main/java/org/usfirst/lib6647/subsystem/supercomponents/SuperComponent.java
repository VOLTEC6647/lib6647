package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow a custom type of Component's initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} or and implement
 * this interface in order to initialize custom Component objects declared in
 * {@link SuperSubsystem#robotMap}.
 */
public interface SuperComponent {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link SuperComponent custom
	 * Component} instances.
	 * 
	 * <p>
	 * <b>NOTE:</b> Components obtained must be cast to be used properly.
	 */
	final Map<String, Object> customComponents = new HashMap<>();

	/**
	 * Method to initialize custom Components declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #customComponents} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 * @param componentName The name of this {@link SuperComponent custom Component}
	 */
	default void initCustomComponents(JsonNode robotMap, String subsystemName, String componentName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get(componentName).spliterator().forEachRemaining(node -> {
			try {
				customInit(node, subsystemName);
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * This method must be implemented in order to add the custom Components to
	 * {@link SuperComponent#customComponents}.
	 * 
	 * <p>
	 * Each and every value to be read during initialization must also be declared
	 * here.
	 * 
	 * <p>
	 * You can view {@link SuperTalon any} {@link SuperVictor other}
	 * {@link SuperSolenoid SuperComponent} for an example.
	 * 
	 * <p>
	 * <b>NOTE:</b> Make sure to properly handle any {@link Exception} and throw a
	 * {@link ComponentInitException} with the subsystem's name in the message.
	 * 
	 * @param node          The node in {@link SuperSubsystem#robotMap} to look for
	 *                      initialization data
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 * @throws ComponentInitException When an error occurs while initializing a
	 *                                {@link SuperComponent custom Component}.
	 */
	public void customInit(JsonNode node, String subsystemName) throws ComponentInitException;

	/**
	 * Gets specified {@link SuperComponent custom Component} from the
	 * {@link #customComponents} HashMap.
	 * 
	 * <p>
	 * <b>NOTE:</b> Objects obtained must be cast to be used properly.
	 * 
	 * @param componentName The name of the {@link SuperComponent custom Component}
	 * @return The requested {@link SuperComponent custom Component}, if found in
	 *         the {@link #customComponents} HashMap
	 */
	default Object getComponent(String componentName) {
		return customComponents.get(componentName);
	}
}