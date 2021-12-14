package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;

/**
 * Interface to allow {@link Servo} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link Servo Servo objects} declared in
 * {@link SuperSubsystem#robotMap}.
 */
public interface SuperServo {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link Servo} instances.
	 */
	final Map<String, Servo> servos = new HashMap<>();

	/**
	 * Method to initialize {@link Servo Servo objects} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #servos} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initServos(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("servos").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !servos.containsKey(json.get("name").asText())
						&& json.hasNonNull("channel")) {
					// Read values from JsonNode.
					var channel = json.get("channel").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (channel < 0)
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY CHANNEL VALUE(S) FOR SERVO '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").asText(), subsystemName));

					// Create Servo object.
					var servo = new Servo(json.get("channel").asInt());

					// Additional initialization configuration.
					if (json.hasNonNull("initialValue"))
						servo.setAngle(json.get("initialValue").asDouble());
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					servos.put(json.get("name").asText(), servo);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY SERVO ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				Logger.getGlobal().severe(e::getLocalizedMessage);
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link Servo} from the {@link #servos} HashMap.
	 * 
	 * @param servoName The name of the {@link Servo}
	 * @return The requested {@link Servo}, if found
	 */
	default Servo getServo(String servoName) {
		return servos.get(servoName);
	}
}