package org.usfirst.lib6647.subsystem.hypercomponents;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * HyperComponent for {@link WPI_TalonSRX}.
 */
public class HyperTalon extends WPI_TalonSRX {

	/**
	 * Limits how fast the Talon can go as a percentage if using
	 * {@link #setTalonWithRamp(double)} or {@link #setTalon(double, boolean)} if
	 * true.
	 */
	private double limiter = 1;

	/** Speed to be added to {@link HyperTalon}. */
	private double added = 0;

	/**
	 * Wrapper for {@link WPI_TalonSRX}.
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
	 * @return
	 */
	public double getLimiter() {
		return limiter;
	}

	/**
	 * Sets {@link #limiter} for {@link WPI_TalonSRX} speed.
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
	 * Sets added {@link HyperTalon} speed.
	 * 
	 * @param speed
	 */
	public void add(double speed) {
		this.added += speed;
	}

	/**
	 * Sets {@link HyperTalon} to a given speed, in {@link ControlMode#PercentOutput
	 * PercentOutput}.
	 * 
	 * @param speed
	 */
	@Override
	public void set(double speed) {
		set(speed, false);
	}

	/**
	 * Sets {@link HyperTalon} to a given speed, in {@link ControlMode#PercentOutput
	 * PercentOutput}.
	 * 
	 * @param speed
	 * @param limited
	 */
	public void set(double speed, boolean limited) {
		super.set(ControlMode.PercentOutput, (speed >= limiter && limited ? limiter : speed) + added);
	}

	/**
	 * Sets {@link HyperTalon} to a given amount multiplied by {@link #limiter}, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed
	 */
	public void setWithRamp(double speed) {
		super.set(ControlMode.PercentOutput, speed * limiter + added);
	}
}