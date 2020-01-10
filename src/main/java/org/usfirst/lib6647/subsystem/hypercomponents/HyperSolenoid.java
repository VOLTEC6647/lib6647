package org.usfirst.lib6647.subsystem.hypercomponents;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * Simple Wrapper for a {@link Solenoid}, currently only adds a
 * {@link HyperSolenoid#toggle()} method.
 */
public class HyperSolenoid extends Solenoid {
	/**
	 * HyperComponent Wrapper for {@link Solenoid}.
	 * 
	 * @param channel
	 */
	public HyperSolenoid(int channel) {
		super(channel);
	}

	/**
	 * Toggle {@link Solenoid}.
	 */
	public void toggle() {
		set(!get());
	}
}