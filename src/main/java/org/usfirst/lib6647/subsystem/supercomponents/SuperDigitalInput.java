package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.stream.Stream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.ComponentInitException;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Interface to allow {@link DigitalInput} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link DigitalInput
 * DigitalInputs} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperDigitalInput {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link DigitalInput
	 * DigitalInputs}.
	 */
	public HashMap<String, DigitalInput> digitalInputs = new HashMap<String, DigitalInput>();

	/**
	 * Method to initialize {@link DigitalInput DigitalInputs} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #digitalInputs} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initDigitalInputs(JSONObject robotMap, String subsystemName) {
		// Create a JSONArray out of the declared objects.
		JSONArray digitalInputArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
				.get(subsystemName)).get("digitalInputs");

		// Create a parallel stream from the JSONArray.
		Stream<?> stream = digitalInputArray.parallelStream();
		// Cast each entry into a JSONObject, and configure it using the values declared
		// in the JSON file.
		stream.map(json -> (JSONObject) json).forEach(json -> {
			try {
				if (json.containsKey("name") && json.containsKey("channel")) {

					DigitalInput digitalInput;
					try {
						// Try to initialize an object from an index in the JSONArray.
						digitalInput = new DigitalInput(Integer.parseInt(json.get("channel").toString()));
					} catch (NumberFormatException e) {
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY CHANNEL VALUE FOR DIGITALINPUT '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").toString(), subsystemName));
					}

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					digitalInputs.put(json.get("name").toString(), digitalInput);
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
		digitalInputArray.clear();
	}

	/**
	 * Gets specified {@link DigitalInput}.
	 * 
	 * @return {@link DigitalInput}
	 * @param digitalInputName
	 */
	default DigitalInput getDigitalInput(String digitalInputName) {
		return digitalInputs.get(digitalInputName);
	}
}