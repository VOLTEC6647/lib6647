package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link Compressor} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link Compressor Compressor objects}
 * declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperCompressor {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link Compressor} instances.
	 */
	final Map<String, Compressor> compressors = new HashMap<>();

	/**
	 * Method to initialize {@link Compressor objects} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #compressors} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initCompressors(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("compressors").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !compressors.containsKey(json.get("name").asText())
						&& json.hasNonNull("module")) {
					// Read values from JsonNode.
					var module = json.get("module").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (module < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY VALUE(S) FOR COMPRESSOR '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create Compressor object.
					var compressor = new Compressor(json.get("module").asInt());

					// Additional initialization configuration.
					// (None required at the moment).
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					compressors.put(json.get("name").asText(), compressor);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY COMPRESSOR ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				Logger.getGlobal().severe(e::getLocalizedMessage);
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link Compressor} from the {@link #compressors} HashMap.
	 * 
	 * @param compressorName The name of the {@link Compressor}
	 * @return The requested {@link Compressor}, if found
	 */
	default Compressor getCompressor(String compressorName) {
		return compressors.get(compressorName);
	}
}