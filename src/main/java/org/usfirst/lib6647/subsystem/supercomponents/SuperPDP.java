package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.ComponentInitException;

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
	default void initPDPs(JSONObject robotMap, String subsystemName) {
		// Create a JSONArray out of the declared objects.
		JSONArray PDPArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems")).get(subsystemName))
				.get("PDPs");

		// Create a parallel stream from the JSONArray.
		Stream<?> stream = PDPArray.parallelStream();
		// Cast each entry into a JSONObject, and configure it using the values declared
		// in the JSON file.
		stream.map(json -> (JSONObject) json).forEach(json -> {
			try {
				if (json.containsKey("name") && json.containsKey("module")) {

					PowerDistributionPanel pdp;
					try {
						// Try to initialize an object from an index in the JSONArray.
						pdp = new PowerDistributionPanel(Integer.parseInt(json.get("module").toString()));
					} catch (NumberFormatException e) {
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR PDP '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").toString(), subsystemName));
					}

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					PDPs.put(json.get("name").toString(), pdp);
				} else {
					System.out.println(String.format("[!] UNDECLARED OR EMPTY PDP ENTRY IN SUBSYSTEM '%s'",
							subsystemName.toUpperCase()));
				}
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
			} finally {
				// Clear JSONObject after use, not sure if it does anything, but it might free
				// some unused memory.
				json.clear();
			}
		});

		// Clear JSONArray after use, not sure if it does anything, but it might free
		// some unused memory.
		PDPArray.clear();
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