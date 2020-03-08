package org.usfirst.lib6647.subsystem.hypercomponents;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Simple Wrapper for a {@link Solenoid DoubleSolenoid}, currently only adds a
 * {@link #toggle()} method.
 */
public class HyperDoubleSolenoid {
	/** {@link Solenoid Solenoids} used by this HyperComponent. */
	private Solenoid forward, reverse;

	// TODO: Make this Sendable, add Solenoids as children.

	/**
	 * HyperComponent Wrapper for a {@link Solenoid DoubleSolenoid}.
	 * 
	 * @param forwardChannel The {@link Solenoid DoubleSolenoid}'s forward channel
	 * @param reverseChannel The {@link Solenoid DoubleSolenoid}'s reverse channel
	 */
	public HyperDoubleSolenoid(int forwardChannel, int reverseChannel) {
		forward = new Solenoid(forwardChannel);
		reverse = new Solenoid(reverseChannel);
	}

	/**
	 * Set {@link Solenoid DoubleSolenoid} to a specific value.
	 * 
	 * @param value The value to set the {@link Solenoid DoubleSolenoid}
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
	 * Gets the state of the {@link #forward} {@link HyperDoubleSolenoid}.
	 * 
	 * @return The state of the {@link #forward forward solenoid}
	 */
	public boolean getForward() {
		return forward.get();
	}

	/**
	 * Gets the state of the {@link #reverse} {@link HyperDoubleSolenoid}.
	 * 
	 * @return The state of the {@link #reverse reverse solenoid}
	 */
	public boolean getReverse() {
		return reverse.get();
	}

	/**
	 * Stop any {@link Solenoid DoubleSolenoid} movement.
	 */
	public void stop() {
		forward.set(false);
		reverse.set(false);
	}
}