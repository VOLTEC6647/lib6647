package org.usfirst.lib6647.subsystem.hypercomponents;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * Simple Wrapper for a {@link WPI_VictorSPX}, adds a couple of useful features.
 */
public class HyperVictor extends WPI_VictorSPX {
	/**
	 * Limits how fast the Victor can go as a percentage if using
	 * {@link #setVictorWithRamp(double)} or {@link #setVictor(double, boolean)} if
	 * true.
	 */
	private double limiter = 1;

	/** Stores last set speed. */
	private double lastSpeed = Double.NaN;
	/** Stores last set {@link ControlMode}. */
	protected ControlMode lastMode = null;

	/**
	 * HyperComponent Wrapper for {@link WPI_VictorSPX}.
	 * 
	 * @param port The {@link HyperVictor}'s port (set via CTRE's Phoenix Tuner)
	 */
	public HyperVictor(int port) {
		super(port);

		configFactoryDefault();
	}

	/**
	 * Returns {@link #limiter} value for {@link WPI_VictorSPX} speed.
	 * 
	 * @return limiter The {@link HyperVictor}'s current limiter
	 */
	public double getLimiter() {
		return limiter;
	}

	/**
	 * Sets {@link #limiter limiter} for {@link WPI_VictorSPX} speed.
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
	 * Sets {@link HyperVictor} to a given speed, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed The speed at which to set this
	 */
	@Override
	public void set(double speed) {
		set(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets {@link HyperVictor} to a given speed, in the given {@link ControlMode}.
	 * 
	 * @param mode  The mode at which to set this {@link HyperVictor}
	 * @param speed The speed at which to set this {@link HyperVictor}
	 */
	@Override
	public void set(ControlMode mode, double speed) {
		set(mode, speed, false);
	}

	/**
	 * Sets {@link HyperVictor} to a given speed, in the given {@link ControlMode}.
	 * Also sets whether to limit the set value or not.
	 * 
	 * @param mode    The mode at which to set this {@link HyperVictor}
	 * @param speed   The speed at which to set this {@link HyperVictor}
	 * @param limited Whether or not to limit the given speed
	 */
	public void set(ControlMode mode, double speed, boolean limited) {
		lazySet(mode, speed >= limiter && limited ? limiter : speed);
	}

	/**
	 * Sets {@link HyperVictor} to a given amount multiplied by {@link #limiter}, in
	 * the given {@link ControlMode}.
	 * 
	 * @param mode  The mode at which to set this {@link HyperVictor}
	 * @param speed The speed at which to set this {@link HyperVictor}
	 */
	public void setWithRamp(ControlMode mode, double speed) {
		lazySet(mode, speed * limiter);
	}

	/**
	 * Sets {@link HyperVictor} to a given speed, in the given {@link ControlMode}.
	 * 
	 * <p>
	 * Copied over from:
	 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/lib/drivers/LazyTalonSRX.java
	 * 
	 * @param mode  The mode at which to set this {@link HyperVictor}
	 * @param speed The speed at which to set this {@link HyperVictor}
	 */
	private void lazySet(ControlMode mode, double speed) {
		if (speed != lastSpeed || mode != lastMode) {
			lastSpeed = speed;
			lastMode = mode;
			super.set(mode, speed);
		}
	}
}