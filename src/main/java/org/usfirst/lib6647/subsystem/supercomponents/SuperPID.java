package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperPIDController;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperPIDController} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} or
 * {@link PIDSuperSubsystem} and implement this interface in order to initialize
 * {@link HyperPIDController HyperPIDController objects} declared in
 * {@link SuperSubsystem#robotMap}.
 */
public interface SuperPID {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperPIDController}
	 * instances.
	 */
	final HashMap<String, HyperPIDController> pidControllers = new HashMap<>();

	/**
	 * Method to initialize {@link HyperPIDController HyperPIDController objects}
	 * declared in the {@link SuperSubsystem#robotMap JSON file}, and add them to
	 * the {@link #pidControllers} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initPIDs(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("pid").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !pidControllers.containsKey(json.get("name").asText())) {
					String pidName = json.get("name").asText();
					double p = json.get("p").asDouble(), i = json.get("i").asDouble(), d = json.get("d").asDouble(),
							period = json.get("period").asDouble(0.02);

					// Build HyperPIDController object.
					HyperPIDController controller = new HyperPIDController(pidName, subsystemName, p, i, d, period);

					// Read and apply PIDSuperSubsystem configuration from JSON file.
					if (json.get("continuous").asBoolean(false))
						controller.setInputRange(json.get("inputMin").asDouble(), json.get("inputMax").asDouble());
					else
						controller.disableContinuousInput();

					controller.setOutputRange(json.get("outputMin").asDouble(), json.get("outputMax").asDouble());

					if (json.hasNonNull("minI") && json.hasNonNull("maxI"))
						controller.setIntegratorRange(json.get("minI").asDouble(), json.get("maxI").asDouble());

					controller.setTolerance(json.get("tolerance").asDouble());

					if (!json.get("fixedValues").asBoolean(true))
						controller.outputPIDValues();
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					pidControllers.put(pidName, controller);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY PID ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link HyperPIDController} from the {@link #pidControllers}
	 * HashMap.
	 * 
	 * @param pidName The name of the {@link HyperPIDController}
	 * @return The requested {@link HyperPIDController}, if found
	 */
	default HyperPIDController getPIDController(String pidName) {
		return pidControllers.get(pidName);
	}
}