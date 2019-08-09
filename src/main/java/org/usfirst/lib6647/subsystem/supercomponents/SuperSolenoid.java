package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSolenoid;
import org.usfirst.lib6647.util.ComponentInitException;

/**
 * Interface to allow {@link HyperSolenoid} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link HyperSolenoid
 * HyperSolenoids} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperSolenoid {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperSolenoid
	 * HyperSolenoids}.
	 */
	public HashMap<String, HyperSolenoid> solenoids = new HashMap<String, HyperSolenoid>();

	/**
	 * Method to initialize {@link HyperSolenoid HyperSolenoids} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #solenoids} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initSolenoids(JSONObject robotMap, String subsystemName) {
		// Create a JSONArray out of the declared objects.
		JSONArray solenoidArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
				.get(subsystemName)).get("solenoids");

		// Create a parallel stream from the JSONArray.
		Stream<?> stream = solenoidArray.parallelStream();
		// Cast each entry into a JSONObject, and configure it using the values declared
		// in the JSON file.
		stream.map(json -> (JSONObject) json).forEach(json -> {
			try {
				if (json.containsKey("name") && json.containsKey("channel")) {

					HyperSolenoid solenoid;
					try {
						// Create an object out of one index in the JSONArray.
						solenoid = new HyperSolenoid(Integer.parseInt(json.get("channel").toString()));
					} catch (NumberFormatException e) {
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY CHANNEL VALUE(S) FOR SOLENOID '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").toString(), subsystemName));
					}

					if (json.containsKey("initialValue"))
						setInitialValue(json, solenoid);

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					solenoids.put(json.get("name").toString(), solenoid);
				} else {
					System.out.println(String.format("[!] UNDECLARED OR EMPTY SOLENOID ENTRY IN SUBSYSTEM '%s'",
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
	 * Sets a given {@link HyperSolenoid}'s inverted value from a
	 * {@link JSONObject}.
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperSolenoid}
	 * @throws ComponentInitException if {@link JSONObject} key is defined, but
	 *                                empty.
	 */
	private void setInitialValue(JSONObject json, HyperSolenoid solenoid) throws ComponentInitException {
		if (json.get("initialValue").toString().isEmpty())
			throw new ComponentInitException(String.format("[!] EMPTY INITIAL VALUE FOR SOLENOID '%s'.",
					json.get("name").toString().toUpperCase()));

		solenoid.set(Boolean.parseBoolean(json.get("initialValue").toString()));
	}

	/**
	 * Gets specified {@link HyperSolenoid}.
	 * 
	 * @return {@link HyperSolenoid}
	 * @param solenoidName
	 */
	default HyperSolenoid getSolenoid(String solenoidName) {
		return solenoids.get(solenoidName);
	}
}