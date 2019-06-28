package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.util.MotorUtils;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperTalon} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link HyperTalon
 * HyperTalons} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperTalon extends MotorUtils {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperTalon HyperTalons}.
	 */
	public HashMap<String, HyperTalon> talons = new HashMap<String, HyperTalon>();

	/**
	 * Method to initialize {@link HyperTalon HyperTalons} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #talons} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 * 
	 * @Note ALWAYS declare and initialize masters before followers!
	 */
	default void initTalons(JSONObject robotMap, String subsystemName) {
		try {
			// Create a JSONArray out of the declared objects.
			JSONArray talonArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
					.get(subsystemName)).get("talons");
			// Create a stream to cast each entry in the JSONArray into a JSONObject, in
			// order to configure it using the values declared in the robotMap file.
			Arrays.stream(talonArray.toArray()).map(json -> (JSONObject) json).forEach(json -> {
				try {
					// Create an object out of one index in the JSONArray.
					HyperTalon talon = new HyperTalon(Integer.parseInt(json.get("port").toString()));

					talon.setName(json.get("name").toString());

					if (json.containsKey("limiter"))
						setLimiter(json, talon);

					if (json.containsKey("neutralMode"))
						setNeutralMode(json, talon);

					if (json.containsKey("inverted"))
						setInverted(json, talon);

					if (json.containsKey("loopRamp"))
						setLoopRamp(json, talon);

					if (json.containsKey("sensor"))
						setSensors(json, talon);

					if (json.containsKey("pid"))
						setPIDValues(json, talon);

					talon.stopMotor();

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					talons.put(json.get("name").toString(), talon);
				} catch (Exception e) {
					DriverStation.reportError(
							"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' TALON INIT ERROR: " + e.getMessage(),
							false);
					System.out.println(
							"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' TALON INIT ERROR: " + e.getMessage());
					System.exit(1);
				} finally {
					// Clear JSONObject after use, not sure if it does anything, but it might free
					// some unused memory.
					json.clear();
				}
			});
			// Clear JSONArray after use, not sure if it does anything, but it might free
			// some unused memory.
			talonArray.clear();
		} catch (Exception e) {
			DriverStation.reportError(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' TALON INIT ERROR: " + e.getMessage(), false);
			System.out
					.println("[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' TALON INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Gets {@link HyperTalon} HashMap.
	 * @return {@link HyperTalon} HashMap
	 */
	default HashMap<String, HyperTalon> getTalons() {
		return talons;
	}
}