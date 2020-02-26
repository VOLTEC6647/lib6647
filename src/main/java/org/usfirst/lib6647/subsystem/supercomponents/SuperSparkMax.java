package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.revrobotics.CANSparkMax;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.REVUtil;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link CANSparkMax} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link CANSparkMax CANSparkMax objects}
 * declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperSparkMax {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link CANSparkMax} instances.
	 */
	final HashMap<String, CANSparkMax> sparks = new HashMap<>();

	/**
	 * Method to initialize {@link CANSparkMax CANSparkMax objects} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #sparks} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initSparks(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("sparks").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !sparks.containsKey(json.get("name").asText()) && json.hasNonNull("port")
						&& json.hasNonNull("type")) {
					var type = REVUtil.getMotorType(json.get("type").asText());

					// Check if the required JsonNode values to initialize the object are present.
					if (type == null)
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY MOTORTYPE VALUE FOR SPARK '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").asText(), subsystemName));

					// Create CANSparkMax object.
					var spark = new CANSparkMax(json.get("port").asInt(), type);

					// Additional initialization configuration.
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					sparks.put(json.get("name").asText(), spark);
				}
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}
}