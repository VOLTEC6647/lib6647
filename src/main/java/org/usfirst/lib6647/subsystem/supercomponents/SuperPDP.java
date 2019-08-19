package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.ComponentInitException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PowerDistributionPanel;

/**
 * Interface to allow {@link PowerDistributionPanel} initialization via JSON.
 * Subsystems declared need to extend {@link SuperSubsystem} or
 * {@link PIDSuperSubsystem} and implement this interface in order to initialize
 * {@link PowerDistributionPanel PDPs} declared in
 * {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperPDP {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link PowerDistributionPanel
	 * PDPs}.
	 */
	public HashMap<String, PowerDistributionPanel> PDPs = new HashMap<String, PowerDistributionPanel>();

	/**
	 * Method to initialize {@link PowerDistributionPanel PDPs} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #PDPs} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initPDPs(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("PDPs").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !PDPs.containsKey(json.get("name").asText())
						&& json.hasNonNull("module")) {
					// Read values from JsonNode.
					int module = json.get("module").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (module < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR PDP '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create Ultrasonic object.
					PowerDistributionPanel pdp = new PowerDistributionPanel(module);

					// Additional initialization configuration.
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					PDPs.put(json.get("name").asText(), pdp);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY PDP ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
				DriverStation.reportError(e.getMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link PowerDistributionPanel}.
	 * 
	 * @return {@link PowerDistributionPanel}
	 * @param pdpName
	 */
	default PowerDistributionPanel getPDP(String pdpName) {
		return PDPs.get(pdpName);
	}
}