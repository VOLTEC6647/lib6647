package org.usfirst.lib6647.subsystem.hypercomponents;

import edu.wpi.first.wpilibj.Solenoid;

/**
 * HyperComponent for {@link Solenoid}.
 */
public class HyperSolenoid extends Solenoid {

	/**
	 * Wrapper for {@link Solenoid}.
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