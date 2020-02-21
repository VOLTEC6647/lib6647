package org.usfirst.lib6647.subsystem.hypercomponents;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpiutil.math.MathUtil;

/**
 * Simple Wrapper for a {@link WPI_VictorSPX}, adds a couple of useful features.
 */
public class HyperVictor extends WPI_VictorSPX {
	/**
	 * Limits how fast the {@link HyperVictor} can go if using {@link #setWithRamp}
	 * or {@link #set} if true.
	 */
	private double limiter = 1;

	/** Stores last set speed and demand. */
	private double lastSpeed = Double.NaN, lastDemandSpeed = Double.NaN;
	/** Stores last set {@link ControlMode}. */
	protected ControlMode lastMode = null;
	/** Stores last set {@link DemandType}. */
	protected DemandType lastDemandType = null;

	/**
	 * HyperComponent Wrapper for {@link WPI_VictorSPX}.
	 * 
	 * @param port The {@link HyperVictor}'s port (set via CTRE's Phoenix Tuner)
	 */
	public HyperVictor(int port) {
		super(port);
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
	 * Sets the {@link HyperVictor} to the given speed, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed The speed at which to set this {@link HyperVictor} to
	 */
	@Override
	public void set(double speed) {
		set(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets the {@link HyperVictor} to the given speed and demand speed, in
	 * {@link ControlMode#PercentOutput PercentOutput} and
	 * {@ink DemandType#ArbitraryFeedForward ArbitraryFeedForward}.
	 * 
	 * @param speed       The speed at which to set this {@link HyperVictor} to
	 * @param demandSpeed The value at which to set this {@link HyperVictor}'s
	 *                    demand speed to
	 */
	public void set(double speed, double demandSpeed) {
		set(ControlMode.PercentOutput, speed, DemandType.ArbitraryFeedForward, demandSpeed);
	}

	/**
	 * Sets the {@link HyperVictor} to the given speed, for the given
	 * {@link ControlMode}.
	 * 
	 * @param mode  The mode at which to set this {@link HyperVictor} to
	 * @param speed The speed at which to set this {@link HyperVictor} to
	 */
	@Override
	public void set(ControlMode mode, double speed) {
		set(mode, speed, false);
	}

	/**
	 * Sets the {@link HyperVictor} to the given speed and demand speed, for the
	 * given {@link ControlMode} and {@link DemandType}.
	 * 
	 * @param mode        The mode at which to set this {@link HyperVictor} to
	 * @param speed       The speed at which to set this {@link HyperVictor} to
	 * @param demandType  The type of demand to set this {@link HyperVictor} to
	 * @param demandSpeed The value at which to set this {@link HyperVictor}'s
	 *                    demand speed to
	 */
	@Override
	public void set(ControlMode mode, double speed, DemandType demandType, double demandSpeed) {
		set(mode, speed, demandType, demandSpeed, false);
	}

	/**
	 * Sets the {@link HyperVictor} to the given speed, for the given
	 * {@link ControlMode}. Also sets whether to limit the set value or not.
	 * 
	 * @param mode    The mode at which to set this {@link HyperVictor}
	 * @param speed   The speed at which to set this {@link HyperVictor}
	 * @param limited Whether or not to limit the given speed
	 */
	public void set(ControlMode mode, double speed, boolean limited) {
		lazySet(mode, limited ? MathUtil.clamp(speed, -limiter, limiter) : speed);
	}

	/**
	 * Sets the {@link HyperVictor} to the given speed and demand speed, for the
	 * given {@link ControlMode} and {@link DemandType}. Also sets whether to limit
	 * both the set speed and demand speed or not.
	 * 
	 * @param mode        The mode at which to set this {@link HyperVictor} to
	 * @param speed       The speed at which to set this {@link HyperVictor} to
	 * @param demandType  The type of demand to set this {@link HyperVictor} to
	 * @param demandSpeed The value at which to set this {@link HyperVictor}'s
	 *                    demand speed to
	 * @param limited     Whether or not to limit both the given speed and demand
	 *                    speed
	 */
	public void set(ControlMode mode, double speed, DemandType demandType, double demandSpeed, boolean limited) {
		lazySet(mode, limited ? MathUtil.clamp(speed, -limiter, limiter) : speed, demandType,
				limited ? MathUtil.clamp(demandSpeed, -limiter, limiter) : demandSpeed);
	}

	/**
	 * Sets the {@link HyperVictor} to the given speed multiplied by the
	 * {@link #limiter}, in {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param mode  The mode at which to set this {@link HyperVictor} to
	 * @param speed The speed at which to set this {@link HyperVictor} to
	 */
	public void setWithRamp(double speed) {
		setWithRamp(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets the {@link HyperVictor} to the given speed multiplied by the
	 * {@link #limiter}, for the given {@link ControlMode}.
	 * 
	 * @param mode  The mode at which to set this {@link HyperVictor} to
	 * @param speed The speed at which to set this {@link HyperVictor} to
	 */
	public void setWithRamp(ControlMode mode, double speed) {
		lazySet(mode, speed * limiter);
	}

	/**
	 * Sets the {@link HyperVictor} to the given speed and demand multiplied by the
	 * {@link #limiter}, for the given {@link ControlMode} and {@link DemandType}.
	 * 
	 * @param mode        The mode at which to set this {@link HyperVictor} to
	 * @param speed       The speed at which to set this {@link HyperVictor} to
	 * @param demandType  The type of demand to set this {@link HyperVictor} to
	 * @param demandSpeed The value at which to set this {@link HyperVictor}'s
	 *                    demand speed to
	 */
	public void setWithRamp(ControlMode mode, double speed, DemandType demandType, double demandSpeed) {
		lazySet(mode, speed * limiter, demandType, demandSpeed * limiter);
	}

	/**
	 * Sets the {@link HyperVictor} to the given speed, for the given
	 * {@link ControlMode}.
	 * 
	 * <p>
	 * Copied over from:
	 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/lib/drivers/LazyTalonSRX.java
	 * 
	 * @param mode  The mode at which to set this {@link HyperVictor} to
	 * @param speed The speed at which to set this {@link HyperVictor} to
	 */
	private void lazySet(ControlMode mode, double speed) {
		if (speed != lastSpeed || mode != lastMode) {
			lastSpeed = speed;
			lastMode = mode;

			super.set(mode, speed);
		}
	}

	/**
	 * Sets the {@link HyperVictor} to the given speed and demand, for the given
	 * {@link ControlMode} and {@link DemandType}.
	 * 
	 * @param mode        The mode at which to set this {@link HyperVictor} to
	 * @param speed       The speed at which to set this {@link HyperVictor} to
	 * @param demandType  The type of demand to set this {@link HyperVictor} to
	 * @param demandSpeed The value at which to set this {@link HyperVictor}'s
	 *                    demand speed to
	 */
	private void lazySet(ControlMode mode, double speed, DemandType demandType, double demandSpeed) {
		if (speed != lastSpeed || mode != lastMode || demandType != lastDemandType || demandSpeed != lastDemandSpeed) {
			lastSpeed = speed;
			lastMode = mode;
			lastDemandType = demandType;
			lastDemandSpeed = demandSpeed;

			super.set(mode, speed, demandType, demandSpeed);
		}
	}
}