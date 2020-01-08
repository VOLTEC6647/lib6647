package org.usfirst.lib6647.subsystem;

import com.fasterxml.jackson.databind.JsonNode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDSubsystem;
import edu.wpi.first.wpilibj2.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Abstract class to allow usage of {@link #robotMap JSON files} for
 * {@link Subsystem} creation, with added {@link PIDController} functionality.
 */
public abstract class PIDSuperSubsystem extends SuperSubsystem {

	/** Bread and butter of {@link PIDSuperSubsystem}. */
	protected JsonNode robotMap;
	/** Proportional, integral, and derivative constants. */
	private double p = 0.0, i = 0.0, d = 0.0;
	/** PID loop period time. Default: 0.02s (20ms). */
	private double period = 0.02;

	private PIDController controller;

	/**
	 * Constructor for {@link PIDSuperSubsystem}. Initializes the
	 * {@link PIDSubsystem} with 0.0f, 0.0f, and 0.0f as PID values, then replaces
	 * them with the values declared in {@link #robotMap}
	 * 
	 * @param name     (of the {@link PIDSuperSubsystem})
	 * @param fileName (to {@link #robotMap JSON file})
	 */
	public PIDSuperSubsystem(final String name) {
		super(name);

		// Pass robotMap on to the class extending this.
		robotMap = super.robotMap;

		initPID();
		outputPIDValues(name, p, i, d);
	}

	/**
	 * Method to initialize and set {@link PIDSuperSubsystem}'s PID values and
	 * configuration.
	 */
	private void initPID() {
		try {
			// Get JsonNode out of the 'pid' key.
			JsonNode pid = robotMap.get("pid");

			// Set PID values and period from JSON file.
			p = pid.get("p").asDouble();
			i = pid.get("i").asDouble();
			d = pid.get("d").asDouble();
			period = pid.get("period").asDouble();

			// Initialize PIDController with set values.
			controller = new PIDController(p, i, d, period);

			// Read and apply PIDSuperSubsystem configuration from JSON file.
			if (pid.get("continuous").asBoolean())
				enableContinuousInput(pid.get("inputMin").asDouble(), pid.get("inputMax").asDouble());
			else
				disableContinuousInput();

			setTolerance(pid.get("tolerance").asDouble());
		} catch (Exception e) {
			String error = String.format("[!] PIDSUBSYSTEM '%1$s' INIT ERROR:\n\t%2$s", getName().toUpperCase(),
					e.getLocalizedMessage());

			System.out.println(error);
			DriverStation.reportError(error, false);

			System.exit(1);
		}
	}

	/**
	 * Method to output {@link #p}, {@link #i}, and {@link #d} values from the
	 * {@link PIDSuperSubsystem} to the {@link SmartDashboard}.
	 * 
	 * @param subsystemName
	 * @param {@link        #p}
	 * @param {@link        #i}
	 * @param {@link        #d}
	 */
	private void outputPIDValues(final String subsystemName, final double p, final double i, final double d) {
		SmartDashboard.putString(subsystemName + "P", p + "");
		SmartDashboard.putString(subsystemName + "I", i + "");
		SmartDashboard.putString(subsystemName + "D", d + "");
	}

	/**
	 * Method to update {@link #p}, {@link #i}, and {@link #d} values as float from
	 * the {@link SmartDashboard}.
	 */
	public void updatePIDValues() {
		p = Double.parseDouble(SmartDashboard.getString(getName() + "P", p + ""));
		i = Double.parseDouble(SmartDashboard.getString(getName() + "I", i + ""));
		d = Double.parseDouble(SmartDashboard.getString(getName() + "D", d + ""));

		getPIDController().setPID(p, i, d);
	}

	/**
	 * Returns the {@link PIDController} used by this {@link PIDSuperSubsystem}. Use
	 * this if you would like to fine tune the pid loop.
	 *
	 * @return the {@link PIDController} used by this {@link PIDSuperSubsystem}
	 */
	public PIDController getPIDController() {
		return controller;
	}

	/**
	 * Adds the given value to the setpoint. If
	 * {@link PIDSuperSubsystem#setInputRange(double, double) setInputRange(...)}
	 * was used, then the bounds will still be honored by this method.
	 *
	 * @param deltaSetpoint the change in the setpoint
	 */
	public void setSetpointRelative(double deltaSetpoint) {
		setSetpoint(getPosition() + deltaSetpoint);
	}

	/**
	 * Sets the setpoint to the given value. If
	 * {@link PIDSuperSubsystem#setInputRange(double, double) setInputRange(...)}
	 * was called, then the given setpoint will be trimmed to fit within the range.
	 *
	 * @param setpoint the new setpoint
	 */
	public void setSetpoint(double setpoint) {
		controller.setSetpoint(setpoint);
	}

	/**
	 * Returns the setpoint.
	 *
	 * @return the setpoint
	 */
	public double getSetpoint() {
		return controller.getSetpoint();
	}

	/**
	 * Enables continuous input, and sets its minimum and maximum values.
	 *
	 * @param minimumInput the minimum value expected from the input
	 * @param maximumInput the maximum value expected from the output
	 */
	public void enableContinuousInput(double minimumInput, double maximumInput) {
		controller.enableContinuousInput(minimumInput, maximumInput);
	}

	/**
	 * Disables continuous input.
	 */
	public void disableContinuousInput() {
		controller.disableContinuousInput();
	}

	/**
	 * Set the absolute error which is considered tolerable for use with
	 * {@link PIDSuperSubsystem#onTarget onTarget()}.
	 *
	 * @param tolerance
	 */
	public void setTolerance(double tolerance) {
		controller.setTolerance(tolerance);
	}

	/**
	 * Return true if the error is within the percentage of the total input range,
	 * determined by setTolerance. This assumes that the maximum and minimum input
	 * were set using setInput.
	 *
	 * @return atSetpoint
	 */
	public boolean onTarget() {
		return controller.atSetpoint();
	}

	/**
	 * Returns the current sensor position.
	 *
	 * @return position
	 */
	public abstract double getPosition();
}