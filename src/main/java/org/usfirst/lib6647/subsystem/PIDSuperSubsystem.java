package org.usfirst.lib6647.subsystem;

import com.fasterxml.jackson.databind.JsonNode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Abstract class to allow usage of {@link #robotMap JSON files} for
 * {@link Subsystem} creation, with added {@link PIDController} functionality.
 */
public abstract class PIDSuperSubsystem extends SuperSubsystem {

	/** Bread and butter of {@link PIDSuperSubsystem}. */
	private JsonNode robotMap;
	/** Proportional, integral, and derivative constants. */
	private double p = 0.0, i = 0.0, d = 0.0;
	/** PID loop period time. Default: 0.05s (50ms). */
	private double period = 0.05;

	private PIDController controller;
	private final PIDOutput output = this::usePIDOutput;
	private final PIDSource source = new PIDSource() {
		@Override
		public void setPIDSourceType(final PIDSourceType pidSource) {
		}

		@Override
		public PIDSourceType getPIDSourceType() {
			return PIDSourceType.kDisplacement;
		}

		@Override
		public double pidGet() {
			return returnPIDInput();
		}
	};

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

		initPID();
		outputPIDValues(name, p, i, d);
		addChild("PIDController", controller);
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
			controller = new PIDController(p, i, d, source, output, period);

			// Read and apply PIDSuperSubsystem configuration from JSON file.
			setInputRange(pid.get("inputMin").asDouble(), pid.get("inputMax").asDouble());
			setOutputRange(pid.get("outputMin").asDouble(), pid.get("outputMax").asDouble());
			setAbsoluteTolerance(pid.get("absoluteTolerance").asDouble());
			controller.setContinuous(pid.get("continuous").asBoolean());
		} catch (Exception e) {
			String error = String.format("[!] PIDSUBSYSTEM '%S' INIT ERROR:\n\t%s", getName().toUpperCase(),
					e.getMessage());

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
	 * Returns the current position.
	 *
	 * @return the current position
	 */
	public double getPosition() {
		return returnPIDInput();
	}

	/**
	 * Sets the maximum and minimum values expected from the input.
	 *
	 * @param minimumInput the minimum value expected from the input
	 * @param maximumInput the maximum value expected from the output
	 */
	public void setInputRange(double minimumInput, double maximumInput) {
		controller.setInputRange(minimumInput, maximumInput);
	}

	/**
	 * Sets the maximum and minimum values to write.
	 *
	 * @param minimumOutput the minimum value to write to the output
	 * @param maximumOutput the maximum value to write to the output
	 */
	public void setOutputRange(double minimumOutput, double maximumOutput) {
		controller.setOutputRange(minimumOutput, maximumOutput);
	}

	/**
	 * Set the absolute error which is considered tolerable for use with OnTarget.
	 * The value is in the same range as the PIDInput values.
	 *
	 * @param tolerance the absolute tolerance
	 */
	public void setAbsoluteTolerance(double tolerance) {
		controller.setAbsoluteTolerance(tolerance);
	}

	/**
	 * Set the percentage error which is considered tolerable for use with OnTarget.
	 * (Value of 15.0 == 15 percent).
	 *
	 * @param percentage the percent tolerance
	 */
	public void setPercentTolerance(double percentage) {
		controller.setPercentTolerance(percentage);
	}

	/**
	 * Return true if the error is within the percentage of the total input range,
	 * determined by setTolerance. This assumes that the maximum and minimum input
	 * were set using setInput.
	 *
	 * @return true if the error is less than the tolerance
	 */
	public boolean onTarget() {
		return controller.onTarget();
	}

	/**
	 * Returns the input for the pid loop.
	 *
	 * <p>
	 * It returns the input for the pid loop, so if this Subsystem was based off of
	 * a gyro, then it should return the angle of the gyro.
	 *
	 * <p>
	 * All subclasses of {@link PIDSuperSubsystem} must override this method.
	 *
	 * @return the value the pid loop should use as input
	 */
	protected abstract double returnPIDInput();

	/**
	 * Uses the value that the pid loop calculated. The calculated value is the
	 * "output" parameter. This method is a good time to set motor values, maybe
	 * something along the lines of
	 * <code>driveline.tankDrive(output, -output)</code>.
	 *
	 * <p>
	 * All subclasses of {@link PIDSuperSubsystem} must override this method.
	 *
	 * @param output the value the pid loop calculated
	 */
	protected abstract void usePIDOutput(double output);

	/**
	 * Enables the internal {@link PIDController}.
	 */
	public void enable() {
		controller.enable();
	}

	/**
	 * Disables the internal {@link PIDController}.
	 */
	public void disable() {
		controller.disable();
	}
}