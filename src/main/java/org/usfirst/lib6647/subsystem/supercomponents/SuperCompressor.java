package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.Arrays;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link Compressor} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link Compressor
 * Compressors} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperCompressor {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link Compressor Compressors}.
	 */
	public HashMap<String, Compressor> compressors = new HashMap<String, Compressor>();

	/**
	 * Method to initialize {@link Compressor Compressors} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #compressors} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initCompressors(JSONObject robotMap, String subsystemName) {
		try {
			// Create a JSONArray out of the declared objects.
			JSONArray compressorArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems"))
					.get(subsystemName)).get("compressors");
			// Create a stream to cast each entry in the JSONArray into a JSONObject, in
			// order to configure it using the values declared in the robotMap file.
			Arrays.stream(compressorArray.toArray()).map(json -> (JSONObject) json).forEach(json -> {
				try {
					// Create an object out of one index in the JSONArray.
					Compressor compressor = new Compressor(Integer.parseInt(json.get("module").toString()));
					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					compressors.put(json.get("name").toString(), compressor);
				} catch (Exception e) {
					DriverStation.reportError("[!] SUBSYSTEM '" + subsystemName.toUpperCase()
							+ "' COMPRESSOR INIT ERROR: " + e.getMessage(), false);
					System.out.println("[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' COMPRESSOR INIT ERROR: "
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
			compressorArray.clear();
		} catch (Exception e) {
			DriverStation.reportError(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' COMPRESSOR INIT ERROR: " + e.getMessage(),
					false);
			System.out.println(
					"[!] SUBSYSTEM '" + subsystemName.toUpperCase() + "' COMPRESSOR INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Gets specified {@link Compressor}.
	 * 
	 * @return {@link Compressor}
	 * @param compressorName
	 */
	default Compressor getCompressor(String compressorName) {
		return compressors.get(compressorName);
	}
}