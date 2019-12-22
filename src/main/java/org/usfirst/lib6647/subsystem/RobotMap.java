package org.usfirst.lib6647.subsystem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.Looper;

/**
 * Class holding instances of objects required to read values from a JSON file,
 * for {@link SuperSubsystem} usage. Also contains an ArrayList holding each of
 * the {@link SuperSubsystem subsystems}.
 */
public class RobotMap implements ILooper {

	/** Static instance of {@link RobotMap}. */
	private static RobotMap instance = null;

	/**
	 * Gets static {@link RobotMap} instance. If there is none, creates one.
	 * 
	 * @return static {@link RobotMap} instance
	 */
	public static RobotMap getInstance() {
		if (instance == null)
			instance = new RobotMap();

		return instance;
	}

	/** Map holding every {@link SuperSubsystem}, with its name as its key. */
	private Map<String, SuperSubsystem> subsystems;
	/** List holding every {@link Loop}. */
	private List<Loop> loops;

	/**
	 * Constructor for {@link RobotMap}, initializes {@link #subsystems} and
	 * {@link #loops}.
	 */
	private RobotMap() {
		subsystems = new HashMap<String, SuperSubsystem>();
		loops = new ArrayList<Loop>();
	}

	/**
	 * Return a {@link Stream} of every declared {@link SuperSubsystem}.
	 * 
	 * @return stream
	 */
	public Stream<SuperSubsystem> getSubsystems() {
		return subsystems.values().stream();
	}

	/**
	 * Get a specific {@link SuperSubsystem}.
	 * 
	 * @param name
	 * @return subsystem
	 */
	public SuperSubsystem getSubsystem(String name) {
		return subsystems.get(name);
	}

	/**
	 * Set every subsystem managed by this class.
	 * 
	 * @param <T> subsystems
	 */
	@SafeVarargs
	public final <T extends SuperSubsystem> void setSubsystems(Supplier<T>... subsystems) {
		Arrays.asList(subsystems).forEach(s -> this.subsystems.put(s.get().getName(), (SuperSubsystem) s.get()));
	}

	/**
	 * {@link Loop} implementation for running subroutines while the robot is
	 * enabled.
	 */
	private class EnabledLoop implements Loop {
		@Override
		public void onStart(double timestamp) {
			loops.forEach(loop -> loop.onStart(timestamp));
		}

		@Override
		public void onLoop(double timestamp) {
			subsystems.values().forEach(SuperSubsystem::readPeriodicInputs);
			loops.forEach(loop -> loop.onLoop(timestamp));
			subsystems.values().forEach(SuperSubsystem::writePeriodicOutputs);
		}

		@Override
		public void onStop(double timestamp) {
			loops.forEach(loop -> loop.onStop(timestamp));
		}
	}

	/**
	 * {@link Loop} implementation for running subroutines while the robot is
	 * disabled.
	 */
	private class DisabledLoop implements Loop {
		@Override
		public void onStart(double timestamp) {
		}

		@Override
		public void onLoop(double timestamp) {
			subsystems.values().forEach(SuperSubsystem::readPeriodicInputs);
		}

		@Override
		public void onStop(double timestamp) {
		}
	}

	/**
	 * Registers enabled {@link Loop loops} for every {@link SuperSubsystem}.
	 * 
	 * @param enabledLooper
	 */
	public void registerEnabledLoops(Looper enabledLooper) {
		subsystems.values().forEach(s -> s.registerEnabledLoops(this));
		enabledLooper.register(new EnabledLoop());
	}

	/**
	 * Registers disabled {@link Loop loops} for every {@link SuperSubsystem}.
	 * 
	 * @param disabledLooper
	 */
	public void registerDisabledLoops(Looper disabledLooper) {
		disabledLooper.register(new DisabledLoop());
	}

	/**
	 * Adds a loop into the {@link #loops} {@link ArrayList}.
	 * 
	 * @param loop
	 */
	@Override
	public void register(Loop loop) {
		loops.add(loop);
	}
}