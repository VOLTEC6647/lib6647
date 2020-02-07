package org.usfirst.lib6647.subsystem;

import org.usfirst.lib6647.subsystem.hypercomponents.HyperPIDController;
import org.usfirst.lib6647.subsystem.supercomponents.SuperPID;

/**
 * Abstract class to allow usage of {@link #robotMap JSON files} for
 * {@link SuperSubsystem} creation, with added {@link HyperPIDController}
 * functionality.
 */
public abstract class PIDSuperSubsystem extends SuperSubsystem implements SuperPID {
	/**
	 * Constructor for {@link PIDSuperSubsystem}.
	 * 
	 * <p>
	 * Initializes {@link HyperPIDController HyperPIDController objects} declared in
	 * the {@link SuperSubsystem#robotMap JSON file}.
	 * 
	 * @param name The name of the {@link SuperSubsystem}
	 */
	public PIDSuperSubsystem(final String name) {
		super(name);

		initPIDs(robotMap, getName());
	}

	// This method can be overwritten in the case that constantly checking for PID
	// updates in the Shuffleboard proves to be inconvenient.
	@Override
	public void periodic() {
		pidControllers.values().forEach(HyperPIDController::updatePIDValues);
	}

	/**
	 * Sets the specified {@link HyperPIDController}'s setpoint to the given value.
	 *
	 * @param name     The name of the {@link HyperPIDController}
	 * @param setpoint The value to be set as the {@link HyperPIDController}'s
	 *                 setpoint
	 */
	public synchronized void setSetpoint(String name, double setpoint) {
		getPIDController(name).setSetpoint(setpoint);
	}

	/**
	 * Gets the current setpoint of the specified {@link HyperPIDController}.
	 *
	 * @param name The name of the {@link HyperPIDController}
	 * @return The specified {@link HyperPIDController}'s setpoint
	 */
	public synchronized double getSetpoint(String name) {
		return getPIDController(name).getSetpoint();
	}

	/**
	 * Return true if the specified {@link HyperPIDController} error is within the
	 * percentage of the total input range, determined by setTolerance.
	 *
	 * @param name The name of the {@link HyperPIDController}
	 * @return Whether or not the {@link HyperPIDController}'s target has been
	 *         reached (within the tolerance range).
	 */
	public synchronized boolean onTarget(String name) {
		return pidControllers.get(name).atSetpoint();
	}
}