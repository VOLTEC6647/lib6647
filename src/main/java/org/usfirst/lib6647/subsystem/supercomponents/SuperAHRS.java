package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperAHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;

/**
 * Interface to allow {@link HyperAHRS} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize any {@link HyperAHRS HyperAHRS objects}
 * declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperAHRS {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperAHRS} instances.
	 */
	final Map<String, HyperAHRS> ahrsDevices = new HashMap<>();

	/**
	 * Method to initialize {@link HyperAHRS HyperAHRS objects} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #ahrsDevices} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initAHRS(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("ahrs").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !ahrsDevices.containsKey(json.get("name").asText())
						&& json.hasNonNull("port")) {
					// Read values from JsonNode.
					var port = getPort(json.get("port").asText());

					// Check if the required JsonNode values to initialize the object are present.
					if (port == null)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR AHRS '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create HyperAHRS object.
					var ahrs = new HyperAHRS(port, json.get("inverted").asBoolean());

					// Additional initialization configuration.
					if (json.hasNonNull("resetOnStart") && json.get("resetOnStart").asBoolean())
						ahrs.reset();
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					ahrsDevices.put(json.get("name").asText(), ahrs);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY AHRS ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				Logger.getGlobal().severe(e::getLocalizedMessage);
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link HyperAHRS} from the {@link #ahrsDevices} HashMap.
	 * 
	 * @param ahrsName The name of the {@link HyperAHRS}
	 * @return The requested {@link HyperAHRS}, if found
	 */
	default HyperAHRS getAHRS(String ahrsName) {
		return ahrsDevices.get(ahrsName);
	}

	/**
	 * Get a {@link HyperAHRS} port value from a String.
	 * 
	 * @param port The {@link HyperAHRS}'s port, as a String value
	 * @return The {@link SerialPort.Port}, as a valid port value
	 */
	private SerialPort.Port getPort(String port) {
		switch (port) {
			case "MXP":
				return Port.kMXP;
			case "Onboard":
				return Port.kOnboard;
			case "USB":
				return Port.kUSB;
			case "USB1":
				return Port.kUSB1;
			case "USB2":
				return Port.kUSB2;
			default:
				return null;
		}
	}
}