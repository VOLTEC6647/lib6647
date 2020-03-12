package org.usfirst.lib6647.control;

import java.util.function.BiConsumer;

import edu.wpi.first.wpiutil.math.MathUtil;

/**
 * Helper class to implement "Cheesy Drive". "Cheesy Drive" simply means that
 * the "turning" stick controls the curvature of the robot's path rather than
 * its rate of heading change. This helps make the robot more controllable at
 * high speeds. Also handles the robot's quick turn functionality - "quick turn"
 * overrides constant-curvature turning for turn-in-place maneuvers.
 * <p>
 * Originally copied over from:
 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/lib/util/CheesyDriveHelper.java.
 */
public class CheesyDrive {

	// TODO: Comment properly, and fix output.

	// These factor determine how fast the wheel traverses the "non linear" sine
	// curve.
	private double highWheelNonLinearity = 0.01, lowWheelNonLinearity = 0.5;

	private double highNegInertiaScalar = 0.0;

	private double lowNegInertiaThreshold = 0.65, lowNegInertiaTurnScalar = 3.0, lowNegInertiaCloseScalar = 3.0,
			lowNegInertiaFarScalar = 4.0;

	private double highSensitivity = 0.6;
	private double lowSensitivity = 0.625;

	private double wheelQuickTurnScalar = .65;

	private double quickStopDeadband = 0.5, quickStopWeight = 0.125, quickStopScalar = 2.8;

	private double oldForward = 0.0, quickStopAccum = 0.0, negInertiaAccum = 0.0;

	public CheesyDrive() {
	}

	public void cheesyDrive(double forward, double rotation, boolean quickTurn, boolean highGear,
			BiConsumer<Double, Double> output) {
		var negInertia = forward - oldForward;
		oldForward = forward;

		double wheelNonLinearity;
		if (highGear) {
			wheelNonLinearity = highWheelNonLinearity;

			var denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);

			forward = Math.sin(Math.PI / 2.0 * wheelNonLinearity * forward) / denominator;
			forward = Math.sin(Math.PI / 2.0 * wheelNonLinearity * forward) / denominator;
		} else {
			wheelNonLinearity = lowWheelNonLinearity;

			var denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);

			forward = Math.sin(Math.PI / 2.0 * wheelNonLinearity * forward) / denominator;
			forward = Math.sin(Math.PI / 2.0 * wheelNonLinearity * forward) / denominator;
			forward = Math.sin(Math.PI / 2.0 * wheelNonLinearity * forward) / denominator;
		}

		double leftPwm, rightPwm, overpower;
		double sensitivity;

		double angularPower, linearPower;

		// Negative inertia!
		double negInertiaScalar;

		negInertiaScalar = highGear ? highNegInertiaScalar
				: forward * negInertia > 0 ? lowNegInertiaTurnScalar
						: Math.abs(forward) > lowNegInertiaThreshold ? lowNegInertiaFarScalar
								: lowNegInertiaCloseScalar;
		sensitivity = highGear ? highSensitivity : lowSensitivity;

		var negInertiaPower = negInertia * negInertiaScalar;
		negInertiaAccum += negInertiaPower;

		forward += negInertiaAccum;
		negInertiaAccum = negInertiaAccum > 1 ? negInertiaAccum - 1 : negInertiaAccum < -1 ? negInertiaAccum + 1 : 0;
		linearPower = rotation;

		// Quick turn!
		if (quickTurn) {
			if (Math.abs(linearPower) < quickStopDeadband) {
				var alpha = quickStopWeight;
				quickStopAccum = (1 - alpha) * quickStopAccum
						+ alpha * MathUtil.clamp(forward, -1, 1) * quickStopScalar;
			}
			overpower = 1.0;
			angularPower = forward * wheelQuickTurnScalar;
		} else {
			overpower = 0.0;
			angularPower = Math.abs(rotation) * forward * sensitivity - quickStopAccum;
			quickStopAccum = quickStopAccum > 1 ? quickStopAccum - 1 : quickStopAccum < -1 ? quickStopAccum + 1 : 0.0;
		}

		rightPwm = leftPwm = linearPower;
		leftPwm += angularPower;
		rightPwm -= angularPower;

		if (leftPwm > 1.0) {
			rightPwm -= overpower * (leftPwm - 1.0);
			leftPwm = 1.0;
		} else if (rightPwm > 1.0) {
			leftPwm -= overpower * (rightPwm - 1.0);
			rightPwm = 1.0;
		} else if (leftPwm < -1.0) {
			rightPwm += overpower * (-1.0 - leftPwm);
			leftPwm = -1.0;
		} else if (rightPwm < -1.0) {
			leftPwm += overpower * (-1.0 - rightPwm);
			rightPwm = -1.0;
		}

		output.accept(leftPwm, rightPwm);
	}
}