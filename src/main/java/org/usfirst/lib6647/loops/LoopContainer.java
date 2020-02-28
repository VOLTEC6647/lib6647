package org.usfirst.lib6647.loops;

import java.util.HashMap;
import java.util.Map;

import org.usfirst.lib6647.subsystem.BaseContainer;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

/**
 * This class is a template Container for {@link Loop} subroutines. You need to
 * extend this class in your own RobotContainer and supply the
 * {@link #registerSubsystems} method with your own {@link SuperSubsystem}
 * instances.
 */
public class LoopContainer extends BaseContainer {
	/** HashMap holding each {@link Looper} instance, to be run separately. */
	private final Map<String, Looper> loopers = new HashMap<>();
	/** Instance of {@link LoopRegistrar}. */
	private final LoopRegistrar loopRegistrar = new LoopRegistrar();

	/**
	 * Constructor for {@link LoopContainer}. You need to extend this class in your
	 * own RobotContainer and supply the {@link #registerSubsystems} method with
	 * your own {@link SuperSubsystem} instances.
	 */
	public LoopContainer() {
		loopers.put(LoopType.ENABLED.value, new Looper(LoopType.ENABLED.value));
		loopers.put(LoopType.DISABLED.value, new Looper(LoopType.DISABLED.value));
		loopers.put(LoopType.AUTO.value, new Looper(LoopType.AUTO.value));
		loopers.put(LoopType.TELEOP.value, new Looper(LoopType.TELEOP.value));
	}

	/**
	 * Gets a {@link Looper} instance from the {@link #loopers} HashMap.
	 * 
	 * @param type The {@link LoopType type} of {@link Looper} to get.
	 * @return The requested {@link Looper} instance, if it exists.
	 */
	public Looper getLooper(LoopType type) {
		return loopers.get(type.value);
	}

	/**
	 * Register each and every given {@link SuperSubsystem}, as well as their
	 * {@link Loop loop subroutines}, in the {@link LoopRegistrar}.
	 * 
	 * @param subsystems Every {@link SuperSubsystem} to register
	 */
	public synchronized void registerSubsystems(SuperSubsystem... subsystems) {
		loopRegistrar.registerSubsystems(subsystems);
		loopRegistrar.registerLoops(getLooper(LoopType.ENABLED), getLooper(LoopType.TELEOP), getLooper(LoopType.AUTO),
				getLooper(LoopType.DISABLED));
	}
}