package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

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
		try {
			// Create a JSONArray out of the declared objects.
			JSONArray digitalInputArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
					.get(subsystemName)).get("digitalInputs");
			// Create a stream to cast each entry in the JSONArray into a JSONObject, in
			// order to configure it using the values declared in the robotMap file.
			Arrays.stream(digitalInputArray.toArray()).map(json -> (JSONObject) json).forEach(json -> {
				try {
					// Create an object out of one index in the JSONArray.
					DigitalInput digitalInput = new DigitalInput(Integer.parseInt(json.get("channel").toString()));
					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					digitalInputs.put(json.get("name").toString(), digitalInput);
				} catch (Exception e) {
					DriverStation.reportError("[!] SUBSYSTEM '" + subsystemName.toUpperCase()
							+ "' DIGITAL INPUT INIT ERROR: " + e.getMessage(), false);
					System.out.println("[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' DIGITAL INPUT INIT ERROR: "
							+ e.getMessage());
					System.exit(1);
				} finally {
					// Clear JSONObject after use, not sure if it does anything, but it might free
					// some unused memory.
					json.clear();
				}
			});
			// Clear JSONArray after use, not sure if it does anything, but it might free
			// some unused memory.
			digitalInputArray.clear();
		} catch (Exception e) {
			DriverStation.reportError(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' DIGITAL INPUT INIT ERROR: " + e.getMessage(),
					false);
			System.out.println(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' DIGITAL INPUT INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
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