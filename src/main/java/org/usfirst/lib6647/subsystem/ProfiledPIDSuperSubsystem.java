package org.usfirst.lib6647.subsystem;

import org.usfirst.lib6647.subsystem.supercomponents.SuperProfiledPID;
import org.usfirst.lib6647.wpilib.ProfiledPIDController;

import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile.State;

/**
 * Abstract class to allow usage of {@link #robotMap JSON files} for
 * {@link SuperSubsystem} creation, with added {@link ProfiledPIDController}
 * functionality.
 */
public class ProfiledPIDSuperSubsystem extends SuperSubsystem implements SuperProfiledPID {
	/**
	 * Constructor for {@link ProfiledPIDSuperSubsystem}.
	 * 
	 * <p>
	 * Initializes {@link ProfiledPIDController ProfiledPIDController objects}
	 * declared in the {@link SuperSubsystem#robotMap JSON file}.
	 * 
	 * @param name The name of the {@link SuperSubsystem}
	 */
	public ProfiledPIDSuperSubsystem(final String name) {
		super(name);

		initProfiledPIDs(robotMap, getName());
	}

	// This method can be overwritten in the case that constantly checking for PID
	// updates in the Shuffleboard proves to be inconvenient.
	@Override
	public void periodic() {
		profiledPIDControllers.values().forEach(ProfiledPIDController::updatePIDValues);
	}

	/**
	 * Sets the specified {@link ProfiledPIDController}'s goal to the given value.
	 *
	 * @param name The name of the {@link ProfiledPIDController}
	 * @param goal The value to be set as the {@link ProfiledPIDController}'s goal
	 */
	public synchronized void setGoal(String name, double goal) {
		getProfiledPIDController(name).setGoal(goal);
	}

	/**
	 * Gets the current setpoint of the specified {@link ProfiledPIDController}.
	 *
	 * @param name The name of the {@link ProfiledPIDController}
	 * @return The specified {@link ProfiledPIDController}'s goal as a {@link State}
	 */
	public synchronized State getGoal(String name) {
		return getProfiledPIDController(name).getGoal();
	}

	/**
	 * Return true if the specified {@link ProfiledPIDController} error is within
	 * the percentage of the total input range, determined by setTolerance.
	 *
	 * @param name The name of the {@link ProfiledPIDController}
	 * @return Whether or not the {@link ProfiledPIDController}'s goal has been
	 *         reached (within the tolerance range).
	 */
	public synchronized boolean onTarget(String name) {
		return getProfiledPIDController(name).atGoal();
	}
}