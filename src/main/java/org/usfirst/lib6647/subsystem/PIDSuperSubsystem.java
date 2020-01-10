package org.usfirst.lib6647.subsystem;

import java.util.HashMap;

import org.usfirst.lib6647.util.ComponentInitException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Abstract class to allow usage of {@link SuperSubsystem#robotMap JSON files}
 * for {@link SuperSubsystem} creation, with added {@link PIDController}
 * functionality.
 */
public abstract class PIDSuperSubsystem extends SuperSubsystem {
	/**
	 * HashMap storing the {@link PIDSuperSubsystem}'s {@link PIDController
	 * PIDControllers}.
	 */
	private HashMap<String, PIDController> pidControllers = new HashMap<>();

	/**
	 * Constructor for {@link PIDSuperSubsystem}. Initializes {@link PIDController
	 * PIDControllers} declared in the {@link SuperSubsystem#robotMap JSON file}.
	 * 
	 * @param name
	 */
	public PIDSuperSubsystem(final String name) {
		super(name);

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("pid").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !pidControllers.containsKey(json.get("name").asText())) {
					double p = json.get("p").asDouble(0.0), i = json.get("i").asDouble(0.0),
							d = json.get("d").asDouble(0.0), period = json.get("period").asDouble(0.02);

					// Build PIDController object.
					PIDController controller = new PIDController(p, i, d, period);

					// Read and apply PIDSuperSubsystem configuration from JSON file.
					if (json.get("continuous").asBoolean(false))
						controller.enableContinuousInput(json.get("inputMin").asDouble(),
								json.get("inputMax").asDouble());
					else
						controller.disableContinuousInput();

					controller.setTolerance(json.get("tolerance").asDouble());
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					pidControllers.put(json.get("name").asText(), controller);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY PID ENTRY IN SUBSYSTEM '%s'",
									getName().toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getMessage());
				DriverStation.reportError(e.getMessage(), false);
			}
		});

		// Output every PIDController's starting P, I, and D values.
		pidControllers.entrySet().forEach(entry -> {
			SmartDashboard.putString(name + "_" + entry.getKey() + "P", entry.getValue().getP() + "");
			SmartDashboard.putString(name + "_" + entry.getKey() + "I", entry.getValue().getI() + "");
			SmartDashboard.putString(name + "_" + entry.getKey() + "D", entry.getValue().getD() + "");
		});
	}

	// This method can be overwritten in the case that constantly checking for PID
	// updates in the Shuffleboard proves to be inconvenient.
	@Override
	public void periodic() {
		pidControllers.entrySet().forEach(entry -> {
			// Look for a change in P, then change its value in the PIDController.
			if (entry.getValue().getP() != Double.parseDouble(
					SmartDashboard.getString(getName() + "_" + entry.getKey() + "P", entry.getValue().getP() + "")))
				entry.getValue()
						.setP(Double.parseDouble(SmartDashboard.getString(getName() + "_" + entry.getKey() + "P",
								entry.getValue().getP() + "")));

			// Look for a change in I, then change its value in the PIDController.
			if (entry.getValue().getI() != Double.parseDouble(
					SmartDashboard.getString(getName() + "_" + entry.getKey() + "I", entry.getValue().getI() + "")))
				entry.getValue()
						.setI(Double.parseDouble(SmartDashboard.getString(getName() + "_" + entry.getKey() + "I",
								entry.getValue().getI() + "")));

			// Look for a change in D, then change its value in the PIDController.
			if (entry.getValue().getD() != Double.parseDouble(
					SmartDashboard.getString(getName() + "_" + entry.getKey() + "D", entry.getValue().getD() + "")))
				entry.getValue()
						.setD(Double.parseDouble(SmartDashboard.getString(getName() + "_" + entry.getKey() + "D",
								entry.getValue().getD() + "")));
		});
	}

	/**
	 * Gets specified {@link PIDController}.
	 * 
	 * @param name
	 * @return pidController
	 */
	public PIDController getPIDController(String name) {
		return pidControllers.get(name);
	}

	/**
	 * Sets the specified {@link PIDController PIDController's} setpoint to the
	 * given value.
	 *
	 * @param name
	 * @param setpoint
	 */
	public void setSetpoint(String name, double setpoint) {
		pidControllers.get(name).setSetpoint(setpoint);
	}

	/**
	 * Returns the current setpoint of the specified {@link PIDController}.
	 *
	 * @param name
	 * @return setpoint
	 */
	public double getSetpoint(String name) {
		return pidControllers.get(name).getSetpoint();
	}

	/**
	 * Return true if the specified {@link PIDController} error is within the
	 * percentage of the total input range, determined by setTolerance.
	 *
	 * @param name
	 * @return atSetpoint
	 */
	public boolean onTarget(String name) {
		return pidControllers.get(name).atSetpoint();
	}
}