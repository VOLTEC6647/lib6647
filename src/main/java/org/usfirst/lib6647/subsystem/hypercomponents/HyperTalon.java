package org.usfirst.lib6647.subsystem.hypercomponents;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpiutil.math.MathUtil;

/**
 * Simple Wrapper for a {@link WPI_TalonSRX}, adds a couple of useful features.
 */
public class HyperTalon extends WPI_TalonSRX {
	/**
	 * Limits how fast the {@link HyperTalon} can go if using {@link #setWithRamp}
	 * or {@link #set} if true.
	 */
	private double limiter = 1;
	/**
	 * Constant to multiply current {@link FeedbackDevice#QuadEncoder sensor}
	 * positions, in order to be relative to the Robot itself.
	 */
	private double encoderDistancePerPulse = Double.NaN;

	/** Stores last set speed and demand. */
	private double lastSpeed = Double.NaN, lastDemandSpeed = Double.NaN;
	/** Stores last set {@link ControlMode}. */
	protected ControlMode lastMode = null;
	/** Stores last set {@link DemandType}. */
	protected DemandType lastDemandType = null;

	/**
	 * HyperComponent Wrapper for {@link WPI_TalonSRX}.
	 * 
	 * @param port The {@link HyperTalon}'s port (set via CTRE's Phoenix Tuner)
	 */
	public HyperTalon(int port) {
		super(port);
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
	 * Sets the {@link HyperTalon} to the given speed, in
	 * {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed The speed at which to set this {@link HyperTalon} to
	 */
	@Override
	public void set(double speed) {
		set(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed and demand speed, in
	 * {@link ControlMode#PercentOutput PercentOutput} and
	 * {@link DemandType#ArbitraryFeedForward ArbitraryFeedForward}.
	 * 
	 * @param speed       The speed at which to set this {@link HyperTalon} to
	 * @param demandSpeed The value at which to set this {@link HyperTalon}'s demand
	 *                    speed to
	 */
	public void set(double speed, double demandSpeed) {
		set(ControlMode.PercentOutput, speed, DemandType.ArbitraryFeedForward, demandSpeed);
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed, for the given
	 * {@link ControlMode}.
	 * 
	 * @param mode  The mode at which to set this {@link HyperTalon} to
	 * @param speed The speed at which to set this {@link HyperTalon} to
	 */
	@Override
	public void set(ControlMode mode, double speed) {
		set(mode, speed, false);
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed and demand speed, for the
	 * given {@link ControlMode} and {@link DemandType}.
	 * 
	 * @param mode        The mode at which to set this {@link HyperTalon} to
	 * @param speed       The speed at which to set this {@link HyperTalon} to
	 * @param demandType  The type of demand to set this {@link HyperTalon} to
	 * @param demandSpeed The value at which to set this {@link HyperTalon}'s demand
	 *                    speed to
	 */
	@Override
	public void set(ControlMode mode, double speed, DemandType demandType, double demandSpeed) {
		set(mode, speed, demandType, demandSpeed, false);
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed, for the given
	 * {@link ControlMode}. Also sets whether to limit the set value or not.
	 * 
	 * @param mode    The mode at which to set this {@link HyperTalon}
	 * @param speed   The speed at which to set this {@link HyperTalon}
	 * @param limited Whether or not to limit the given speed
	 */
	public void set(ControlMode mode, double speed, boolean limited) {
		lazySet(mode, limited ? MathUtil.clamp(speed, -limiter, limiter) : speed);
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed and demand speed, for the
	 * given {@link ControlMode} and {@link DemandType}. Also sets whether to limit
	 * both the set speed and demand speed or not.
	 * 
	 * @param mode        The mode at which to set this {@link HyperTalon} to
	 * @param speed       The speed at which to set this {@link HyperTalon} to
	 * @param demandType  The type of demand to set this {@link HyperTalon} to
	 * @param demandSpeed The value at which to set this {@link HyperTalon}'s demand
	 *                    speed to
	 * @param limited     Whether or not to limit both the given speed and demand
	 *                    speed
	 */
	public void set(ControlMode mode, double speed, DemandType demandType, double demandSpeed, boolean limited) {
		lazySet(mode, limited ? MathUtil.clamp(speed, -limiter, limiter) : speed, demandType,
				limited ? MathUtil.clamp(demandSpeed, -limiter, limiter) : demandSpeed);
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed multiplied by the
	 * {@link #limiter}, in {@link ControlMode#PercentOutput PercentOutput}.
	 * 
	 * @param speed The speed at which to set this {@link HyperTalon} to
	 */
	public void setWithRamp(double speed) {
		setWithRamp(ControlMode.PercentOutput, speed);
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed multiplied by the
	 * {@link #limiter}, in {@link ControlMode#PercentOutput PercentOutput} and
	 * {@link DemandType#ArbitraryFeedForward ArbitraryFeedForward}.
	 * 
	 * @param speed       The speed at which to set this {@link HyperTalon} to
	 * @param demandSpeed The value at which to set this {@link HyperTalon}'s demand
	 *                    speed to
	 */
	public void setWithRamp(double speed, double demandSpeed) {
		setWithRamp(ControlMode.PercentOutput, speed, DemandType.ArbitraryFeedForward, demandSpeed);
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed multiplied by the
	 * {@link #limiter}, for the given {@link ControlMode}.
	 * 
	 * @param mode  The mode at which to set this {@link HyperTalon} to
	 * @param speed The speed at which to set this {@link HyperTalon} to
	 */
	public void setWithRamp(ControlMode mode, double speed) {
		lazySet(mode, speed * limiter);
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed and demand multiplied by the
	 * {@link #limiter}, for the given {@link ControlMode} and {@link DemandType}.
	 * 
	 * @param mode        The mode at which to set this {@link HyperTalon} to
	 * @param speed       The speed at which to set this {@link HyperTalon} to
	 * @param demandType  The type of demand to set this {@link HyperTalon} to
	 * @param demandSpeed The value at which to set this {@link HyperTalon}'s demand
	 *                    speed to
	 */
	public void setWithRamp(ControlMode mode, double speed, DemandType demandType, double demandSpeed) {
		lazySet(mode, speed * limiter, demandType, demandSpeed * limiter);
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed, for the given
	 * {@link ControlMode}.
	 * 
	 * <p>
	 * Copied over from:
	 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/lib/drivers/LazyTalonSRX.java
	 * 
	 * @param mode  The mode at which to set this {@link HyperTalon} to
	 * @param speed The speed at which to set this {@link HyperTalon} to
	 */
	private void lazySet(ControlMode mode, double speed) {
		if (speed != lastSpeed || mode != lastMode) {
			lastSpeed = speed;
			lastMode = mode;

			super.set(mode, speed);
		}
	}

	/**
	 * Sets the {@link HyperTalon} to the given speed and demand, for the given
	 * {@link ControlMode} and {@link DemandType}.
	 * 
	 * @param mode        The mode at which to set this {@link HyperTalon} to
	 * @param speed       The speed at which to set this {@link HyperTalon} to
	 * @param demandType  The type of demand to set this {@link HyperTalon} to
	 * @param demandSpeed The value at which to set this {@link HyperTalon}'s demand
	 *                    speed to
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

	/**
	 * Syntactic sugar for resetting the {@link HyperTalon}'s
	 * {@link FeedbackDevice#QuadEncoder encoder}.
	 */
	public void reset() {
		reset(0);
	}

	/**
	 * Syntactic sugar for resetting the {@link HyperTalon}'s
	 * {@link FeedbackDevice#QuadEncoder encoder}.
	 * 
	 * @param pidIdx The {@link FeedbackDevice#QuadEncoder sensor} slot to read
	 */
	public void reset(int pidIdx) {
		setSelectedSensorPosition(pidIdx);
	}

	/**
	 * Set {@link #encoderDistancePerPulse encoder constant} for this
	 * {@link HyperTalon}.
	 * 
	 * @param encoderDistancePerPulse The distance travelled per
	 *                                {@link FeedbackDevice#QuadEncoder encoder}
	 *                                pulse
	 */
	public void setEncoderDistancePerPulse(double encoderDistancePerPulse) {
		this.encoderDistancePerPulse = encoderDistancePerPulse;
	}

	/**
	 * Syntactic sugar for getting the {@link HyperTalon}'s
	 * {@link FeedbackDevice#QuadEncoder encoder}'s current position.
	 * 
	 * @return The distance traveled by the {@link FeedbackDevice#QuadEncoder
	 *         encoder}, in units relative to the robot
	 */
	public double getEncoderDistance() {
		return getEncoderDistance(0);
	}

	/**
	 * Syntactic sugar for getting the {@link HyperTalon}'s
	 * {@link FeedbackDevice#QuadEncoder encoder}'s current position.
	 * 
	 * @param pidIdx The {@link FeedbackDevice#QuadEncoder sensor} slot to read
	 * @return The distance traveled by the {@link FeedbackDevice#QuadEncoder
	 *         encoder}, in units relative to the robot
	 */
	public double getEncoderDistance(int pidIdx) {
		return getSelectedSensorPosition(pidIdx) * encoderDistancePerPulse;
	}

	/**
	 * Syntactic sugar for getting the {@link HyperTalon}'s
	 * {@link FeedbackDevice#QuadEncoder encoder}'s current rate.
	 * 
	 * @return The rate at which the {@link FeedbackDevice#QuadEncoder encoder}
	 *         changes, in units relative to the robot
	 */
	public double getEncoderRate() {
		return getEncoderRate(0);
	}

	/**
	 * Syntactic sugar for getting the {@link HyperTalon}'s
	 * {@link FeedbackDevice#QuadEncoder encoder}'s current rate.
	 * 
	 * @param pidIdx The {@link FeedbackDevice#QuadEncoder sensor} slot to load
	 * @return The rate at which the {@link FeedbackDevice#QuadEncoder encoder}
	 *         changes, in units relative to the robot
	 */
	public double getEncoderRate(int pidIdx) {
		return getSelectedSensorVelocity() * encoderDistancePerPulse * 10;
	}
}