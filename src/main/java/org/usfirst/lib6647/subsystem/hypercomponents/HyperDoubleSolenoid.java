package org.usfirst.lib6647.subsystem.hypercomponents;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;

/**
 * Simple Wrapper for a {@link Solenoid DoubleSolenoid}, currently only adds a
 * {@link #toggle()} method.
 */
public class HyperDoubleSolenoid implements Sendable, AutoCloseable {
	/** {@link HyperSolenoid Solenoids} used by this HyperComponent. */
	private HyperSolenoid forward, reverse;

	/**
	 * HyperComponent Wrapper for a {@link DoubleSolenoid}.
	 * 
	 * @param name           The {@link HyperDoubleSolenoid}'s name
	 * @param forwardChannel The {@link HyperDoubleSolenoid}'s forward channel
	 * @param reverseChannel The {@link HyperDoubleSolenoid}'s reverse channel
	 */
	public HyperDoubleSolenoid(String name, int forwardChannel, int reverseChannel) {
		forward = new HyperSolenoid(name + "Forward", forwardChannel);
		reverse = new HyperSolenoid(name + "Reverse", reverseChannel);

		SendableRegistry.addLW(this, name);
		SendableRegistry.addChild(this, forward);
		SendableRegistry.addChild(this, reverse);
	}

	@Override
	public void close() {
		SendableRegistry.remove(this);
		forward.close();
		reverse.close();
	}

	/**
	 * Set {@link HyperDoubleSolenoid} to a specific value.
	 * 
	 * @param value The value to set the {@link HyperDoubleSolenoid}
	 */
	public void set(boolean value) {
		forward.set(value);
		reverse.set(!value);
	}

	/**
	 * Toggle the {@link HyperDoubleSolenoid}.
	 */
	public void toggle() {
		set(!forward.get());
	}

	/**
	 * Gets the state of the {@link HyperDoubleSolenoid}.
	 * 
	 * @return The state of the {@link HyperDoubleSolenoid}
	 */
	public boolean get() {
		return forward.get();
	}

	/**
	 * Set {@link #forward} {@link HyperSolenoid} to a specific value.
	 * 
	 * @param value The value to set the {@link HyperSolenoid}
	 */
	public void setForward(boolean value) {
		forward.set(value);
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
	 * Set {@link #reverse} {@link HyperSolenoid} to a specific value.
	 * 
	 * @param value The value to set the {@link HyperSolenoid}
	 */
	public void setReverse(boolean value) {
		reverse.set(value);
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
	 * Stop any {@link HyperDoubleSolenoid} movement.
	 */
	public void stop() {
		forward.set(false);
		reverse.set(false);
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("Solenoid");
		builder.setActuator(true);
		builder.setSafeState(() -> set(false));
		builder.addBooleanProperty("Value", this::get, this::set);
		builder.addBooleanProperty("ForwardValue", this::getForward, this::setForward);
		builder.addBooleanProperty("ReverseValue", this::getReverse, this::setReverse);
	}
}