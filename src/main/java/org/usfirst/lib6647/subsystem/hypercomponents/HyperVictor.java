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
	 * @param port
	 */
	public HyperVictor(int port) {
		super(port);

		configFactoryDefault();
	}

	/**
	 * Returns {@link #limiter} value for Victor speed.
	 * 
	 * @return limiter
	 */
	public double getLimiter() {
		return limiter;
	}

	/**
	 * Sets {@link #limiter limiter} for Victor speed.
	 */
	public void setLimiter(double limiter) {
		this.limiter = limiter;
	}

	/**
	 * Raises {@link #limiter} by a given amount.
	 * 
	 * @param amount
	 */
	public void raiseLimiter(double amount) {
		limiter += amount;
	}

	/**
	 * Sets {@link HyperVictor} to a given speed, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed
	 */
	@Override
	public void set(double speed) {
		lazySet(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets {@link HyperVictor} to a given speed, in the given {@link ControlMode}.
	 * 
	 * @param mode
	 * @param speed
	 */
	@Override
	public void set(ControlMode mode, double speed) {
		set(mode, speed, false);
	}

	/**
	 * Sets {@link HyperVictor} to a given speed, in the given {@link ControlMode}.
	 * Also sets whether to limit the set value or not.
	 * 
	 * @param mode
	 * @param speed
	 * @param limited
	 */
	public void set(ControlMode mode, double speed, boolean limited) {
		lazySet(mode, speed >= limiter && limited ? limiter : speed);
	}

	/**
	 * Sets {@link HyperVictor} to a given amount multiplied by {@link #limiter}, in
	 * the given {@link ControlMode}.
	 * 
	 * @param mode
	 * @param speed
	 */
	public void setWithRamp(ControlMode mode, double speed) {
		lazySet(mode, speed * limiter);
	}

	/**
	 * Sets {@link HyperVictor} to a given speed, in the given {@link ControlMode}.
	 * Copied over from:
	 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/lib/drivers/LazyTalonSRX.java
	 * 
	 * @param mode
	 * @param speed
	 */
	private void lazySet(ControlMode mode, double speed) {
		if (speed != lastSpeed || mode != lastMode) {
			lastSpeed = speed;
			lastMode = mode;
			super.set(mode, speed);
		}
	}
}