package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow a custom Component's initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize custom Components
 * declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperComponent {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s custom Components. Components
	 * obtained must be cast to be used properly.
	 */
	final HashMap<String, Object> customComponents = new HashMap<>();

	/**
	 * Method to initialize custom Components declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #customComponents} HashMap using its declared name as its key.
	 * 
	 * @param {@link        SuperSubsystem#robotMap}
	 * @param {@link        SuperSubsystem#getName}
	 * @param componentName
	 */
	default void initCustomComponents(JsonNode robotMap, String subsystemName, String componentName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get(componentName).spliterator().forEachRemaining(node -> {
			try {
				customInit(node, subsystemName);
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
				DriverStation.reportError(e.getMessage(), false);
			}
		});
	}

	/**
	 * This method must be implemented in order to add the custom Component to
	 * {@link SuperComponent#customComponents}. Each and every value to be read
	 * during initialization must also be declared here. See any other
	 * SuperComponent for an example. Make sure to properly handle any
	 * {@link Exception} and throw a {@link ComponentInitException} with the
	 * subsystem's name in the message.
	 * 
	 * @param node
	 * @param subsystemName
	 * @throws ComponentInitException
	 */
	public void customInit(JsonNode node, String subsystemName) throws ComponentInitException;

	/**
	 * Gets specified {@link SuperComponent}. Must be cast to be properly used.
	 * 
	 * @return Component
	 * @param componentName
	 */
	default Object getComponent(String componentName) {
		return customComponents.get(componentName);
	}
}