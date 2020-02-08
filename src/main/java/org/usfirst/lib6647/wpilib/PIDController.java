/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.lib6647.wpilib;

import org.usfirst.lib6647.util.ControllerUtil;

import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import edu.wpi.first.wpiutil.math.MathUtil;

/**
 * Implements a PID control loop.
 */
public class PIDController implements Sendable, AutoCloseable {
	private static int instances;

	/** Names of both the {@link PIDController} and its subsystem. */
	private final String name, subsystemName;

	/**
	 * The {@link PIDController}'s {@link #p Proportional} , {@link #i Integral} ,
	 * and {@link #d Derivative} coefficients.
	 */
	private double p, i, d;

	/** The period (in seconds) of the loop that calls the controller. */
	private final double period;

	/** Minimum and maximum integral values. */
	private double minimumIntegral = -1.0, maximumIntegral = 1.0;

	/**
	 * {@link #minimumInput Minimum} and {@link #maximumInput maximum} input values
	 * — limit setpoint to this.
	 */
	private double minimumInput = Double.NEGATIVE_INFINITY, maximumInput = Double.POSITIVE_INFINITY;

	/**
	 * {@link #minimumOutput Minimum} and {@link #maximumOutput maximum} output
	 * values — limit output to this.
	 */
	private double minimumOutput = -1.0, maximumOutput = 1.0;

	/**
	 * Whether the {@link PIDController PID loop}'s endpoints wrap around.
	 * 
	 * <p>
	 * e.g. -180° and 180° on a gyro.
	 */
	private boolean continuous;

	/**
	 * The error at the time of the most recent call to {@link #calculate(double)}.
	 */
	private double positionError, velocityError;

	/**
	 * The error at the time of the second-most-recent call to calculate() (used to
	 * compute velocity).
	 */
	private double prevError;

	/** The sum of the errors for use in the integral calculation. */
	private double totalError;

	/** The percentage or absolute error that is considered at setpoint. */
	private double positionTolerance = 0.05, velocityTolerance = Double.POSITIVE_INFINITY;

	/** The {@link PIDController}'s current goal/target. */
	private double setpoint;

	/**
	 * Allocates a {@link PIDController} with the given constants for PID gain, and
	 * a default period of 0.02 seconds.
	 *
	 * @param name          The {@link PIDController}'s {@link #name}
	 * @param subsystemName The {@link #subsystemName name} of the Subsystem this
	 *                      {@link PIDController} belongs to
	 * @param p             The {@link PIDController}'s {@link #p Proportional}
	 *                      coefficient
	 * @param i             The {@link PIDController}'s {@link #i Integral}
	 *                      coefficient
	 * @param d             The {@link PIDController}'s {@link #d Derivative}
	 *                      coefficient
	 */
	public PIDController(String name, String subsystemName, double p, double i, double d) {
		this(name, subsystemName, p, i, d, 0.02);
	}

	/**
	 * Allocates a {@link PIDController} with the given constants for PID gain.
	 *
	 * @param name          The {@link PIDController}'s {@link #name}
	 * @param subsystemName The {@link #subsystemName name} of the Subsystem this
	 *                      {@link PIDController} belongs to
	 * @param p             The {@link PIDController}'s {@link #p Proportional}
	 *                      coefficient
	 * @param i             The {@link PIDController}'s {@link #i Integral}
	 *                      coefficient
	 * @param d             The {@link PIDController}'s {@link #d Derivative}
	 *                      coefficient
	 * @param period        The period between controller updates in seconds
	 */
	public PIDController(String name, String subsystemName, double p, double i, double d, double period) {
		this.name = name;
		this.subsystemName = subsystemName;

		this.p = p;
		this.i = i;
		this.d = d;

		this.period = period;

		instances++;
		SendableRegistry.addLW(this, "PIDController", instances);

		HAL.report(tResourceType.kResourceType_PIDController2, instances);
	}

	@Override
	public void close() {
		SendableRegistry.remove(this);
	}

	/**
	 * Gets this {@link PIDController}'s {@link #name}.
	 * 
	 * @return The {@link #name} of this {@link PIDController}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets this {@link PIDController}'s Subsystem's name.
	 * 
	 * @return The {@link #subsystemName} of the Subsystem this
	 *         {@link PIDController} belongs to
	 */
	public String getSubsystemName() {
		return subsystemName;
	}

	/**
	 * Sets the {@link PIDController} gain parameters.
	 *
	 * <p>
	 * Set the {@link #p Proportional}, {@link #i Integral}, and {@link #d
	 * Derivative} coefficients.
	 *
	 * @param p The {@link PIDController}'s {@link #p Proportional} coefficient
	 * @param i The {@link PIDController}'s {@link #i Integral} coefficient
	 * @param d The {@link PIDController}'s {@link #d Derivative} coefficient
	 */
	public void setPID(double p, double i, double d) {
		this.p = p;
		this.i = i;
		this.d = d;
	}

	/**
	 * Sets the {@link #p Proportional} coefficient of the {@link PIDController}
	 * gain.
	 *
	 * @param p The {@link PIDController}'s {@link #p Proportional} coefficient
	 */
	public void setP(double p) {
		this.p = p;
	}

	/**
	 * Sets the {@link #i Integral} coefficient of the {@link PIDController} gain.
	 *
	 * @param i The {@link PIDController}'s {@link #i Integral} coefficient
	 */
	public void setI(double i) {
		this.i = i;
	}

	/**
	 * Sets the Differential coefficient of the {@link PIDController} gain.
	 *
	 * @param d The {@link PIDController}'s {@link #d Derivative} coefficient
	 */
	public void setD(double d) {
		this.d = d;
	}

	/**
	 * Get the {@link #p Proportional} coefficient.
	 *
	 * @return The {@link #p Proportional} coefficient
	 */
	public double getP() {
		return p;
	}

	/**
	 * Get the {@link #i Integral} coefficient.
	 *
	 * @return The {@link #i Integral} coefficient
	 */
	public double getI() {
		return i;
	}

	/**
	 * Get the {@link #d Derivative} coefficient.
	 *
	 * @return The {@link #d Derivative} coefficient
	 */
	public double getD() {
		return d;
	}

	/**
	 * Returns the {@link #period} of this {@link PIDController}.
	 *
	 * @return The {@link #period} of the {@link PIDController}
	 */
	public double getPeriod() {
		return period;
	}

	/**
	 * Sets the {@link #setpoint} for the {@link PIDController}.
	 *
	 * @param setpoint The desired {@link #setpoint}
	 */
	public void setSetpoint(double setpoint) {
		this.setpoint = setpoint;
	}

	/**
	 * Returns the current {@link #setpoint} of the {@link PIDController}.
	 *
	 * @return The current {@link #setpoint}.
	 */
	public double getSetpoint() {
		return setpoint;
	}

	/**
	 * Returns true if the {@link #positionError} and {@link #velocityError} are
	 * within the percentage of the total input range, determined by
	 * {@link #setTolerance(double)}. This asssumes that the {@link #maximumInput
	 * maximum} and {@link #minimumInput minimum} inputs were set using
	 * {@link #enableContinuousInput(double, double)}.
	 *
	 * <p>
	 * This will return false until at least one input value has been computed.
	 *
	 * @return Whether the {@link #positionError} and {@link #velocityError} is
	 *         within the acceptable bounds
	 */
	public boolean atSetpoint() {
		return Math.abs(positionError) < positionTolerance && Math.abs(velocityError) < velocityTolerance;
	}

	/**
	 * Enables continuous input.
	 *
	 * <p>
	 * Rather then using the max and min input range as constraints, it considers
	 * them to be the same point and automatically calculates the shortest route to
	 * the setpoint.
	 *
	 * @param minimumInput The minimum value expected from the input
	 * @param maximumInput The maximum value expected from the input
	 */
	public void enableContinuousInput(double minimumInput, double maximumInput) {
		continuous = true;

		this.minimumInput = minimumInput;
		this.maximumInput = maximumInput;
	}

	/**
	 * Disables continuous input.
	 */
	public void disableContinuousInput() {
		continuous = false;
	}

	/**
	 * Returns true if continuous input is enabled, must be enabled via
	 * {@link #enableContinuousInput(double, double)}.
	 * 
	 * @return Whether or not continuous input is enabled.
	 */
	public boolean isContinuous() {
		return continuous;
	}

	/**
	 * Sets the minimum and maximum values for the integrator.
	 *
	 * <p>
	 * When the cap is reached, the integrator value is added to the controller
	 * output rather than the integrator value times the {@link #i Integral} gain.
	 *
	 * @param minimumIntegral The minimum value of the integrator
	 * @param maximumIntegral The maximum value of the integrator
	 */
	public void setIntegratorRange(double minimumIntegral, double maximumIntegral) {
		this.minimumIntegral = minimumIntegral;
		this.maximumIntegral = maximumIntegral;
	}

	/**
	 * Sets this {@link PIDController}'s {@link #minimumOutput minimum} and
	 * {@link #maximumOutput maximum} output values.
	 * 
	 * @param minimumOutput The {@link PIDController}'s {@link #minimumOutput
	 *                      minimum}
	 * @param maximumOutput The {@link PIDController}'s {@link #maximumOutput
	 *                      maximum}
	 */
	public void setOutputRange(double minimumOutput, double maximumOutput) {
		this.minimumOutput = minimumOutput;
		this.maximumOutput = maximumOutput;
	}

	/**
	 * Sets the error which is considered tolerable for use with
	 * {@link #atSetpoint()}.
	 *
	 * @param positionTolerance Position error which is tolerable
	 */
	public void setTolerance(double positionTolerance) {
		setTolerance(positionTolerance, Double.POSITIVE_INFINITY);
	}

	/**
	 * Sets the error which is considered tolerable for use with
	 * {@link #atSetpoint()}.
	 *
	 * @param positionTolerance Position error which is tolerable
	 * @param velocityTolerance Velocity error which is tolerable
	 */
	public void setTolerance(double positionTolerance, double velocityTolerance) {
		this.positionTolerance = positionTolerance;
		this.velocityTolerance = velocityTolerance;
	}

	/**
	 * Gets the difference between the {@link #setpoint} and the measurement.
	 *
	 * @return The {@link #positionError position error}
	 */
	public double getPositionError() {
		return positionError;
	}

	/**
	 * Gets the current {@link #velocityError velocity error}.
	 * 
	 * @return The {@link #velocityError velocity error}
	 */
	public double getVelocityError() {
		return velocityError;
	}

	/**
	 * Returns the next output of the {@link PIDController}.
	 *
	 * @param measurement The current measurement of the process variable.
	 * @param setpoint    The new {@link #setpoint} of the {@link PIDController}.
	 */
	public double calculate(double measurement, double setpoint) {
		// Set setpoint to provided value
		setSetpoint(setpoint);
		return calculate(measurement);
	}

	/**
	 * Returns the next output of the {@link PIDController}.
	 *
	 * @param measurement The current measurement of the process variable.
	 */
	public double calculate(double measurement) {
		prevError = positionError;

		positionError = continuous ? ControllerUtil.getModulusError(setpoint, measurement, minimumInput, maximumInput)
				: setpoint - measurement;
		velocityError = (positionError - prevError) / period;

		if (i != 0)
			totalError = MathUtil.clamp(totalError + positionError * period, minimumIntegral / i, maximumIntegral / i);

		return MathUtil.clamp(p * positionError + i * totalError + d * velocityError, minimumOutput, maximumOutput);
	}

	/**
	 * Resets the previous error and the integral term.
	 */
	public void reset() {
		prevError = 0;
		totalError = 0;
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("RobotPreferences");

		builder.addDoubleProperty(subsystemName + "_" + name + "P", this::getP, this::setP);
		builder.addDoubleProperty(subsystemName + "_" + name + "I", this::getI, this::setI);
		builder.addDoubleProperty(subsystemName + "_" + name + "D", this::getD, this::setD);
		
		builder.addDoubleProperty(subsystemName + "_" + name + "Setpoint", this::getSetpoint, this::setSetpoint);
	}
}
