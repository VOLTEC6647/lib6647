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
	 * @param inverted
	 */
	public void setInverted(boolean inverted) {
		this.inverted = inverted;
	}

	/**
	 * @NOTE Depending on the JSON configuration of this SuperComponent, the
	 *       returned value may be inverted.
	 */
	@Override
	public float getYaw() {
		return (inverted ? -1 : 1) * super.getYaw();
	}

	/**
	 * @NOTE Depending on the JSON configuration of this SuperComponent, the
	 *       returned value may be inverted.
	 */
	@Override
	public double getAngle() {
		return (inverted ? -1 : 1) * super.getAngle();
	}
}