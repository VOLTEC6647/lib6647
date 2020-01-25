package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSolenoid;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperSolenoid} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link HyperSolenoid
 * HyperSolenoids} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperSolenoid {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperSolenoid
	 * HyperSolenoids}.
	 */
	final HashMap<String, HyperSolenoid> solenoids = new HashMap<>();

	/**
	 * Method to initialize {@link HyperSolenoid HyperSolenoids} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #solenoids} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
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
						setInitialValue(json, solenoid);
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					solenoids.put(json.get("name").asText(), solenoid);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY SOLENOID ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
				DriverStation.reportError(e.getMessage(), false);
			}
		});
	}

	/**
	 * Sets a given {@link HyperSolenoid}'s inverted value from a {@link JsonNode}.
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperSolenoid}
	 * @throws ComponentInitException if {@link JsonNode} key is defined, but empty.
	 */
	private void setInitialValue(JsonNode json, HyperSolenoid solenoid) throws ComponentInitException {
		solenoid.set(json.get("initialValue").asBoolean());
	}

	/**
	 * Gets specified {@link HyperSolenoid}.
	 * 
	 * @return {@link HyperSolenoid}
	 * @param solenoidName
	 */
	default HyperSolenoid getSolenoid(String solenoidName) {
		return solenoids.get(solenoidName);
	}
}