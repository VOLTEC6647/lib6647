package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.MotorUtils;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;

/**
 * Interface to allow {@link Encoder} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} or
 * {@link PIDSuperSubsystem} and implement this interface in order to initialize
 * {@link Encoder Encoder objects} declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperEncoder extends MotorUtils {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link Encoder} instances.
	 */
	final HashMap<String, Encoder> encoders = new HashMap<>();

	/**
	 * Method to initialize {@link Encoder Encoder objects} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #encoders} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initEncoders(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("encoders").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !encoders.containsKey(json.get("name").asText())
						&& json.hasNonNull("channelA") && json.hasNonNull("channelB") && json.hasNonNull("reverse")
						&& json.hasNonNull("encodingType")) {
					// Read values from JsonNode.
					int channelA = json.get("channelA").asInt(-1), channelB = json.get("channelB").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (channelA < 0 || channelB < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY VALUE(S) FOR ENCODER '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create Encoder object.
					Encoder encoder = new Encoder(json.get("channelA").asInt(), json.get("channelB").asInt(),
							json.get("reverse").asBoolean(), getEncodingType(json.get("encodingType").asText()));

					// Additional initialization configuration.
					if (json.get("resetOnStart").asBoolean(false))
						encoder.reset();
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					encoders.put(json.get("name").asText(), encoder);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY ENCODER ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link Encoder} from the {@link #encoders} HashMap.
	 * 
	 * @param encoderName The name of the {@link Encoder}
	 * @return The requested {@link Encoder}, if found
	 */
	default Encoder getEncoder(String encoderName) {
		return encoders.get(encoderName);
	}
}