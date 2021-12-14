package org.usfirst.lib6647.loops;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.usfirst.lib6647.subsystem.BaseContainer;
import org.usfirst.lib6647.subsystem.SuperSubsystem;

/**
 * This class is a template Container for {@link Loop} subroutines. You need to
 * extend this class in your own RobotContainer and supply the
 * {@link #registerSubsystems} method with your own {@link SuperSubsystem}
 * instances.
 */
public abstract class LoopContainer extends BaseContainer {
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
		Stream.of(LoopType.values())
				.forEach(type -> loopers.put(type.name().toLowerCase(), new Looper(type.name().toLowerCase())));
	}

	/**
	 * Gets a {@link Looper} instance from the {@link #loopers} HashMap.
	 * 
	 * @param type The {@link LoopType type} of {@link Looper} to get.
	 * @return The requested {@link Looper} instance, if it exists.
	 */
	public Looper getLooper(LoopType type) {
		return loopers.get(type.name().toLowerCase());
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