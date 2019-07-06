package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperDoubleSolenoid;
import org.usfirst.lib6647.util.ComponentInitException;

/**
 * Interface to allow {@link HyperDoubleSolenoid} initialization via JSON.
 * Subsystems declared need to extend {@link SuperSubsystem} or
 * {@link PIDSuperSubsystem} and implement this interface in order to initialize
 * {@link HyperDoubleSolenoid HyperDoubleSolenoids} declared in
 * {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperDoubleSolenoid {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperDoubleSolenoid
	 * HyperDoubleSolenoids}.
	 */
	public HashMap<String, HyperDoubleSolenoid> solenoids = new HashMap<String, HyperDoubleSolenoid>();

	/**
	 * Method to initialize {@link HyperDoubleSolenoid HyperDoubleSolenoids}
	 * declared in the {@link SuperSubsystem#robotMap robotMap} JSON file, and add
	 * them to the {@link #solenoids} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initSolenoids(JSONObject robotMap, String subsystemName) {
		// Create a JSONArray out of the declared objects.
		JSONArray solenoidArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
				.get(subsystemName)).get("doubleSolenoids");
		// Create a stream to cast each entry in the JSONArray into a JSONObject, in
		// order to configure it using the values declared in the robotMap file.
		Arrays.stream(solenoidArray.toArray()).map(json -> (JSONObject) json).forEach(json -> {
			try {
				if (json.containsKey("name") && json.containsKey("forwardChannel")
						&& json.containsKey("reverseChannel")) {

					HyperDoubleSolenoid solenoid;
					try {
						// Try to initialize an object from an index in the JSONArray.
						solenoid = new HyperDoubleSolenoid(Integer.parseInt(json.get("forwardChannel").toString()),
								Integer.parseInt(json.get("reverseChannel").toString()));
					} catch (NumberFormatException e) {
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY CHANNEL VALUE(S) FOR DOUBLESOLENOID '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").toString(), subsystemName));
					}
					solenoid.stop();

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					solenoids.put(json.get("name").toString(), solenoid);
				} else {
					System.out.println(String.format("[!] UNDECLARED OR EMPTY DOUBLESOLENOID ENTRY IN SUBSYSTEM '%s'",
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
		solenoidArray.clear();
	}

	/**
	 * Gets specified {@link HyperDoubleSolenoid}.
	 * 
	 * @return {@link HyperDoubleSolenoid}
	 * @param solenoidName
	 */
	default HyperDoubleSolenoid getSolenoid(String solenoidName) {
		return solenoids.get(solenoidName);
	}
}