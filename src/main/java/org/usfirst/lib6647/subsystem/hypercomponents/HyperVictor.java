package org.usfirst.lib6647.subsystem.hypercomponents;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * HyperComponent for {@link WPI_VictorSPX}.
 */
public class HyperVictor extends WPI_VictorSPX {

	/**
	 * Limits how fast the Victor can go as a percentage if using
	 * {@link #setVictorWithRamp(double)} or {@link #setVictor(double, boolean)} if
	 * true.
	 */
	private double limiter = 1;

	/**
	 * Wrapper for {@link WPI_VictorSPX}.
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
	 * @return
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
	public void setVictor(double speed) {
		setVictor(speed, false);
	}

	/**
	 * Sets {@link HyperVictor} to a given speed, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed
	 * @param limited
	 */
	public void setVictor(double speed, boolean limited) {
		if (speed >= limiter && limited)
			super.set(ControlMode.PercentOutput, limiter);
		else
			super.set(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets {@link HyperVictor} to a given amount multiplied by {@link #limiter}, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed
	 */
	public void setVictorWithRamp(double speed) {
		super.set(ControlMode.PercentOutput, speed * limiter);
	}

	/**
	 * Stops {@link HyperVictor} dead in its tracks.
	 */
	public void stopVictor() {
		setVictor(0, false);
	}
}