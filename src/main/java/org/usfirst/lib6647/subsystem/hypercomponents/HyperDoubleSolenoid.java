package org.usfirst.lib6647.subsystem.hypercomponents;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Simple Wrapper for a {@link Solenoid DoubleSolenoid}, currently only adds a
 * {@link HyperDoubleSolenoid#toggle()} method.
 */
public class HyperDoubleSolenoid {
	/** {@link Solenoid Solenoids} used by this HyperComponent. */
	private Solenoid forward, reverse;

	/**
	 * HyperComponent Wrapper for a {@link Solenoid DoubleSolenoid}.
	 * 
	 * @param forwardChannel
	 * @param reverseChannel
	 */
	public HyperDoubleSolenoid(int forwardChannel, int reverseChannel) {
		forward = new Solenoid(forwardChannel);
		reverse = new Solenoid(reverseChannel);
	}

	/**
	 * Set {@link Solenoid DoubleSolenoid} to a specific value.
	 * 
	 * @param value
	 */
	public void set(boolean value) {
		forward.set(value);
		reverse.set(!value);
	}

	/**
	 * Toggle {@link Solenoid DoubleSolenoid}.
	 */
	public void toggle() {
		set(!forward.get());
	}

	/**
	 * Stop any {@link Solenoid DoubleSolenoid} movement.
	 */
	public void stop() {
		forward.set(false);
		reverse.set(false);
	}
}