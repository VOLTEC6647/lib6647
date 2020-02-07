package org.usfirst.lib6647.subsystem.hypercomponents;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Simple Wrapper for a {@link Solenoid}, currently only adds a
 * {@link #toggle()} method.
 */
public class HyperSolenoid extends Solenoid {
	/**
	 * HyperComponent Wrapper for a {@link Solenoid}.
	 * 
	 * @param channel The {@link HyperSolenoid}'s channel
	 */
	public HyperSolenoid(int channel) {
		super(channel);
	}

	/**
	 * Toggle the {@link HyperSolenoid}.
	 */
	public void toggle() {
		set(!get());
	}
}