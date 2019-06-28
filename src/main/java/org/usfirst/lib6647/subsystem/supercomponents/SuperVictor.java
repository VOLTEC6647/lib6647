package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperVictor;
import org.usfirst.lib6647.util.MotorUtils;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperVictor} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link HyperTalon
 * HyperVictors} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperVictor extends MotorUtils {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperVictor
	 * HyperVictors}.
	 */
	public HashMap<String, HyperVictor> victors = new HashMap<String, HyperVictor>();

	/**
	 * Method to initialize {@link HyperVictor HyperVictors} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #victors} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 * 
	 * @Note ALWAYS declare and initialize masters before followers!
	 */
	default void initVictors(JSONObject robotMap, String subsystemName) {
		try {
			// Create a JSONArray out of the declared objects.
			JSONArray victorArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
					.get(subsystemName)).get("victors");
			// Create a stream to cast each entry in the JSONArray into a JSONObject, in
			// order to configure it using the values declared in the robotMap file.
			Arrays.stream(victorArray.toArray()).map(json -> (JSONObject) json).forEach(json -> {
				try {
					// Create an object out of one index in the JSONArray.
					HyperVictor victor = new HyperVictor(Integer.parseInt(json.get("port").toString()));

					victor.setName(json.get("name").toString());

					if (json.containsKey("limiter"))
						setLimiter(json, victor);

					if (json.containsKey("neutralMode"))
						setNeutralMode(json, victor);

					if (json.containsKey("inverted"))
						setInverted(json, victor);

					if (json.containsKey("loopRamp"))
						setLoopRamp(json, victor);

					victor.stopMotor();

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					victors.put(json.get("name").toString(), victor);
				} catch (Exception e) {
					DriverStation.reportError(
							"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' VICTOR INIT ERROR: " + e.getMessage(),
							false);
					System.out.println(
							"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' VICTOR INIT ERROR: " + e.getMessage());
					System.exit(1);
				} finally {
					// Clear JSONObject after use, not sure if it does anything, but it might free
					// some unused memory.
					json.clear();
				}
			});
			// Clear JSONArray after use, not sure if it does anything, but it might free
			// some unused memory.
			victorArray.clear();
		} catch (Exception e) {
			DriverStation.reportError(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' VICTOR INIT ERROR: " + e.getMessage(), false);
			System.out.println(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' VICTOR INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
	}
}