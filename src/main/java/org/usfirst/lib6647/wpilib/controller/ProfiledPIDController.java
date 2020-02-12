/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.lib6647.wpilib.controller;

import org.usfirst.lib6647.util.ControllerUtil;
import org.usfirst.lib6647.wpilib.trajectory.TrapezoidProfile;
import org.usfirst.lib6647.wpilib.trajectory.TrapezoidProfile.Constraints;
import org.usfirst.lib6647.wpilib.trajectory.TrapezoidProfile.State;

import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;

/**
 * Implements a {@link PIDController PID control loop} whose
 * {@link PIDController#getSetpoint() setpoint} is constrained by a
 * {@link TrapezoidProfile trapezoid profile}. Users should call
 * {@link #reset(double)} when they first start running the {@link #controller
 * PID controller} to avoid unwanted behavior.
 */
public class ProfiledPIDController implements Sendable {
	private static int instances;

	/** Names of both the {@link PIDController} and its subsystem. */
	private final String name, subsystemName;

	/**
	 * Instance of the {@link PIDController} used by this
	 * {@link ProfiledPIDController}.
	 */
	private PIDController controller;

	/**
	 * {@link #minimumInput Minimum} and {@link #maximumInput maximum} input values
	 * — limit setpoint to this.
	 */
	private double minimumInput, maximumInput;

	private State goal = new State(), setpoint = new State();
	private Constraints constraints;

	/**
	 * Allocates a {@link ProfiledPIDController} with the given constants for
	 * {@link PIDController#getP() Proportional}, {@link PIDController#getI()
	 * Integral}, and {@link PIDController#getD() Derivative} gains.
	 *
	 * @param name          The name of this {@link ProfiledPIDController}
	 * @param subsystemName The name of the Subsystem this
	 *                      {@link ProfiledPIDController} belongs to
	 * @param p             The {@link ProfiledPIDController}'s
	 *                      {@link PIDController#getP() Proportional} coefficient
	 * @param i             The {@link ProfiledPIDController}'s
	 *                      {@link PIDController#getI() Integral} coefficient
	 * @param d             The {@link ProfiledPIDController}'s
	 *                      {@link PIDController#getD() Derivative} coefficient
	 * @param constraints   Velocity and acceleration constraints for {@link #goal}
	 */
	public ProfiledPIDController(String name, String subsystemName, double p, double i, double d,
			Constraints constraints) {
		this(name, subsystemName, p, i, d, constraints, 0.02);
	}

	/**
	 * Allocates a {@link ProfiledPIDController} with the given constants for
	 * {@link PIDController#getP() Proportional}, {@link PIDController#getI()
	 * Integral}, and {@link PIDController#getD() Derivative} gains.
	 *
	 * @param name          The name of this {@link ProfiledPIDController}
	 * @param subsystemName The name of the Subsystem this
	 *                      {@link ProfiledPIDController} belongs to
	 * @param p             The {@link ProfiledPIDController}'s
	 *                      {@link PIDController#getP() Proportional} coefficient
	 * @param i             The {@link ProfiledPIDController}'s
	 *                      {@link PIDController#getI() Integral} coefficient
	 * @param d             The {@link ProfiledPIDController}'s
	 *                      {@link PIDController#getD() Derivative} coefficient
	 * @param constraints   Velocity and acceleration constraints for {@link #goal}
	 * @param period        The period between controller updates in seconds
	 */
	public ProfiledPIDController(String name, String subsystemName, double p, double i, double d,
			Constraints constraints, double period) {
		this.name = name;
		this.subsystemName = subsystemName;

		controller = new PIDController(name, subsystemName, p, i, d, period);
		this.constraints = constraints;

		instances++;
		SendableRegistry.addLW(this, "ProfiledPIDController", instances);

		HAL.report(tResourceType.kResourceType_ProfiledPIDController, instances);
	}

	/**
	 * Sets the {@link #controller PID loop} gain parameters.
	 *
	 * <p>
	 * Sets the {@link PIDController#getP() Proportional},
	 * {@link PIDController#getI() Integral}, and {@link PIDController#getD()
	 * Derivative} coefficients.
	 *
	 * @param p The {@link ProfiledPIDController}'s {@link PIDController#getP()
	 *          Proportional} coefficient
	 * @param i The {@link ProfiledPIDController}'s {@link PIDController#getI()
	 *          Integral} coefficient
	 * @param d The {@link ProfiledPIDController}'s {@link PIDController#getD()
	 *          Derivative} coefficient
	 */
	public void setPID(double p, double i, double d) {
		controller.setPID(p, i, d);
	}

	/**
	 * Sets the {@link PIDController#getP() Proportional} coefficient of the
	 * {@link #controller} gain.
	 *
	 * @param p {@link PIDController#getP() Proportional} coefficient
	 */
	public void setP(double p) {
		controller.setP(p);
	}

	/**
	 * Sets the {@link PIDController#getI() Integral} coefficient of the
	 * {@link #controller} gain.
	 *
	 * @param i {@link PIDController#getI() Integral} coefficient
	 */
	public void setI(double i) {
		controller.setI(i);
	}

	/**
	 * Sets the {@link PIDController#getD() Derivative} coefficient of the
	 * {@link #controller} gain.
	 *
	 * @param d {@link PIDController#getD() Derivative} coefficient
	 */
	public void setD(double d) {
		controller.setD(d);
	}

	/**
	 * Gets the {@link PIDController#getP() Proportional} coefficient.
	 *
	 * @return The {@link #controller}'s {@link PIDController#getP() Proportional}
	 *         coefficient
	 */
	public double getP() {
		return controller.getP();
	}

	/**
	 * Gets the {@link PIDController#getI() Integral} coefficient.
	 *
	 * @return The {@link #controller}'s {@link PIDController#getI() Integral}
	 *         coefficient
	 */
	public double getI() {
		return controller.getI();
	}

	/**
	 * Gets the {@link PIDController#getD() Derivative} coefficient.
	 *
	 * @return The {@link #controller}'s {@link PIDController#getD() Derivative}
	 *         coefficient
	 */
	public double getD() {
		return controller.getD();
	}

	/**
	 * Gets the {@link PIDController#getPeriod() period} of this
	 * {@link ProfiledPIDController}.
	 *
	 * @return The {@link PIDController#getPeriod() period} of the
	 *         {@link #controller}
	 */
	public double getPeriod() {
		return controller.getPeriod();
	}

	/**
	 * Sets the {@link #goal} for the {@link ProfiledPIDController}.
	 *
	 * @param goal The desired {@link #goal} state
	 */
	public void setGoal(State goal) {
		this.goal = goal;
	}

	/**
	 * Sets the {@link #goal} for the {@link ProfiledPIDController}.
	 *
	 * @param goal The desired {@link #goal} position
	 */
	public void setGoalPosition(double goal) {
		setGoal(goal, this.goal.velocity);
	}

	/**
	 * Sets the {@link #goal} for the {@link ProfiledPIDController}.
	 *
	 * @param goal The desired {@link #goal} velocity
	 */
	public void setGoalVelocity(double goal) {
		setGoal(this.goal.position, goal);
	}

	/**
	 * Sets the {@link #goal} for the {@link ProfiledPIDController}.
	 *
	 * @param positionGoal The desired {@link #goal} position
	 * @param velocityGoal The desired {@link #goal} velocity
	 */
	public void setGoal(double positionGoal, double velocityGoal) {
		goal = new State(positionGoal, velocityGoal);
	}

	/**
	 * Gets the {@link #goal} for the {@link ProfiledPIDController}.
	 * 
	 * @return The {@link ProfiledPIDController}'s {@link #goal}
	 */
	public State getGoal() {
		return goal;
	}

	/**
	 * Returns true if the error is within the tolerance of the error.
	 *
	 * <p>
	 * This will return false until at least one input value has been computed.
	 * 
	 * @return Whether the {@link ProfiledPIDController} is at its goal or not
	 */
	public boolean atGoal() {
		return controller.atSetpoint() && goal.equals(setpoint);
	}

	/**
	 * Set velocity {@link #constraints constraint} for {@link #goal}.
	 *
	 * @param maxVelocity This {@link ProfiledPIDController}'s max velocity
	 */
	public void setConstraintVelocity(double maxVelocity) {
		setConstraints(new Constraints(maxVelocity, constraints.maxAcceleration));
	}

	/**
	 * Set acceleration {@link #constraints constraint} for {@link #goal}.
	 *
	 * @param maxAcceleration This {@link ProfiledPIDController}'s max acceleration
	 */
	public void setConstraintAcceleration(double maxAcceleration) {
		setConstraints(new Constraints(constraints.maxVelocity, maxAcceleration));
	}

	/**
	 * Set velocity and acceleration {@link #constraints} for {@link #goal}.
	 *
	 * @param maxVelocity     This {@link ProfiledPIDController}'s max velocity
	 * @param maxAcceleration This {@link ProfiledPIDController}'s max acceleration
	 */
	public void setConstraints(double maxVelocity, double maxAcceleration) {
		setConstraints(new Constraints(maxVelocity, maxAcceleration));
	}

	/**
	 * Set velocity and acceleration {@link #constraints} for {@link #goal}.
	 *
	 * @param constraints Velocity and acceleration {@link #constraints} for
	 *                    {@link #goal}
	 */
	public void setConstraints(Constraints constraints) {
		this.constraints = constraints;
	}

	/**
	 * Returns the current {@link #setpoint} of the {@link ProfiledPIDController}.
	 *
	 * @return The current {@link #setpoint}
	 */
	public State getSetpoint() {
		return setpoint;
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
		this.minimumInput = minimumInput;
		this.maximumInput = maximumInput;

		controller.enableContinuousInput(minimumInput, maximumInput);
	}

	/**
	 * Disables continuous input.
	 */
	public void disableContinuousInput() {
		this.minimumInput = Double.NEGATIVE_INFINITY;
		this.maximumInput = Double.POSITIVE_INFINITY;

		controller.disableContinuousInput();
	}

	/**
	 * Sets the minimum and maximum values for the integrator.
	 *
	 * <p>
	 * When the cap is reached, the integrator value is added to the controller
	 * output rather than the integrator value times the integral gain.
	 *
	 * @param minimumIntegral The minimum value of the integrator
	 * @param maximumIntegral The maximum value of the integrator
	 */
	public void setIntegratorRange(double minimumIntegral, double maximumIntegral) {
		controller.setIntegratorRange(minimumIntegral, maximumIntegral);
	}

	/**
	 * Sets the {@link #controller PID controller}'s minimum and maximum output
	 * values.
	 * 
	 * @param minimumOutput The {@link PIDController}'s {@link #minimumInput
	 *                      minimum}
	 * @param maximumOutput The {@link PIDController}'s {@link #maximumInput
	 *                      maximum}
	 */
	public void setOutputRange(double minimumOutput, double maximumOutput) {
		controller.setOutputRange(minimumOutput, maximumOutput);
	}

	/**
	 * Sets the error which is considered tolerable for use with atSetpoint().
	 *
	 * @param positionTolerance Position error which is tolerable
	 */
	public void setTolerance(double positionTolerance) {
		setTolerance(positionTolerance, Double.POSITIVE_INFINITY);
	}

	/**
	 * Sets the error which is considered tolerable for use with atSetpoint().
	 *
	 * @param positionTolerance Position error which is tolerable
	 * @param velocityTolerance Velocity error which is tolerable
	 */
	public void setTolerance(double positionTolerance, double velocityTolerance) {
		controller.setTolerance(positionTolerance, velocityTolerance);
	}

	/**
	 * Returns the difference between the {@link #setpoint} and the measurement.
	 *
	 * @return The position error
	 */
	public double getPositionError() {
		return controller.getPositionError();
	}

	/**
	 * Returns the change in error per second.
	 * 
	 * @return The velocity error
	 */
	public double getVelocityError() {
		return controller.getVelocityError();
	}

	/**
	 * Returns the next output of the {@link #controller PID controller}.
	 *
	 * @param measurement The current measurement of the process variable
	 * @return This {@link ProfiledPIDController}'s calculated output
	 */
	public double calculate(double measurement) {
		TrapezoidProfile profile;

		if (controller.isContinuous()) {
			// Get error which is smallest distance between goal and measurement
			var error = ControllerUtil.getModulusError(goal.position, measurement, minimumInput, maximumInput);
			goal.position = error + measurement;

			profile = new TrapezoidProfile(constraints, goal, setpoint, true, error);
		} else
			profile = new TrapezoidProfile(constraints, goal, setpoint);

		setpoint = profile.calculate(getPeriod());
		return controller.calculate(measurement, setpoint.position);
	}

	/**
	 * Returns the next output of the {@link #controller PID controller}.
	 *
	 * @param measurement The current measurement of the process variable
	 * @param goal        The new {@link #goal} position of the {@link #controller}
	 * @return This {@link ProfiledPIDController}'s calculated output
	 */
	public double calculate(double measurement, State goal) {
		setGoal(goal);
		return calculate(measurement);
	}

	/**
	 * Returns the next output of the {@link #controller PID controller}.
	 *
	 * @param measurement The current measurement of the process variable
	 * @param goal        The new {@link #goal} position of the {@link #controller}
	 * @return This {@link ProfiledPIDController}'s calculated output
	 */
	public double calculate(double measurement, double goal) {
		setGoal(goal, 0);
		return calculate(measurement);
	}

	/**
	 * Returns the next output of the {@link #controller PID controller}.
	 *
	 * @param measurement  The current measurement of the process variable
	 * @param positionGoal The new {@link #goal} position of the {@link #controller}
	 * @param velocityGoal The new {@link #goal} velocity of the {@link #controller}
	 * @return This {@link ProfiledPIDController}'s calculated output
	 */
	public double calculate(double measurement, double positionGoal, double velocityGoal) {
		setGoal(positionGoal, velocityGoal);
		return calculate(measurement);
	}

	/**
	 * Returns the next output of the {@link #controller PID controller}.
	 *
	 * @param measurement The current measurement of the process variable.
	 * @param goal        The new {@link #goal} of the {@link #controller}.
	 * @param constraints Velocity and acceleration constraints for {@link #goal}.
	 * @return This {@link ProfiledPIDController}'s calculated output
	 */
	public double calculate(double measurement, TrapezoidProfile.State goal, TrapezoidProfile.Constraints constraints) {
		setConstraints(constraints);
		return calculate(measurement, goal);
	}

	/**
	 * Reset the previous error and the integral term.
	 *
	 * @param measurement The current measured State of the system.
	 */
	public void reset(TrapezoidProfile.State measurement) {
		controller.reset();
		setpoint = measurement;
	}

	/**
	 * Reset the previous error and the integral term.
	 *
	 * @param measuredPosition The current measured position of the system.
	 * @param measuredVelocity The current measured velocity of the system.
	 */
	public void reset(double measuredPosition, double measuredVelocity) {
		reset(new TrapezoidProfile.State(measuredPosition, measuredVelocity));
	}

	/**
	 * Reset the previous error and the integral term.
	 *
	 * @param measuredPosition The current measured position of the system. The
	 *                         velocity is assumed to be zero.
	 */
	public void reset(double measuredPosition) {
		reset(measuredPosition, 0.0);
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("RobotPreferences");

		builder.addDoubleProperty(subsystemName + "_" + name + "P", this::getP, this::setP);
		builder.addDoubleProperty(subsystemName + "_" + name + "I", this::getI, this::setI);
		builder.addDoubleProperty(subsystemName + "_" + name + "D", this::getD, this::setD);

		builder.addDoubleProperty(subsystemName + "_" + name + "MaxVelocity", () -> constraints.maxVelocity,
				this::setConstraintVelocity);
		builder.addDoubleProperty(subsystemName + "_" + name + "MaxAcceleration", () -> constraints.maxAcceleration,
				this::setConstraintAcceleration);
	}
}