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

	/** Stores last set speed. */
	private double lastSpeed = Double.NaN;
	/** Stores last set {@link ControlMode}. */
	protected ControlMode lastMode = null;

	/**
	 * HyperComponent Wrapper for {@link WPI_TalonSRX}.
	 * 
	 * @param port
	 */
	public HyperTalon(int port) {
		super(port);

		configFactoryDefault();
	}

	/**
	 * Returns {@link #limiter} value for {@link WPI_TalonSRX} speed.
	 * 
	 * @return limiter
	 */
	public double getLimiter() {
		return limiter;
	}

	/**
	 * Sets {@link #limiter} for {@link WPI_TalonSRX} speed.
	 * 
	 * @param limiter
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
	 * Sets {@link HyperTalon} to a given speed, in {@link ControlMode#PercentOutput
	 * PercentOutput}.
	 * 
	 * @param speed
	 */
	@Override
	public void set(double speed) {
		lazySet(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets {@link HyperTalon} to a given speed, with the given {@link ControlMode}.
	 * 
	 * @param mode
	 * @param speed
	 */
	@Override
	public void set(ControlMode mode, double speed) {
		lazySet(mode, speed);
	}

	/**
	 * Sets {@link HyperTalon} to a given speed, in the given {@link ControlMode}.
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
	 * Sets {@link HyperTalon} to a given amount multiplied by {@link #limiter}, for
	 * the given {@link ControlMode}.
	 * 
	 * @param mode
	 * @param speed
	 */
	public void setWithRamp(ControlMode mode, double speed) {
		lazySet(mode, speed * limiter);
	}

	/**
	 * Sets {@link HyperTalon} to a given speed, with the given {@link ControlMode}.
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