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

	/** Speed to be added to {@link HyperVictor}. */
	private double added = 0;

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
	 * Sets added {@link HyperVictor} speed.
	 * 
	 * @param speed
	 */
	public void add(double speed) {
		this.added += speed;
	}

	/**
	 * Get added value.
	 * 
	 * @return added
	 */
	public double getAdd() {
		return added;
	}

	/**
	 * Reset added {@link HyperTalon} speed.
	 * @NOTE: only use on robot init.
	 */
	public void resetAdd() {
		added = 0;
	}

	/**
	 * Sets {@link HyperVictor} to a given speed, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed
	 */
	@Override
	public void set(double speed) {
		set(speed, false);
	}

	/**
	 * Sets {@link HyperVictor} to a given speed, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed
	 * @param limited
	 */
	public void set(double speed, boolean limited) {
		super.set(ControlMode.PercentOutput, (speed >= limiter && limited ? limiter : speed) + added);
	}

	/**
	 * Sets {@link HyperVictor} to a given amount multiplied by {@link #limiter}, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed
	 */
	public void setWithRamp(double speed) {
		super.set(ControlMode.PercentOutput, speed * limiter + added);
	}
}