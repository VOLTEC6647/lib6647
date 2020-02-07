/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.lib6647.wpilib;

import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperPIDController;

import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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

	private boolean fixedValues = true;

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
	 * Allocates a {@link ProfiledPIDController} with the given constants.
	 *
	 * @param name          The name of the {@link #controller PID controller}
	 * @param subsystemName The name of the {@link SuperSubsystem} the
	 *                      {@link #controller PID controller} belongs in
	 * @param p             The {@link #controller PID controller}'s proportional
	 *                      coefficient
	 * @param i             The {@link #controller PID controller}'s integral
	 *                      coefficient
	 * @param d             The {@link #controller PID controller}'s derivative
	 *                      coefficient
	 * @param constraints   Velocity and acceleration constraints for goal
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
	 * @param constraints Velocity and acceleration constraints for goal
	 * @param period      The period between controller updates in seconds
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
	 * @param p The {@link #controller PID controller}'s proportional coefficient
	 * @param i The {@link #controller PID controller}'s integral coefficient
	 * @param d The {@link #controller PID controller}'s derivative coefficient
	 */
	public void setPID(double p, double i, double d) {
		controller.setPID(p, i, d);
	}

	/**
	 * Sets the proportional coefficient of the PID controller gain.
	 *
	 * @param p The {@link #controller PID controller}'s proportional coefficient
	 */
	public void setP(double p) {
		controller.setP(p);
	}

	/**
	 * Sets the integral coefficient of the PID controller gain.
	 *
	 * @param i The {@link #controller PID controller}'s integral coefficient
	 */
	public void setI(double i) {
		controller.setI(i);
	}

	/**
	 * Sets the differential coefficient of the PID controller gain.
	 *
	 * @param d The {@link #controller PID controller}'s derivative coefficient
	 */
	public void setD(double d) {
		controller.setD(d);
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
	 * Gets the period of this {@link #controller PID controller}.
	 *
	 * @return The period of the {@link #controller PID controller}
	 */
	public double getPeriod() {
		return controller.getPeriod();
	}

	/**
	 * Sets the goal for the {@link ProfiledPIDController}.
	 *
	 * @param goal The desired goal state
	 */
	public void setGoal(TrapezoidProfile.State goal) {
		this.goal = goal;
	}

	/**
	 * Sets the goal for the {@link ProfiledPIDController}.
	 *
	 * @param goal The desired goal position
	 */
	public void setGoal(double goal) {
		this.goal = new TrapezoidProfile.State(goal, 0);
	}

	/**
	 * Gets the goal for the {@link ProfiledPIDController}.
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
	 * @param constraints Velocity and acceleration constraints for goal
	 */
	public void setConstraints(TrapezoidProfile.Constraints constraints) {
		this.constraints = constraints;
	}

	/**
	 * Returns the current setpoint of the {@link ProfiledPIDController}.
	 *
	 * @return The current setpoint
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
	 * @param minimumInput The minimum value expected from the input
	 * @param maximumInput The maximum value expected from the input
	 */
	public void enableContinuousInput(double minimumInput, double maximumInput) {
		controller.setInputRange(minimumInput, maximumInput);
	}

	/**
	 * Disables continuous input.
	 */
	public void disableContinuousInput() {
		controller.disableContinuousInput();
	}

	/**
	 * Sets the {@link #controller PID controller}'s minimum and maximum output.
	 * 
	 * @param outputMin
	 * @param outputMax
	 */
	public void setOutputRange(double outputMin, double outputMax) {
		controller.setOutputRange(outputMin, outputMax);
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
	 * Returns the difference between the setpoint and the measurement.
	 *
	 * @return The {@link #controller PID controller}'s error
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
	 * @param measurement The current measurement of the process variable
	 */
	public double calculate(double measurement) {
		var profile = new TrapezoidProfile(constraints, goal, setpoint);
		setpoint = profile.calculate(getPeriod());
		return controller.calculate(measurement, setpoint.position);
	}

	/**
	 * Returns the next output of the PID controller.
	 *
	 * @param measurement The current measurement of the process variable
	 * @param goal        The new goal of the controller
	 */
	public double calculate(double measurement, TrapezoidProfile.State goal) {
		setGoal(goal);
		return calculate(measurement);
	}

	/**
	 * Returns the next output of the PIDController.
	 *
	 * @param measurement The current measurement of the process variable
	 * @param goal        The new goal of the controller
	 */
	public double calculate(double measurement, double goal) {
		setGoal(goal);
		return calculate(measurement);
	}

	/**
	 * Returns the next output of the PID controller.
	 *
	 * @param measurement The current measurement of the process variable
	 * @param goal        The new goal of the controller
	 * @param constraints Velocity and acceleration constraints for goal
	 */
	public double calculate(double measurement, TrapezoidProfile.State goal, TrapezoidProfile.Constraints constraints) {
		setConstraints(constraints);
		return calculate(measurement, goal);
	}

	/**
	 * Reset the previous error and the integral term.
	 *
	 * @param measurement The current measured {@link TrapezoidProfile.State} of the
	 *                    system
	 */
	public void reset(TrapezoidProfile.State measurement) {
		controller.reset();
		setpoint = measurement;
	}

	/**
	 * Reset the previous error and the integral term.
	 *
	 * @param measuredPosition The current measured position of the system
	 * @param measuredVelocity The current measured velocity of the system
	 */
	public void reset(double measuredPosition, double measuredVelocity) {
		reset(new TrapezoidProfile.State(measuredPosition, measuredVelocity));
	}

	/**
	 * Reset the previous error and the integral term.
	 *
	 * @param measuredPosition The current measured position of the system. The
	 *                         velocity is assumed to be zero
	 */
	public void reset(double measuredPosition) {
		reset(measuredPosition, 0.0);
	}

	/**
	 * Method that enables the changing of PID values from the shuffleboard.
	 */
	public void outputPIDValues() {
		controller.outputPIDValues();

		SmartDashboard.putNumber(controller.getSubsystemName() + "_" + controller.getName() + "_MaxVelocity",
				constraints.maxVelocity);
		SmartDashboard.putNumber(controller.getSubsystemName() + "_" + controller.getName() + "_MaxAcceleration",
				constraints.maxAcceleration);
		fixedValues = false;
	}

	/**
	 * Method to update current PID values from the ones found in the Shuffleboard.
	 * 
	 * <p>
	 * The {@link #outputPIDValues()} method must first be called in order to
	 * activate the changing of these values; whether or not to update PID values
	 * can be specified in the {@link SuperSubsystem#robotMap JSON file}.
	 */
	public void updatePIDValues() {
		controller.updatePIDValues();

		if (!fixedValues) {
			try {
				setConstraints(new TrapezoidProfile.Constraints(
						SmartDashboard.getNumber(
								controller.getSubsystemName() + "_" + controller.getName() + "_MaxVelocity",
								constraints.maxVelocity),
						SmartDashboard.getNumber(
								controller.getSubsystemName() + "_" + controller.getName() + "_MaxAcceleration",
								constraints.maxAcceleration)));
			} catch (NumberFormatException e) {
				DriverStation.reportError("[!] ERROR WHILE UPDATING PROFILE CONSTRAINTS OF PROFILED PID CONTROLLER '"
						+ controller.getName().toUpperCase() + "' IN SUBSYSTEM '"
						+ controller.getSubsystemName().toUpperCase()
						+ "', ENSURE CURRENT VALUES IN SHUFFLEBOARD ARE OF TYPE 'DOUBLE':\n\t"
						+ e.getLocalizedMessage(), false);
				System.out.println("[!] ERROR WHILE UPDATING PROFILE CONSTRAINTS OF PROFILED PID CONTROLLER '"
						+ controller.getName().toUpperCase() + "' IN SUBSYSTEM '"
						+ controller.getSubsystemName().toUpperCase()
						+ "', ENSURE CURRENT VALUES IN SHUFFLEBOARD ARE OF TYPE 'DOUBLE':\n\t"
						+ e.getLocalizedMessage());
			}
		}
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
