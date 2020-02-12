/*----------------------------------------------------------------------------*/
/* Copyright (c) 2019 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.lib6647.wpilib.trajectory;

import java.util.Objects;

import org.usfirst.lib6647.util.ControllerUtil;

import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;

/**
 * A trapezoid-shaped velocity profile.
 *
 * <p>
 * While this class can be used for a profiled movement from start to finish,
 * the intended usage is to filter a reference's dynamics based on trapezoidal
 * velocity constraints. To compute the reference obeying this constraint, do
 * the following.
 *
 * <p>
 * Initialization:
 * 
 * <pre>
 * <code>
 * TrapezoidProfile.Constraints constraints =
 *   new TrapezoidProfile.Constraints(kMaxV, kMaxA);
 * TrapezoidProfile.State previousProfiledReference =
 *   new TrapezoidProfile.State(initialReference, 0.0);
 * </code>
 * </pre>
 *
 * <p>
 * Run on update:
 * 
 * <pre>
 * <code>
 * TrapezoidProfile profile =
 *   new TrapezoidProfile(constraints, unprofiledReference, previousProfiledReference);
 * previousProfiledReference = profile.calculate(timeSincePreviousUpdate);
 * </code>
 * </pre>
 *
 * <p>
 * where `unprofiledReference` is free to change between calls. Note that when
 * the unprofiled reference is within the constraints, `calculate()` returns the
 * unprofiled reference unchanged.
 *
 * <p>
 * Otherwise, a timer can be started to provide monotonic values for
 * `calculate()` and to determine when the profile has completed via
 * `isFinished()`.
 */
public class TrapezoidProfile {
	// The direction of the profile, either 1 for forwards or -1 for inverted
	private int direction;

	private Constraints constraints;
	private State initial, goal;

	private double endAccel, endFullSpeed, endDeccel;

	public static class Constraints {
		public double maxVelocity, maxAcceleration;

		public Constraints() {
			HAL.report(tResourceType.kResourceType_TrapezoidProfile, 1);
		}

		/**
		 * Construct {@link Constraints} for a {@link TrapezoidProfile profile}.
		 *
		 * @param maxVelocity     maximum velocity
		 * @param maxAcceleration maximum acceleration
		 */
		public Constraints(double maxVelocity, double maxAcceleration) {
			this.maxVelocity = maxVelocity;
			this.maxAcceleration = maxAcceleration;
			HAL.report(tResourceType.kResourceType_TrapezoidProfile, 1);
		}
	}

	public static class State {
		public double position, velocity;

		public State() {
		}

		public State(double position, double velocity) {
			this.position = position;
			this.velocity = velocity;
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof State) {
				State rhs = (State) other;
				return this.position == rhs.position && this.velocity == rhs.velocity;
			} else {
				return false;
			}
		}

		@Override
		public int hashCode() {
			return Objects.hash(position, velocity);
		}
	}

	/**
	 * Construct a {@link TrapezoidProfile profile}.
	 *
	 * @param constraints The constraints on the {@link TrapezoidProfile profile},
	 *                    like maximum velocity
	 * @param goal        The desired {@link State} when the {@link TrapezoidProfile
	 *                    profile} is complete
	 * @param initial     The initial {@link State} (usually the current state)
	 * @param continuous  Whether or not the {@link TrapezoidProfile} wraps around
	 *                    (e.g. for a gyro)
	 * @param error       The error calculated by {@link ControllerUtil}
	 */
	public TrapezoidProfile(Constraints constraints, State goal, State initial, boolean continuous, double error) {
		direction = (continuous ? error < 0 : initial.position > goal.position) ? -1 : 1;
		this.constraints = constraints;
		this.initial = direct(initial);
		this.goal = direct(goal);

		if (initial.velocity > constraints.maxVelocity)
			initial.velocity = constraints.maxVelocity;

		// Deal with a possibly truncated motion profile (with nonzero initial or
		// final velocity) by calculating the parameters as if the profile began and
		// ended at zero velocity
		var cutoffBegin = initial.velocity / constraints.maxAcceleration;
		var cutoffDistBegin = cutoffBegin * cutoffBegin * constraints.maxAcceleration / 2.0;

		var cutoffEnd = goal.velocity / constraints.maxAcceleration;
		var cutoffDistEnd = cutoffEnd * cutoffEnd * constraints.maxAcceleration / 2.0;

		// Now we can calculate the parameters as if it was a full trapezoid instead
		// of a truncated one

		var fullTrapezoidDist = cutoffDistBegin + (goal.position - initial.position) + cutoffDistEnd;
		var accelerationTime = constraints.maxVelocity / constraints.maxAcceleration;

		var fullSpeedDist = fullTrapezoidDist - accelerationTime * accelerationTime * constraints.maxAcceleration;

		// Handle the case where the profile never reaches full speed
		if (fullSpeedDist < 0) {
			accelerationTime = Math.sqrt(fullTrapezoidDist / constraints.maxAcceleration);
			fullSpeedDist = 0;
		}

		endAccel = accelerationTime - cutoffBegin;
		endFullSpeed = endAccel + fullSpeedDist / constraints.maxVelocity;
		endDeccel = endFullSpeed + accelerationTime - cutoffEnd;
	}

	/**
	 * Construct a {@link TrapezoidProfile profile}.
	 *
	 * @param constraints The constraints on the {@link TrapezoidProfile profile},
	 *                    like maximum velocity
	 * @param goal        The desired {@link State} when the {@link TrapezoidProfile
	 *                    profile} is complete
	 * @param initial     The initial {@link State} (usually the current state)
	 */
	public TrapezoidProfile(Constraints constraints, State goal, State initial) {
		this(constraints, goal, initial, false, Double.NaN);
	}

	/**
	 * Construct a {@link TrapezoidProfile}.
	 *
	 * @param constraints The constraints on the {@link TrapezoidProfile profile},
	 *                    like maximum velocity
	 * @param goal        The desired state when the {@link TrapezoidProfile
	 *                    profile} is complete
	 */
	public TrapezoidProfile(Constraints constraints, State goal) {
		this(constraints, goal, new State(0, 0));
	}

	/**
	 * Calculate the correct position and velocity for the {@link TrapezoidProfile
	 * profile} at a time t where the beginning of the profile was at time t = 0.
	 *
	 * @param t The time since the beginning of the {@link TrapezoidProfile profile}
	 * @return This {@link TrapezoidProfile}'s calculated output
	 */
	public State calculate(double t) {
		var result = initial;

		if (t < endAccel) {
			result.velocity += t * constraints.maxAcceleration;
			result.position += (initial.velocity + t * constraints.maxAcceleration / 2.0) * t;
		} else if (t < endFullSpeed) {
			result.velocity = constraints.maxVelocity;
			result.position += (initial.velocity + endAccel * constraints.maxAcceleration / 2.0) * endAccel
					+ constraints.maxVelocity * (t - endAccel);
		} else if (t <= endDeccel) {
			result.velocity = goal.velocity + (endDeccel - t) * constraints.maxAcceleration;
			var timeLeft = endDeccel - t;
			result.position = goal.position - (goal.velocity + timeLeft * constraints.maxAcceleration / 2.0) * timeLeft;
		} else
			result = goal;

		return direct(result);
	}

	/**
	 * Returns the time left until a target distance in the {@link TrapezoidProfile
	 * profile} is reached.
	 *
	 * @param target The target distance
	 * @return The calculated time until reaching a target
	 */
	public double timeLeftUntil(double target) {
		var position = initial.position * direction;
		var velocity = initial.velocity * direction;

		var endAccel = this.endAccel * direction;
		var endFullSpeed = this.endFullSpeed * direction - endAccel;

		if (target < position) {
			endAccel = -endAccel;
			endFullSpeed = -endFullSpeed;
			velocity = -velocity;
		}

		endAccel = Math.max(endAccel, 0);
		endFullSpeed = Math.max(endFullSpeed, 0);
		double endDeccel = this.endDeccel - endAccel - endFullSpeed;
		endDeccel = Math.max(endDeccel, 0);

		final var acceleration = constraints.maxAcceleration;
		final var decceleration = -constraints.maxAcceleration;

		var distToTarget = Math.abs(target - position);
		if (distToTarget < 1e-6)
			return 0;

		var accelDist = velocity * endAccel + 0.5 * acceleration * endAccel * endAccel;

		var deccelVelocity = endAccel > 0 ? Math.sqrt(Math.abs(velocity * velocity + 2 * acceleration * accelDist))
				: velocity;

		var deccelDist = deccelVelocity * endDeccel + 0.5 * decceleration * endDeccel * endDeccel;

		deccelDist = Math.max(deccelDist, 0);

		var fullSpeedDist = constraints.maxVelocity * endFullSpeed;

		if (accelDist > distToTarget) {
			accelDist = distToTarget;
			fullSpeedDist = 0;
			deccelDist = 0;
		} else if (accelDist + fullSpeedDist > distToTarget) {
			fullSpeedDist = distToTarget - accelDist;
			deccelDist = 0;
		} else
			deccelDist = distToTarget - fullSpeedDist - accelDist;

		var accelTime = (-velocity + Math.sqrt(Math.abs(velocity * velocity + 2 * acceleration * accelDist)))
				/ acceleration;

		var deccelTime = (-deccelVelocity
				+ Math.sqrt(Math.abs(deccelVelocity * deccelVelocity + 2 * decceleration * deccelDist)))
				/ decceleration;

		var fullSpeedTime = fullSpeedDist / constraints.maxVelocity;

		return accelTime + fullSpeedTime + deccelTime;
	}

	/**
	 * Get the total time the {@link TrapezoidProfile profile} takes to reach the
	 * {@link #goal}.
	 * 
	 * @return The total time this {@link TrapezoidProfile} takes to reach its
	 *         {@link #goal}
	 */
	public double totalTime() {
		return endDeccel;
	}

	/**
	 * Returns true if the {@link TrapezoidProfile profile} has reached the goal.
	 *
	 * <p>
	 * The {@link TrapezoidProfile profile} has reached the {@link #goal} if the
	 * time since the {@link TrapezoidProfile profile} started has exceeded the
	 * {@link TrapezoidProfile profile}'s total time.
	 *
	 * @param t The time since the beginning of the {@link TrapezoidProfile profile}
	 * @return Whether or not this {@link TrapezoidProfile} is finished reaching its
	 *         {@link #goal}
	 */
	public boolean isFinished(double t) {
		return t >= totalTime();
	}

	/**
	 * Flip in the sign of velocity and position in the given {@link State}, in the
	 * case that the {@link TrapezoidProfile profile} is inverted.
	 * 
	 * @param in input {@link State}
	 * @return A {@link State} with position and velocity inverted, if necessary
	 */
	private State direct(State in) {
		var result = new State(in.position, in.velocity);
		result.position = result.position * direction;
		result.velocity = result.velocity * direction;
		return result;
	}
}
