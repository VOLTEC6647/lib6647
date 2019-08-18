package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.ComponentInitException;

import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Interface to allow {@link DigitalInput} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link DigitalInput
 * DigitalInputs} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperDigitalInput {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link DigitalInput
	 * DigitalInputs}.
	 */
	public HashMap<String, DigitalInput> digitalInputs = new HashMap<String, DigitalInput>();

	/**
	 * Method to initialize {@link DigitalInput DigitalInputs} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #digitalInputs} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initDigitalInputs(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("digitalInputs").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && json.hasNonNull("channel")) {
					// Read values from JsonNode.
					int channel = json.get("channel").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (channel < 0)
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY CHANNEL VALUE FOR DIGITALINPUT '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").toString(), subsystemName));

					// Create DigitalInput object.
					DigitalInput digitalInput = new DigitalInput(json.get("channel").asInt());

					// Additional initialization configuration.
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					digitalInputs.put(json.get("name").toString(), digitalInput);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED OR EMPTY DIGITALINPUT ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
			}
		});
	}

	/**
	 * Gets specified {@link DigitalInput}.
	 * 
	 * @return {@link DigitalInput}
	 * @param digitalInputName
	 */
	default DigitalInput getDigitalInput(String digitalInputName) {
		return digitalInputs.get(digitalInputName);
	}
}