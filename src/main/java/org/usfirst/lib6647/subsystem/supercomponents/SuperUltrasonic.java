package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Ultrasonic;

/**
 * Interface to allow {@link Ultrasonic} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link Ultrasonic Ultrasonic objects}
 * declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperUltrasonic {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link Ultrasonic} instances.
	 */
	final HashMap<String, Ultrasonic> ultrasonics = new HashMap<>();

	/**
	 * Method to initialize {@link Ultrasonic Ultrasonic objects} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #ultrasonics} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initUltrasonics(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("ultrasonics").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !ultrasonics.containsKey(json.get("name").asText())
						&& json.hasNonNull("pingChannel") && json.hasNonNull("echoChannel")) {
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
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY ULTRASONIC ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link Ultrasonic} from the {@link #ultrasonics} HashMap.
	 * 
	 * @param ultrasonicName The name of the {@link Ultrasonic}
	 * @return The requested {@link Ultrasonic}, if found
	 */
	default Ultrasonic getUltrasonic(String ultrasonicName) {
		return ultrasonics.get(ultrasonicName);
	}
}