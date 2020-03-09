package org.usfirst.lib6647.subsystem.hypercomponents;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;

/**
 * Simple Wrapper for a {@link Solenoid}, currently only adds a
 * {@link #toggle()} method.
 */
public class HyperSolenoid extends Solenoid {
	/**
	 * HyperComponent Wrapper for a {@link Solenoid}.
	 * 
	 * @param name    The {@link HyperSolenoid}'s name
	 * @param channel The {@link HyperSolenoid}'s channel
	 */
	public HyperSolenoid(String name, int channel) {
		super(channel);

		SendableRegistry.setName(this, name);
	}

	/**
	 * Toggle the {@link HyperSolenoid}.
	 */
	public void toggle() {
		set(!get());
	}
}