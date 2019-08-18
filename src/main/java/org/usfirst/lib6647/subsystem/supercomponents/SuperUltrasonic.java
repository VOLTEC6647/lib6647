package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.ComponentInitException;

import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * Interface to allow {@link Ultrasonic} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link Ultrasonic
 * Ultrasonics} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperUltrasonic {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link Ultrasonic Ultrasonics}.
	 */
	public HashMap<String, Ultrasonic> ultrasonics = new HashMap<String, Ultrasonic>();

	/**
	 * Method to initialize {@link Ultrasonic Ultrasonics} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #ultrasonics} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initUltrasonics(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("ultrasonics").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && json.hasNonNull("pingChannel") && json.hasNonNull("echoChannel")) {
					// Read values from JsonNode.
					int pingChannel = json.get("pingChannel").asInt(-1),
							echoChannel = json.get("echoChannel").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (pingChannel < 0 || echoChannel < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY VALUE(S) FOR ULTRASONIC '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create Ultrasonic object.
					Ultrasonic ultrasonic = new Ultrasonic(pingChannel, echoChannel);

					// Additional initialization configuration.
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					ultrasonics.put(json.get("name").asText(), ultrasonic);
				} else
					throw new ComponentInitException(String.format(
							"[!] UNDECLARED OR EMPTY ULTRASONIC ENTRY IN SUBSYSTEM '%s'", subsystemName.toUpperCase()));
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
			}
		});
	}

	/**
	 * Gets specified {@link Ultrasonic}.
	 * 
	 * @return {@link Ultrasonic}
	 * @param ultrasonicName
	 */
	default Ultrasonic getUltrasonic(String ultrasonicName) {
		return ultrasonics.get(ultrasonicName);
	}
}