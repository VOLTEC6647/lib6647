package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSolenoid;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperSolenoid} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} or
 * {@link PIDSuperSubsystem} and implement this interface in order to initialize
 * {@link HyperSolenoid HyperSolenoid objects} declared in
 * {@link SuperSubsystem#robotMap}.
 */
public interface SuperSolenoid {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperSolenoid} instances.
	 */
	final HashMap<String, HyperSolenoid> solenoids = new HashMap<>();

	/**
	 * Method to initialize {@link HyperSolenoid HyperSolenoid objects} declared in
	 * the {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #solenoids} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initSolenoids(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("solenoids").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !solenoids.containsKey(json.get("name").asText())
						&& json.hasNonNull("channel")) {
					// Read values from JsonNode.
					int channel = json.get("channel").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (channel < 0)
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY CHANNEL VALUE(S) FOR SOLENOID '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").asText(), subsystemName));

					// Create HyperSolenoid object.
					HyperSolenoid solenoid = new HyperSolenoid(json.get("channel").asInt());

					// Additional initialization configuration.
					if (json.hasNonNull("initialValue"))
						solenoid.set(json.get("initialValue").asBoolean());
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					solenoids.put(json.get("name").asText(), solenoid);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY SOLENOID ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link HyperSolenoid} from the {@link #solenoids} HashMap.
	 * 
	 * @param solenoidName The name of the {@link HyperSolenoid}
	 * @return The requested {@link HyperSolenoid}, if found
	 */
	default HyperSolenoid getSolenoid(String solenoidName) {
		return solenoids.get(solenoidName);
	}
}