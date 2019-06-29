package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * Interface to allow {@link Ultrasonic} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link Ultrasonic
 * Ultrasonics} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperUltrasonic {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link Ultrasonic Ultrasonics}.
	 */
	public HashMap<String, Ultrasonic> ultrasonics = new HashMap<String, Ultrasonic>();

	/**
	 * Method to initialize {@link Ultrasonic Ultrasonics} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #ultrasonics} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initUltrasonics(JSONObject robotMap, String subsystemName) {
		try {
			// Create a JSONArray out of the declared objects.
			JSONArray ultrasonicArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
					.get(subsystemName)).get("ultrasonics");
			// Create a stream to cast each entry in the JSONArray into a JSONObject, in
			// order to configure it using the values declared in the robotMap file.
			Arrays.stream(ultrasonicArray.toArray()).map(json -> (JSONObject) json).forEach(json -> {
				try {
					// Create an object out of one index in the JSONArray.
					Ultrasonic ultrasonic = new Ultrasonic(Integer.parseInt(json.get("pingChannel").toString()),
							Integer.parseInt(json.get("echoChannel").toString()));
					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					ultrasonics.put(json.get("name").toString(), ultrasonic);
				} catch (Exception e) {
					DriverStation.reportError("[!] SUBSYSTEM '" + subsystemName.toUpperCase()
							+ "' ULTRASONIC INIT ERROR: " + e.getMessage(), false);
					System.out.println("[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' ULTRASONIC INIT ERROR: "
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
			ultrasonicArray.clear();
		} catch (Exception e) {
			DriverStation.reportError(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' ULTRASONIC INIT ERROR: " + e.getMessage(),
					false);
			System.out.println(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' ULTRASONIC INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Gets specified {@link Ultrasonic}.
	 * 
	 * @return {@link Ultrasonic}
	 * @param ultrasonicName
	 */
	default Ultrasonic getUltrasonic(String ultrasonicName) {
		return ultrasonics.get(ultrasonicName);
	}
}