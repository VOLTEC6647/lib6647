package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;
import com.kauailabs.navx.frc.AHRS;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperAHRS;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.SerialPort.Port;

/**
 * Interface to allow {@link HyperAHRS} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize any {@link HyperAHRS}
 * declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperAHRS {
	/** HashMap storing the {@link SuperSubsystem}'s {@link HyperAHRS} devices. */
	public HashMap<String, HyperAHRS> ahrsDevices = new HashMap<>();

	/**
	 * Method to initialize {@link HyperAHRS} devices declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #ahrsDevices} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initAHRS(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("ahrs").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !ahrsDevices.containsKey(json.get("name").asText())
						&& json.hasNonNull("port")) {
					// Read values from JsonNode.
					SerialPort.Port port = getPort(json.get("port").asText());

					// Check if the required JsonNode values to initialize the object are present.
					if (port == null)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR AHRS '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create AHRS object.
					HyperAHRS ahrs = new HyperAHRS(port, json.get("inverted").asBoolean());

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
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
				DriverStation.reportError(e.getMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link AHRS}.
	 * 
	 * @return {@link AHRS}
	 * @param name
	 */
	default AHRS getAHRS(String name) {
		return ahrsDevices.get(name);
	}

	/**
	 * Get roboRIO port from String.
	 * 
	 * @param port
	 * @return port
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