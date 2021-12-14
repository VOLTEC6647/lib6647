package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Interface to allow {@link PowerDistributionPanel} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link PowerDistributionPanel
 * PowerDistributionPanel objects} declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperPDP {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link PowerDistributionPanel}
	 * instances.
	 */
	final Map<String, PowerDistributionPanel> PDPs = new HashMap<>();

	/**
	 * Method to initialize {@link PowerDistributionPanel PowerDistributionPanel
	 * objects} declared in the {@link SuperSubsystem#robotMap JSON file}, and add
	 * them to the {@link #PDPs} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initPDPs(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("PDPs").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !PDPs.containsKey(json.get("name").asText())
						&& json.hasNonNull("module")) {
					// Read values from JsonNode.
					var module = json.get("module").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (module < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR PDP '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create PDP object.
					var pdp = new PowerDistributionPanel(module);

					// Additional initialization configuration.
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					PDPs.put(json.get("name").asText(), pdp);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY PDP ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				Logger.getGlobal().severe(e::getLocalizedMessage);
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link PowerDistributionPanel} from the {@link #PDPs} HashMap.
	 * 
	 * @param pdpName The name of the {@link PowerDistributionPanel}
	 * @return The requested {@link PowerDistributionPanel}, if found
	 */
	default PowerDistributionPanel getPDP(String pdpName) {
		return PDPs.get(pdpName);
	}
}