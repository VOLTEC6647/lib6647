package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;

/**
 * Interface to allow {@link Solenoid} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link Solenoid
 * Solenoids} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperSolenoid {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link Solenoid Solenoids}.
	 */
	public HashMap<String, Solenoid> solenoids = new HashMap<String, Solenoid>();

	/**
	 * Method to initialize {@link Solenoid Solenoids} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #solenoids} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initSolenoids(JSONObject robotMap, String subsystemName) {
		try {
			// Create a JSONArray out of the declared objects.
			JSONArray solenoidArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
					.get(subsystemName)).get("solenoids");
			// Create a stream to cast each entry in the JSONArray into a JSONObject, in
			// order to configure it using the values declared in the robotMap file.
			Arrays.stream(solenoidArray.toArray()).map(json -> (JSONObject) json).forEach(json -> {
				try {
					// Create an object out of one index in the JSONArray.
					Solenoid solenoid = new Solenoid(Integer.parseInt(json.get("channel").toString()));

					if (json.containsKey("initialValue"))
						solenoid.set(Boolean.parseBoolean(json.get("initialValue").toString()));

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					solenoids.put(json.get("name").toString(), solenoid);
				} catch (Exception e) {
					DriverStation.reportError("[!] SUBSYSTEM '" + subsystemName.toUpperCase()
							+ "' SOLENOID INIT ERROR: " + e.getMessage(), false);
					System.out.println("[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' SOLENOID INIT ERROR: "
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
			solenoidArray.clear();
		} catch (Exception e) {
			DriverStation.reportError(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' SOLENOID INIT ERROR: " + e.getMessage(),
					false);
			System.out.println(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' SOLENOID INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Gets specified {@link Solenoid}.
	 * 
	 * @return {@link Solenoid}
	 * @param solenoidName
	 */
	default Solenoid getSolenoid(String solenoidName) {
		return solenoids.get(solenoidName);
	}
}