package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.stream.Stream;

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
	public HashMap<String, HyperDoubleSolenoid> doubleSolenoids = new HashMap<String, HyperDoubleSolenoid>();

	/**
	 * Method to initialize {@link HyperDoubleSolenoid HyperDoubleSolenoids}
	 * declared in the {@link SuperSubsystem#robotMap robotMap} JSON file, and add
	 * them to the {@link #doubleSolenoids} HashMap using its declared name as its
	 * key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initDoubleSolenoids(JSONObject robotMap, String subsystemName) {
		// Create a JSONArray out of the declared objects.
		JSONArray doubleSolenoidArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
				.get(subsystemName)).get("doubleSolenoids");

		// Create a parallel stream from the JSONArray.
		Stream<?> stream = doubleSolenoidArray.parallelStream();
		// Cast each entry into a JSONObject, and configure it using the values declared
		// in the JSON file.
		stream.map(json -> (JSONObject) json).forEach(json -> {
			try {
				if (json.containsKey("name") && json.containsKey("forwardChannel")
						&& json.containsKey("reverseChannel")) {

					HyperDoubleSolenoid doubleSolenoid;
					try {
						// Try to initialize an object from an index in the JSONArray.
						doubleSolenoid = new HyperDoubleSolenoid(
								Integer.parseInt(json.get("forwardChannel").toString()),
								Integer.parseInt(json.get("reverseChannel").toString()));
					} catch (NumberFormatException e) {
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY CHANNEL VALUE(S) FOR DOUBLESOLENOID '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").toString(), subsystemName));
					}
					doubleSolenoid.stop();

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					doubleSolenoids.put(json.get("name").toString(), doubleSolenoid);
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
		doubleSolenoidArray.clear();
	}

	/**
	 * Gets specified {@link HyperDoubleSolenoid}.
	 * 
	 * @return {@link HyperDoubleSolenoid}
	 * @param doubleSolenoidName
	 */
	default HyperDoubleSolenoid getDoubleSolenoid(String doubleSolenoidName) {
		return doubleSolenoids.get(doubleSolenoidName);
	}
}