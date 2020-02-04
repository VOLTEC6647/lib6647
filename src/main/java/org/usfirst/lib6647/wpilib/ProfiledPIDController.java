/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.lib6647.wpilib;

import org.usfirst.lib6647.subsystem.hypercomponents.HyperPIDController;

import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;

/**
 * Implements a PID control loop whose setpoint is constrained by a trapezoid
 * profile. Users should call reset() when they first start running the
 * controller to avoid unwanted behavior.
 */
@SuppressWarnings("PMD.TooManyMethods")
public class ProfiledPIDController implements Sendable {
	private static int instances;

	private HyperPIDController controller;
	private TrapezoidProfile.State goal = new TrapezoidProfile.State(), setpoint = new TrapezoidProfile.State();
	private TrapezoidProfile.Constraints constraints;

	/**
	 * Allocates a {@link ProfiledPIDController} with an already initialized
	 * {@link HyperPIDController PIDController}.
	 * 
	 * @param controller
	 * @param constraints
	 */
	public ProfiledPIDController(HyperPIDController controller, TrapezoidProfile.Constraints constraints) {
		this.controller = controller;
		this.constraints = constraints;
		instances++;
		HAL.report(tResourceType.kResourceType_ProfiledPIDController, instances);
	}

	/**
	 * Allocates a {@link ProfiledPIDController} with the given constants for Kp,
	 * Ki, and Kd.
	 *
	 * @param name          The pid loop's name.
	 * @param subsystemName The subsystem's name.
	 * @param Kp            The proportional coefficient.
	 * @param Ki            The integral coefficient.
	 * @param Kd            The derivative coefficient.
	 * @param constraints   Velocity and acceleration constraints for goal.
	 */
	@SuppressWarnings("ParameterName")
	public ProfiledPIDController(String name, String subsystemName, double Kp, double Ki, double Kd,
			TrapezoidProfile.Constraints constraints) {
		this(name, subsystemName, Kp, Ki, Kd, constraints, 0.01);
	}

	/**
	 * Allocates a ProfiledPIDController with the given constants for Kp, Ki, and
	 * Kd.
	 *
	 * @param Kp          The proportional coefficient.
	 * @param Ki          The integral coefficient.
	 * @param Kd          The derivative coefficient.
	 * @param constraints Velocity and acceleration constraints for goal.
	 * @param period      The period between controller updates in seconds. The
	 *                    default is 0.02 seconds.
	 */
	@SuppressWarnings("ParameterName")
	public ProfiledPIDController(String name, String subsystemName, double Kp, double Ki, double Kd,
			TrapezoidProfile.Constraints constraints, double period) {
		controller = new HyperPIDController(name, subsystemName, Kp, Ki, Kd, period);
		this.constraints = constraints;
		instances++;
		HAL.report(tResourceType.kResourceType_ProfiledPIDController, instances);
	}

	/**
	 * Sets the PID Controller gain parameters.
	 *
	 * <p>
	 * Sets the proportional, integral, and differential coefficients.
	 *
	 * @param Kp Proportional coefficient
	 * @param Ki Integral coefficient
	 * @param Kd Differential coefficient
	 */
	@SuppressWarnings("ParameterName")
	public void setPID(double Kp, double Ki, double Kd) {
		controller.setPID(Kp, Ki, Kd);
	}

	/**
	 * Sets the proportional coefficient of the PID controller gain.
	 *
	 * @param Kp proportional coefficient
	 */
	@SuppressWarnings("ParameterName")
	public void setP(double Kp) {
		controller.setP(Kp);
	}

	/**
	 * Sets the integral coefficient of the PID controller gain.
	 *
	 * @param Ki integral coefficient
	 */
	@SuppressWarnings("ParameterName")
	public void setI(double Ki) {
		controller.setI(Ki);
	}

	/**
	 * Sets the differential coefficient of the PID controller gain.
	 *
	 * @param Kd differential coefficient
	 */
	@SuppressWarnings("ParameterName")
	public void setD(double Kd) {
		controller.setD(Kd);
	}

	/**
	 * Gets the proportional coefficient.
	 *
	 * @return proportional coefficient
	 */
	public double getP() {
		return controller.getP();
	}

	/**
	 * Gets the integral coefficient.
	 *
	 * @return integral coefficient
	 */
	public double getI() {
		return controller.getI();
	}

	/**
	 * Gets the differential coefficient.
	 *
	 * @return differential coefficient
	 */
	public double getD() {
		return controller.getD();
	}

	/**
	 * Gets the period of this controller.
	 *
	 * @return The period of the controller.
	 */
	public double getPeriod() {
		return controller.getPeriod();
	}

	/**
	 * Sets the goal for the ProfiledPIDController.
	 *
	 * @param goal The desired goal state.
	 */
	public void setGoal(TrapezoidProfile.State goal) {
		this.goal = goal;
	}

	/**
	 * Sets the goal for the ProfiledPIDController.
	 *
	 * @param goal The desired goal position.
	 */
	public void setGoal(double goal) {
		this.goal = new TrapezoidProfile.State(goal, 0);
	}

	/**
	 * Gets the goal for the ProfiledPIDController.
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
		return atSetpoint() && goal.equals(setpoint);
	}

	/**
	 * Set velocity and acceleration constraints for goal.
	 *
	 * @param constraints Velocity and acceleration constraints for goal.
	 */
	public void setConstraints(TrapezoidProfile.Constraints constraints) {
		this.constraints = constraints;
	}

	/**
	 * Returns the current setpoint of the ProfiledPIDController.
	 *
	 * @return The current setpoint.
	 */
	public TrapezoidProfile.State getSetpoint() {
		return setpoint;
	}

	/**
	 * Returns true if the error is within the tolerance of the error.
	 *
	 * <p>
	 * This will return false until at least one input value has been computed.
	 */
	public boolean atSetpoint() {
		return controller.atSetpoint();
	}

	/**
	 * Enables continuous input.
	 *
	 * <p>
	 * Rather then using the max and min input range as constraints, it considers
	 * them to be the same point and automatically calculates the shortest route to
	 * the setpoint.
	 *
	 * @param minimumInput The minimum value expected from the input.
	 * @param maximumInput The maximum value expected from the input.
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
	 * Sets the minimum and maximum values for the integrator.
	 *
	 * <p>
	 * When the cap is reached, the integrator value is added to the controller
	 * output rather than the integrator value times the integral gain.
	 *
	 * @param minimumIntegral The minimum value of the integrator.
	 * @param maximumIntegral The maximum value of the integrator.
	 */
	public void setIntegratorRange(double minimumIntegral, double maximumIntegral) {
		controller.setIntegratorRange(minimumIntegral, maximumIntegral);
	}

	/**
	 * Sets the error which is considered tolerable for use with atSetpoint().
	 *
	 * @param positionTolerance Position error which is tolerable.
	 */
	public void setTolerance(double positionTolerance) {
		setTolerance(positionTolerance, Double.POSITIVE_INFINITY);
	}

	/**
	 * Sets the error which is considered tolerable for use with atSetpoint().
	 *
	 * @param positionTolerance Position error which is tolerable.
	 * @param velocityTolerance Velocity error which is tolerable.
	 */
	public void setTolerance(double positionTolerance, double velocityTolerance) {
		controller.setTolerance(positionTolerance, velocityTolerance);
	}

	/**
	 * Returns the difference between the setpoint and the measurement.
	 *
	 * @return The error.
	 */
	public double getPositionError() {
		return controller.getPositionError();
	}

	/**
	 * Returns the change in error per second.
	 */
	public double getVelocityError() {
		return controller.getVelocityError();
	}

	/**
	 * Returns the next output of the PID controller.
	 *
	 * @param measurement The current measurement of the process variable.
	 */
	public double calculate(double measurement) {
		var profile = new TrapezoidProfile(constraints, goal, setpoint);
		setpoint = profile.calculate(getPeriod());
		return controller.calculate(measurement, setpoint.position);
	}

	/**
	 * Returns the next output of the PID controller.
	 *
	 * @param measurement The current measurement of the process variable.
	 * @param goal        The new goal of the controller.
	 */
	public double calculate(double measurement, TrapezoidProfile.State goal) {
		setGoal(goal);
		return calculate(measurement);
	}

	/**
	 * Returns the next output of the PIDController.
	 *
	 * @param measurement The current measurement of the process variable.
	 * @param goal        The new goal of the controller.
	 */
	public double calculate(double measurement, double goal) {
		setGoal(goal);
		return calculate(measurement);
	}

	/**
	 * Returns the next output of the PID controller.
	 *
	 * @param measurement The current measurement of the process variable.
	 * @param goal        The new goal of the controller.
	 * @param constraints Velocity and acceleration constraints for goal.
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
		builder.setSmartDashboardType("ProfiledPIDController");
		builder.addDoubleProperty("p", this::getP, this::setP);
		builder.addDoubleProperty("i", this::getI, this::setI);
		builder.addDoubleProperty("d", this::getD, this::setD);
		builder.addDoubleProperty("goal", () -> getGoal().position, this::setGoal);
	}
}
