package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;

/**
 * Interface to allow {@link Encoder} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link Encoder Encoders}
 * declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperEncoder {
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
		try {
			// Create a JSONArray out of the declared objects.
			JSONArray encoderArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
					.get(subsystemName)).get("encoders");
			// Create a stream to cast each entry in the JSONArray into a JSONObject, in
			// order to configure it using the values declared in the robotMap file.
			Arrays.stream(encoderArray.toArray()).map(json -> (JSONObject) json).forEach(json -> {
				try {
					// Create an object out of one index in the JSONArray.
					Encoder encoder = new Encoder(Integer.parseInt(json.get("channelA").toString()),
							Integer.parseInt(json.get("channelB").toString()),
							Boolean.parseBoolean(json.get("reverse").toString()),
							getEncodingType(json.get("encodingType").toString()));
					encoder.reset();

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					encoders.put(json.get("name").toString(), encoder);
				} catch (Exception e) {
					DriverStation.reportError(
							"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' ENCODER INIT ERROR: " + e.getMessage(),
							false);
					System.out.println("[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' ENCODER INIT ERROR: "
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
			encoderArray.clear();
		} catch (Exception e) {
			DriverStation.reportError(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' ENCODER INIT ERROR: " + e.getMessage(), false);
			System.out.println(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' ENCODER INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
	}

	default EncodingType getEncodingType(String encodingType) {
		switch (encodingType) {
		case "k1X":
			return EncodingType.k1X;
		case "k2X":
			return EncodingType.k2X;
		case "k4X":
			return EncodingType.k4X;
		default:
			return null;
		}
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