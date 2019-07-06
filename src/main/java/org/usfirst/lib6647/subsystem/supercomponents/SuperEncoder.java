package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.ComponentInitException;
import org.usfirst.lib6647.util.MotorUtils;

import edu.wpi.first.wpilibj.Encoder;

/**
 * Interface to allow {@link Encoder} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link Encoder Encoders}
 * declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperEncoder extends MotorUtils {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link Encoder Encoders}.
	 */
	public HashMap<String, Encoder> encoders = new HashMap<String, Encoder>();

	/**
	 * Method to initialize {@link Encoder Encoders} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #encoders} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initEncoders(JSONObject robotMap, String subsystemName) {
		// Create a JSONArray out of the declared objects.
		JSONArray encoderArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems")).get(subsystemName))
				.get("encoders");
		// Create a stream to cast each entry in the JSONArray into a JSONObject, in
		// order to configure it using the values declared in the robotMap file.
		Arrays.stream(encoderArray.toArray()).map(json -> (JSONObject) json).forEach(json -> {
			try {
				if (json.containsKey("name") && json.containsKey("channelA") && json.containsKey("channelB")
						&& json.containsKey("reverse") && json.containsKey("encodingType")) {

					Encoder encoder;
					try {
						// Try to initialize an object from an index in the JSONArray.
						encoder = new Encoder(Integer.parseInt(json.get("channelA").toString()),
								Integer.parseInt(json.get("channelB").toString()),
								Boolean.parseBoolean(json.get("reverse").toString()),
								getEncodingType(json.get("encodingType").toString()));
					} catch (NullPointerException | NumberFormatException e) {
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY VALUE(S) FOR ENCODER '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").toString(), subsystemName));
					}
					encoder.reset();

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					encoders.put(json.get("name").toString(), encoder);
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
		encoderArray.clear();
	}

	/**
	 * Gets specified {@link Encoder}.
	 * 
	 * @return {@link Encoder}
	 * @param encoderName
	 */
	default Encoder getEncoder(String encoderName) {
		return encoders.get(encoderName);
	}
}