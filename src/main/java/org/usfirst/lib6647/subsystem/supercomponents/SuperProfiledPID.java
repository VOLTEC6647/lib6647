package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.wpilib.controller.ProfiledPIDController;
import org.usfirst.lib6647.wpilib.trajectory.TrapezoidProfile.Constraints;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;

/**
 * Interface to allow {@link ProfiledPIDController} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link ProfiledPIDController
 * ProfiledPIDController objects} declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperProfiledPID {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link ProfiledPIDController}
	 * instances.
	 */
	final HashMap<String, ProfiledPIDController> profiledPIDControllers = new HashMap<>();

	/**
	 * Method to initialize {@link ProfiledPIDController ProfiledPIDController
	 * objects} declared in the {@link SuperSubsystem#robotMap JSON file}, and add
	 * them to the {@link #profiledPIDControllers} HashMap using its declared name
	 * as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initProfiledPIDs(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("pidProfiled").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !profiledPIDControllers.containsKey(json.get("name").asText())) {
					var pidName = json.get("name").asText();
					double p = json.get("p").asDouble(), i = json.get("i").asDouble(), d = json.get("d").asDouble(),
							period = json.get("period").asDouble(0.02),
							maxVelocity = json.get("maxVelocity").asDouble(),
							maxAcceleration = json.get("maxAcceleration").asDouble();

					// Build ProfiledPIDController object.
					var controller = new ProfiledPIDController(pidName, subsystemName, p, i, d,
							new Constraints(maxVelocity, maxAcceleration), period);

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

					if (json.hasNonNull("positionTolerance"))
						if (json.hasNonNull("velocityTolerance"))
							controller.setTolerance(json.get("positionTolerance").asDouble(),
									json.get("velocityTolerance").asDouble(Double.POSITIVE_INFINITY));
						else
							controller.setTolerance(json.get("positionTolerance").asDouble());

					if (json.hasNonNull("fixedValues") && !json.get("fixedValues").asBoolean(true))
						Shuffleboard.getTab(subsystemName).add(subsystemName + "_" + pidName, controller).withSize(3,
								3);
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					profiledPIDControllers.put(pidName, controller);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY PROFILEDPID ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link ProfiledPIDController} from the
	 * {@link #profiledPIDControllers} HashMap.
	 * 
	 * @param profiledPIDName The name of the {@link ProfiledPIDController}
	 * @return The requested {@link ProfiledPIDController}, if found
	 */
	default ProfiledPIDController getProfiledPIDController(String profiledPIDName) {
		return profiledPIDControllers.get(profiledPIDName);
	}
}