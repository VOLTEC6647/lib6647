package org.usfirst.lib6647.subsystem;

import java.util.HashMap;

import org.usfirst.lib6647.subsystem.hypercomponents.HyperPIDController;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Abstract class to allow usage of {@link SuperSubsystem#robotMap JSON files}
 * for {@link SuperSubsystem} creation, with added {@link HyperPIDController}
 * functionality.
 */
public abstract class PIDSuperSubsystem extends SuperSubsystem {
	/**
	 * HashMap storing the {@link PIDSuperSubsystem}'s {@link HyperPIDController
	 * HyperPIDControllers}.
	 */
	private HashMap<String, HyperPIDController> pidControllers = new HashMap<>();

	/**
	 * Constructor for {@link PIDSuperSubsystem}. Initializes
	 * {@link HyperPIDController HyperPIDControllers} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}.
	 * 
	 * @param name
	 */
	public PIDSuperSubsystem(final String name) {
		super(name);

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("pid").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !pidControllers.containsKey(json.get("name").asText())) {
					String pidName = json.get("name").asText();
					double p = json.get("p").asDouble(), i = json.get("i").asDouble(), d = json.get("d").asDouble(),
							period = json.get("period").asDouble(0.02);

					// Build HyperPIDController object.
					HyperPIDController controller = new HyperPIDController(pidName, getName(), p, i, d, period);

					// Read and apply PIDSuperSubsystem configuration from JSON file.
					if (json.get("continuous").asBoolean(false))
						controller.setInputRange(json.get("inputMin").asDouble(), json.get("inputMax").asDouble());
					else
						controller.disableContinuousInput();

					controller.setOutputRange(json.get("outputMin").asDouble(), json.get("outputMax").asDouble());
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
									getName().toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getMessage());
				DriverStation.reportError(e.getMessage(), false);
			}
		});
	}

	// This method can be overwritten in the case that constantly checking for PID
	// updates in the Shuffleboard proves to be inconvenient.
	@Override
	public void periodic() {
		pidControllers.values().forEach(HyperPIDController::updatePIDValues);
	}

	/**
	 * Gets specified {@link HyperPIDController}.
	 * 
	 * @param name
	 * @return HyperPIDController
	 */
	public HyperPIDController getPIDController(String name) {
		return pidControllers.get(name);
	}

	/**
	 * Sets the specified {@link HyperPIDController HyperPIDController's} setpoint
	 * to the given value.
	 *
	 * @param name
	 * @param setpoint
	 */
	public void setSetpoint(String name, double setpoint) {
		pidControllers.get(name).setSetpoint(setpoint);
	}

	/**
	 * Returns the current setpoint of the specified {@link HyperPIDController}.
	 *
	 * @param name
	 * @return setpoint
	 */
	public double getSetpoint(String name) {
		return pidControllers.get(name).getSetpoint();
	}

	/**
	 * Return true if the specified {@link HyperPIDController} error is within the
	 * percentage of the total input range, determined by setTolerance.
	 *
	 * @param name
	 * @return atSetpoint
	 */
	public boolean onTarget(String name) {
		return pidControllers.get(name).atSetpoint();
	}
}