/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019-2020 FIRST. All Rights Reserved.                        */
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
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

/**
 * Implements a {@link PIDController PID control loop} whose
 * {@link PIDController#getSetpoint() setpoint} is constrained by a trapezoid
 * profile. Users should call {@link #reset(double)} when they first start
 * running the {@link PIDController} to avoid unwanted behavior.
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

	private TrapezoidProfile.State goal = new TrapezoidProfile.State(), setpoint = new TrapezoidProfile.State();
	private TrapezoidProfile.Constraints constraints;

	/**
	 * Allocates a {@link ProfiledPIDController} with the given constants for
	 * Proportional, Integral, and Derivative gains.
	 *
	 * @param name          The name of this {@link ProfiledPIDController}
	 * @param subsystemName The name of the Subsystem this
	 *                      {@link ProfiledPIDController} belongs to
	 * @param p             The Proportional coefficient
	 * @param i             The Integral coefficient
	 * @param d             The Derivative coefficient
	 * @param constraints   Velocity and acceleration constraints for goal
	 */
	public ProfiledPIDController(String name, String subsystemName, double p, double i, double d,
			TrapezoidProfile.Constraints constraints) {
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
	 * @param constraints   Velocity and acceleration constraints for goal
	 * @param period        The period between controller updates in seconds
	 */
	public ProfiledPIDController(String name, String subsystemName, double p, double i, double d,
			TrapezoidProfile.Constraints constraints, double period) {
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
	public void setGoal(TrapezoidProfile.State goal) {
		this.goal = goal;
	}

	/**
	 * Sets the {@link #goal} for the {@link ProfiledPIDController}.
	 *
	 * @param goal The desired {@link #goal} position
	 */
	public void setGoalPosition(double goal) {
		setGoal(goal, 0);
	}

	/**
	 * Sets the {@link #goal} for the {@link ProfiledPIDController}.
	 *
	 * @param goal The desired {@link #goal} velocity
	 */
	public void setGoalVelocity(double goal) {
		setGoal(0, goal);
	}

	/**
	 * Sets the {@link #goal} for the {@link ProfiledPIDController}.
	 *
	 * @param goal The desired {@link #goal} position
	 * @param goal The desired {@link #goal} velocity
	 */
	public void setGoal(double positionGoal, double velocityGoal) {
		goal = new TrapezoidProfile.State(positionGoal, velocityGoal);
	}

	/**
	 * Gets the {@link #goal} for the {@link ProfiledPIDController}.
	 */
	public TrapezoidProfile.State getGoal() {
		return goal;
	}

	/**
	 * Returns true if the error is within the tolerance of the error.
	 *
	 * <p>
	 * This will return false until at least one input value has been computed.
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
		setConstraints(new TrapezoidProfile.Constraints(maxVelocity, constraints.maxAcceleration));
	}

	/**
	 * Set acceleration {@link #constraints constraint} for {@link #goal}.
	 *
	 * @param maxAcceleration This {@link ProfiledPIDController}'s max acceleration
	 */
	public void setConstraintAcceleration(double maxAcceleration) {
		setConstraints(new TrapezoidProfile.Constraints(constraints.maxVelocity, maxAcceleration));
	}

	/**
	 * Set velocity and acceleration {@link #constraints} for {@link #goal}.
	 *
	 * @param maxVelocity     This {@link ProfiledPIDController}'s max velocity
	 * @param maxAcceleration This {@link ProfiledPIDController}'s max acceleration
	 */
	public void setConstraints(double maxVelocity, double maxAcceleration) {
		setConstraints(new TrapezoidProfile.Constraints(maxVelocity, maxAcceleration));
	}

	/**
	 * Set velocity and acceleration {@link #constraints} for {@link #goal}.
	 *
	 * @param constraints Velocity and acceleration {@link #constraints} for
	 *                    {@link #goal}.
	 */
	public void setConstraints(TrapezoidProfile.Constraints constraints) {
		this.constraints = constraints;
	}

	/**
	 * Returns the current {@link #setpoint} of the {@link ProfiledPIDController}.
	 *
	 * @return The current {@link #setpoint}.
	 */
	public TrapezoidProfile.State getSetpoint() {
		return setpoint;
	}

	/**
	 * Returns this {@link ProfiledPIDController}'s {@link PIDController}.
	 * 
	 * @return This {@link ProfiledPIDController}'s {@link PIDController}
	 */
	public PIDController getPIDController() {
		return controller;
	}

	/**
	 * Returns the next output of the {@link #controller PID controller}.
	 *
	 * @param measurement The current measurement of the process variable
	 */
	public double calculate(double measurement) {
		if (controller.isContinuous()) {
			// Get error which is smallest distance between goal and measurement
			double error = ControllerUtil.getModulusError(goal.position, measurement, minimumInput, maximumInput);

			// Recompute the profile goal with the smallest error, thus giving the shortest
			// path. The goal
			// may be outside the input range after this operation, but that's OK because
			// the controller
			// will still go there and report an error of zero. In other words, the setpoint
			// only needs to
			// be offset from the measurement by the input range modulus; they don't need to
			// be equal.
			goal.position = error + measurement;
		}

		var profile = new TrapezoidProfile(constraints, goal, setpoint);
		setpoint = profile.calculate(getPeriod());
		return controller.calculate(measurement, setpoint.position);
	}

	/**
	 * Returns the next output of the {@link #controller PID controller}.
	 *
	 * @param measurement The current measurement of the process variable
	 * @param goal        The new {@link #goal} position of the {@link #controller}
	 */
	public double calculate(double measurement, TrapezoidProfile.State goal) {
		setGoal(goal);
		return calculate(measurement);
	}

	/**
	 * Returns the next output of the {@link #controller PID controller}.
	 *
	 * @param measurement The current measurement of the process variable
	 * @param goal        The new {@link #goal} position of the {@link #controller}
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

		builder.addDoubleProperty(subsystemName + "_" + name + "GoalPosition", () -> goal.position,
				this::setGoalPosition);
		builder.addDoubleProperty(subsystemName + "_" + name + "GoalVelocity", () -> goal.position,
				this::setGoalVelocity);
	}
}