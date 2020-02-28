package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.wpilib.controller.PIDController;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

/**
 * Interface to allow {@link PIDController} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link PIDController PIDController objects}
 * declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperPID {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link PIDController} instances.
	 */
	final Map<String, PIDController> pidControllers = new HashMap<>();

	/**
	 * Method to initialize {@link PIDController PIDController objects} declared in
	 * the {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #pidControllers} HashMap using its declared name as its key.
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
					var pidName = json.get("name").asText();
					double p = json.get("p").asDouble(), i = json.get("i").asDouble(), d = json.get("d").asDouble(),
							period = json.get("period").asDouble(0.02);

					// Build PIDController object.
					var controller = new PIDController(pidName, subsystemName, p, i, d, period);

					// Read and apply PIDSuperSubsystem configuration from JSON file.
					if (json.hasNonNull("continuous") && json.get("continuous").asBoolean(false))
						controller.enableContinuousInput(json.get("inputMin").asDouble(),
								json.get("inputMax").asDouble());
					else
						controller.disableContinuousInput();

					if (json.hasNonNull("outputMin") && json.hasNonNull("outputMax"))
						controller.setOutputRange(json.get("outputMin").asDouble(), json.get("outputMax").asDouble());

					if (json.hasNonNull("minI") && json.hasNonNull("maxI"))
						controller.setIntegratorRange(json.get("minI").asDouble(), json.get("maxI").asDouble());

					if (json.hasNonNull("tolerance"))
						controller.setTolerance(json.get("tolerance").asDouble());

					if (json.hasNonNull("fixedValues") && !json.get("fixedValues").asBoolean(true))
						Shuffleboard.getTab(subsystemName).add(subsystemName + "_" + pidName, controller).withSize(2,
								2);
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
	 * Gets specified {@link PIDController} from the {@link #pidControllers}
	 * HashMap.
	 * 
	 * @param pidName The name of the {@link PIDController}
	 * @return The requested {@link PIDController}, if found
	 */
	default PIDController getPIDController(String pidName) {
		return pidControllers.get(pidName);
	}
}