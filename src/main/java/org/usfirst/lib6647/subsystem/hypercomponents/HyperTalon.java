package org.usfirst.lib6647.subsystem.hypercomponents;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * Simple Wrapper for a {@link WPI_TalonSRX}, adds a couple of useful features.
 */
public class HyperTalon extends WPI_TalonSRX {
	/**
	 * Limits how fast the Talon can go as a percentage if using
	 * {@link #setTalonWithRamp(double)} or {@link #setTalon(double, boolean)} if
	 * true.
	 */
	private double limiter = 1;
	/**
	 * Constant to multiply current sensor positions, in order to be relative to the
	 * Robot itself.
	 */
	private double encoderDistancePerPulse = Double.NaN;

	/** Stores last set speed. */
	private double lastSpeed = Double.NaN;
	/** Stores last set {@link ControlMode}. */
	protected ControlMode lastMode = null;

	/**
	 * HyperComponent Wrapper for {@link HyperTalon}.
	 * 
	 * @param port The {@link HyperTalon}'s port (set via CTRE's Phoenix Tuner)
	 */
	public HyperTalon(int port) {
		super(port);

		configFactoryDefault();
	}

	/**
	 * Returns {@link #limiter} value for {@link HyperTalon} speed.
	 * 
	 * @return The {@link HyperTalon}'s current limiter
	 */
	public double getLimiter() {
		return limiter;
	}

	/**
	 * Sets {@link #limiter} for {@link HyperTalon} speed.
	 * 
	 * @param limiter The amount to set the {@link #limiter} to
	 */
	public void setLimiter(double limiter) {
		this.limiter = limiter;
	}

	/**
	 * Raises {@link #limiter} by a given amount.
	 * 
	 * @param amount The amount to raise the {@link #limiter} by
	 */
	public void raiseLimiter(double amount) {
		limiter += amount;
	}

	/**
	 * Sets {@link HyperTalon} to a given speed, in {@link ControlMode#PercentOutput
	 * PercentOutput}.
	 * 
	 * @param speed The speed at which to set this {@link HyperTalon}
	 */
	@Override
	public void set(double speed) {
		set(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets {@link HyperTalon} to a given speed, with the given {@link ControlMode}.
	 * 
	 * @param mode  The mode at which to set this {@link HyperTalon}
	 * @param speed The speed at which to set this {@link HyperTalon}
	 */
	@Override
	public void set(ControlMode mode, double speed) {
		set(mode, speed, false);
	}

	/**
	 * Sets {@link HyperTalon} to a given speed, in the given {@link ControlMode}.
	 * Also sets whether to limit the set value or not.
	 * 
	 * @param mode    The mode at which to set this {@link HyperTalon}
	 * @param speed   The speed at which to set this {@link HyperTalon}
	 * @param limited Whether or not to limit the given speed
	 */
	public void set(ControlMode mode, double speed, boolean limited) {
		lazySet(mode, speed >= limiter && limited ? limiter : speed);
	}

	/**
	 * Sets {@link HyperTalon} to a given amount multiplied by {@link #limiter}, for
	 * the given {@link ControlMode}.
	 * 
	 * @param mode  The mode at which to set this {@link HyperTalon}
	 * @param speed The speed at which to set this {@link HyperTalon}
	 */
	public void setWithRamp(ControlMode mode, double speed) {
		lazySet(mode, speed * limiter);
	}

	/**
	 * Sets {@link HyperTalon} to a given speed, with the given {@link ControlMode}.
	 * Copied over from:
	 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/lib/drivers/LazyTalonSRX.java
	 * 
	 * @param mode  The mode at which to set this {@link HyperTalon}
	 * @param speed The speed at which to set this {@link HyperTalon}
	 */
	private void lazySet(ControlMode mode, double speed) {
		if (speed != lastSpeed || mode != lastMode) {
			lastSpeed = speed;
			lastMode = mode;
			super.set(mode, speed);
		}
	}

	/**
	 * Syntactic sugar for resetting the {@link HyperTalon}'s encoder.
	 */
	public void reset() {
		reset(0);
	}

	/**
	 * Syntactic sugar for resetting the {@link HyperTalon}'s encoder.
	 * 
	 * @param pidIdx The sensor slot to read
	 */
	public void reset(int pidIdx) {
		setSelectedSensorPosition(pidIdx);
	}

	/**
	 * Set {@link #encoderDistancePerPulse encoder constant} for this
	 * {@link HyperTalon}.
	 */
	public void setEncoderDistancePerPulse(double encoderDistancePerPulse) {
		this.encoderDistancePerPulse = encoderDistancePerPulse;
	}

	/**
	 * Syntactic sugar for getting the {@link HyperTalon}'s encoder's current
	 * position.
	 * 
	 * @return The distance traveled by the encoder, in units relative to the robot
	 */
	public double getEncoderDistance() {
		return getEncoderDistance(0);
	}

	/**
	 * Syntactic sugar for getting the {@link HyperTalon}'s encoder's current
	 * position.
	 * 
	 * @param pidIdx The sensor slot to read
	 * @return The distance traveled by the encoder, in units relative to the robot
	 */
	public double getEncoderDistance(int pidIdx) {
		return getSelectedSensorPosition(pidIdx) * encoderDistancePerPulse;
	}

	/**
	 * Syntactic sugar for getting the {@link HyperTalon}'s encoder's current rate.
	 * 
	 * @return The rate at which the encoder changes, in units relative to the robot
	 */
	public double getEncoderRate() {
		return getEncoderRate(0);
	}

	/**
	 * Syntactic sugar for getting the {@link HyperTalon}'s encoder's current rate.
	 * 
	 * @param pidIdx The sensor slot to load
	 * @return The rate at which the encoder changes, in units relative to the robot
	 */
	public double getEncoderRate(int pidIdx) {
		return getSelectedSensorVelocity() * encoderDistancePerPulse * 10;
	}
}