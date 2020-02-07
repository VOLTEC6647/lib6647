package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link DigitalInput} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} or
 * {@link PIDSuperSubsystem} and implement this interface in order to initialize
 * {@link DigitalInput DigitalInput objects} declared in
 * {@link SuperSubsystem#robotMap}.
 */
public interface SuperDigitalInput {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link DigitalInput} instances.
	 */
	final HashMap<String, DigitalInput> digitalInputs = new HashMap<>();

	/**
	 * Method to initialize {@link DigitalInput DigitalInput objects} declared in
	 * the {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #digitalInputs} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initDigitalInputs(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("digitalInputs").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !digitalInputs.containsKey(json.get("name").asText())
						&& json.hasNonNull("channel")) {
					// Read values from JsonNode.
					int channel = json.get("channel").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (channel < 0)
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY CHANNEL VALUE FOR DIGITALINPUT '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").asText(), subsystemName));

					// Create DigitalInput object.
					DigitalInput digitalInput = new DigitalInput(json.get("channel").asInt());

					// Additional initialization configuration.
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					digitalInputs.put(json.get("name").asText(), digitalInput);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY DIGITALINPUT ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link DigitalInput} from the {@link #digitalInputs} HashMap.
	 * 
	 * @param digitalInputName The name of the {@link DigitalInput}
	 * @return The requested {@link DigitalInput}, if found
	 */
	default DigitalInput getDigitalInput(String digitalInputName) {
		return digitalInputs.get(digitalInputName);
	}
}