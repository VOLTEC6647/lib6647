package org.usfirst.lib6647.subsystem.hypercomponents;

import org.usfirst.lib6647.subsystem.SuperSubsystem;

import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpiutil.math.MathUtil;

/**
 * Simple wrapper for the {@link PIDController} that re-implements a couple of
 * useful features (such as output range) that were removed in the 2020 update.
 */
public class HyperPIDController extends PIDController {
	/** Names of both the PID loop, and its subsystem. */
	private final String name, subsystemName;
	/** Minimum and maximum output values for the {@link PIDController}. */
	private double outputMin = 0.0, outputMax = 0.0;
	/** Whether or not the current PID values can be updated in the Shuffleboard. */
	private boolean fixedValues = true;

	/**
	 * HyperComponent Wrapper for a {@link PIDController}.
	 * 
	 * @param name
	 * @param subsystemName
	 * @param p
	 * @param i
	 * @param d
	 * @param period
	 */
	public HyperPIDController(String name, String subsystemName, double p, double i, double d, double period) {
		super(p, i, d, period);

		this.name = name;
		this.subsystemName = subsystemName;
	}

	@Override
	public double calculate(double measurement) {
		// 'Clamps' the calculated value to the declared minimum and maximum output.
		return MathUtil.clamp(super.calculate(measurement), outputMin, outputMax);
	}

	/**
	 * Method that enables the changing of PID values from the shuffleboard.
	 */
	public void outputPIDValues() {
		SmartDashboard.putString(subsystemName + "_" + name + "P", getP() + "");
		SmartDashboard.putString(subsystemName + "_" + name + "I", getI() + "");
		SmartDashboard.putString(subsystemName + "_" + name + "D", getD() + "");

		fixedValues = false;
	}

	/**
	 * Method to update current PID values from the ones found in the Shuffleboard.
	 * The {@link HyperPIDController#outputPIDValues()} method must first be called
	 * in order to activate the changing of these values; whether or not to update
	 * PID values can be specified in the {@link SuperSubsystem#robotMap JSON file}.
	 */
	public void updatePIDValues() {
		if (fixedValues)
			return;

		if (!SmartDashboard.getString(subsystemName + "_" + name + "P", getP() + "").equals(getP() + ""))
			setP(Double.parseDouble(SmartDashboard.getString(subsystemName + "_" + name + "P", getP() + "")));
		if (!SmartDashboard.getString(subsystemName + "_" + name + "I", getI() + "").equals(getI() + ""))
			setI(Double.parseDouble(SmartDashboard.getString(subsystemName + "_" + name + "I", getI() + "")));
		if (!SmartDashboard.getString(subsystemName + "_" + name + "D", getD() + "").equals(getD() + ""))
			setD(Double.parseDouble(SmartDashboard.getString(subsystemName + "_" + name + "D", getD() + "")));
	}

	/**
	 * Syntactic sugar; basically the same as
	 * {@link PIDController#enableContinuousInput(double, double)}.
	 * 
	 * @param inputMin
	 * @param inputMax
	 */
	public void setInputRange(double inputMin, double inputMax) {
		enableContinuousInput(inputMin, inputMax);
	}

	/**
	 * Sets the {@link HyperPIDController}'s minimum and maximum output.
	 * 
	 * @param outputMin
	 * @param outputMax
	 */
	public void setOutputRange(double outputMin, double outputMax) {
		this.outputMin = outputMin;
		this.outputMax = outputMax;
	}
}