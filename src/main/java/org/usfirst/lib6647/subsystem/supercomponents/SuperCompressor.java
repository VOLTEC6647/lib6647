package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.ComponentInitException;

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
	public HashMap<String, Compressor> compressors = new HashMap<>();

	/**
	 * Method to initialize {@link Compressor Compressors} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #compressors} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initCompressors(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("compressors").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !compressors.containsKey(json.get("name").asText())
						&& json.hasNonNull("module")) {
					// Read values from JsonNode.
					int module = json.get("module").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (module < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY VALUE(S) FOR COMPRESSOR '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create Compressor object.
					Compressor compressor = new Compressor(json.get("module").asInt());

					// Additional initialization configuration.
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					compressors.put(json.get("name").asText(), compressor);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY COMPRESSOR ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
				DriverStation.reportError(e.getMessage(), false);
			}
		});
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