package org.usfirst.lib6647.subsystem.hypercomponents;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * HyperComponent for {@link WPI_TalonSRX}.
 */
public class HyperTalon extends WPI_TalonSRX {

	/**
	 * Limits how fast the Talon can go if using {@link #setTalonWithRamp(double)}
	 * or {@link #setTalon(double, boolean)} if true.
	 */
	private double limiter = 1;

	/**
	 * Wrapper for {@link WPI_TalonSRX}.
	 * 
	 * @param port
	 */
	public HyperTalon(int port) {
		super(port);
	}

	/**
	 * Sets {@link #limiter} for Talon speed.
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
	 * @param limited
	 */
	public void setTalon(double speed, boolean limited) {
		if (speed >= limiter && limited)
			super.set(ControlMode.PercentOutput, limiter);
		else
			super.set(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets {@link HyperTalon} to a given amount multiplied by {@link #limiter}, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed
	 */
	public void setTalonWithRamp(double speed) {
		super.set(ControlMode.PercentOutput, speed * limiter);
	}

	/**
	 * Stops {@link HyperTalon} dead in its tracks.
	 */
	public void stopTalon() {
		setTalon(0, false);
	}
}