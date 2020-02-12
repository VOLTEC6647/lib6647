package org.usfirst.lib6647.subsystem.hypercomponents;

import com.kauailabs.navx.frc.AHRS;

import org.usfirst.lib6647.subsystem.supercomponents.SuperAHRS;

import edu.wpi.first.wpilibj.SerialPort;

/**
 * Simple Wrapper for the {@link AHRS} class, only adds inverted values
 * functionality as of now.
 */
public class HyperAHRS extends AHRS {
	/** Makes {@link HyperAHRS AHRS} report inverted values if necessary. */
	private boolean inverted = false;

	/**
	 * Constructs the AHRS class using serial communication and the default update
	 * rate, and returning processed (rather than raw) data.
	 * <p>
	 * This constructor should be used if communicating via either TTL UART or USB
	 * Serial interface.
	 * <p>
	 * 
	 * @param port SerialPort to use.
	 */
	public HyperAHRS(SerialPort.Port port) {
		this(port, false);
	}

	/**
	 * Constructs the AHRS class using serial communication and the default update
	 * rate, and returning processed (rather than raw) data.
	 * <p>
	 * This constructor should be used if communicating via either TTL UART or USB
	 * Serial interface.
	 * <p>
	 * 
	 * @param port     SerialPort to use.
	 * @param inverted Whether or not to invert read values.
	 */
	public HyperAHRS(SerialPort.Port port, boolean inverted) {
		super(port);

		setInverted(inverted);
	}

	/**
	 * Sets whether or not the values read by this {@link SuperAHRS AHRS} should be
	 * inverted.
	 * 
	 * @param inverted Whether or not to invert read values.
	 */
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	/**
	 * Returns the current yaw value reported by the sensor. This yaw value is
	 * useful for implementing features including "auto rotate to a known angle".
	 * 
	 * <p>
	 * <b>NOTE</b>: Depending on the JSON configuration of this SuperComponent, the
	 * returned value may be inverted.
	 * 
	 * @return The {@link HyperAHRS}'s yaw angle in degrees, from -180 to 180
	 */
	@Override
	public float getYaw() {
		return (inverted ? -1.0F : 1.0F) * super.getYaw();
	}

	/**
	 * Returns the total accumulated yaw angle (Z Axis, in degrees) reported by the
	 * sensor.
	 * <p>
	 * <b>NOTE</b>: The angle is continuous, meaning it's range is beyond 360
	 * degrees. This ensures that algorithms that wouldn't want to see a
	 * discontinuity in the gyro output as it sweeps past 0 on the second time
	 * around.
	 * <p>
	 * Note that the returned yaw value will be offset by a user-specified offset
	 * value; this user-specified offset value is set by invoking the zeroYaw()
	 * method.
	 * <p>
	 * <b>NOTE</b>: Depending on the JSON configuration of this SuperComponent, the
	 * returned value may be inverted.
	 * 
	 * @return The current total accumulated yaw angle (Z axis) of the robot in
	 *         degrees
	 */
	@Override
	public double getAngle() {
		return (inverted ? -1.0 : 1.0) * super.getAngle();
	}

	/**
	 * Returns the heading of the {@link HyperAHRS}.
	 *
	 * @return The {@link HyperAHRS}'s heading in degrees, from -180 to 180
	 */
	public double getHeading() {
		return Math.IEEEremainder(super.getAngle(), 360) * (inverted ? -1.0 : 1.0);
	}
}