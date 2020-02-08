package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperDoubleSolenoid;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperDoubleSolenoid} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link HyperDoubleSolenoid
 * HyperDoubleSolenoid objects} declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperDoubleSolenoid {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperDoubleSolenoid}
	 * instances.
	 */
	final HashMap<String, HyperDoubleSolenoid> doubleSolenoids = new HashMap<>();

	/**
	 * Method to initialize {@link HyperDoubleSolenoid HyperDoubleSolenoid objects}
	 * declared in the {@link SuperSubsystem#robotMap JSON file}, and add them to
	 * the {@link #doubleSolenoids} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initDoubleSolenoids(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("doubleSolenoids").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !doubleSolenoids.containsKey(json.get("name").asText())
						&& json.hasNonNull("forwardChannel") && json.hasNonNull("reverseChannel")) {
					// Read values from JsonNode.
					int forwardChannel = json.get("forwardChannel").asInt(-1),
							reverseChannel = json.get("reverseChannel").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (forwardChannel < 0 || reverseChannel < 0)
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY CHANNEL VALUE(S) FOR DOUBLESOLENOID '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").asText(), subsystemName));

					// Create HyperDoubleSolenoid object.
					HyperDoubleSolenoid doubleSolenoid = new HyperDoubleSolenoid(json.get("forwardChannel").asInt(),
							json.get("reverseChannel").asInt());

					// Additional initialization configuration.
					doubleSolenoid.stop();
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					doubleSolenoids.put(json.get("name").asText(), doubleSolenoid);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY DOUBLESOLENOID ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link HyperDoubleSolenoid} from the {@link #doubleSolenoids}
	 * HashMap.
	 * 
	 * @param doubleSolenoidName The name of the {@link HyperDoubleSolenoid}
	 * @return The requested {@link HyperDoubleSolenoid}, if found
	 */
	default HyperDoubleSolenoid getDoubleSolenoid(String doubleSolenoidName) {
		return doubleSolenoids.get(doubleSolenoidName);
	}
}