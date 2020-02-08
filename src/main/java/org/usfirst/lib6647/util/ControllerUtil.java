/*----------------------------------------------------------------------------*/
/* Copyright (c) 2020 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.lib6647.util;

import org.usfirst.lib6647.wpilib.PIDController;

/**
 * This class helps enable continuous output of {@link PIDController PID
 * controllers}.
 * 
 * <p>
 * Copied over from:
 * https://github.com/wpilibsuite/allwpilib/blob/7b6838c80880d52e759d3017b0f1c16cffa3e2ce/wpilibj/src/main/java/edu/wpi/first/wpilibj/controller/ControllerUtil.java
 */
public final class ControllerUtil {
	/**
	 * Returns modulus of error where error is the difference between the reference
	 * and a measurement.
	 *
	 * <p>
	 * This implements modular subtraction defined as:
	 *
	 * <p>
	 * e = (r mod m - x mod m) mod m
	 *
	 * <p>
	 * with an offset in the modulus range for minimum input.
	 *
	 * @param reference    Reference input of a controller.
	 * @param measurement  The current measurement.
	 * @param minimumInput The minimum value expected from the input.
	 * @param maximumInput The maximum value expected from the input.
	 */
	public static double getModulusError(double reference, double measurement, double minimumInput,
			double maximumInput) {
		double modulus = maximumInput - minimumInput;
		double error = reference % modulus - measurement % modulus;

		// Moduli on the difference arguments establish a precondition for the
		// following modulus.
		return (error - minimumInput) % modulus + minimumInput;
	}

	private ControllerUtil() {
	}
}