package org.usfirst.lib6647.subsystem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	/** Map holding every {@link SuperSubsystem}, with its name as its key. */
	private Map<String, SuperSubsystem> subsystems;
	/** Lists holding every {@link Loop}. */
	private List<Loop> enabledLoops, disabledLoops, periodicLoops;

	/**
	 * Constructor for {@link RobotMap}, initializes {@link #subsystems} and
	 * {@link #loops}.
	 */
	public RobotMap() {
		subsystems = new HashMap<>();
		enabledLoops = new ArrayList<>();
		disabledLoops = new ArrayList<>();
		periodicLoops = new ArrayList<>();
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
	 * Adds a {@link SuperSubsystem} to the map of {@link RobotMap#subsystems
	 * subsystems}.
	 * 
	 * @param SuperSubsystem
	 * @param subsystem
	 */
	public void registerSubsystem(SuperSubsystem subsystem) {
		subsystems.put(subsystem.getName(), subsystem);
	}

	/**
	 * {@link Loop} implementation for running subroutines while the robot is
	 * enabled.
	 */
	private class EnabledLoop implements Loop {
		@Override
		public void onStart(double timestamp) {
			enabledLoops.forEach(loop -> loop.onStart(timestamp));
		}

		@Override
		public void onLoop(double timestamp) {
			subsystems.values().forEach(SuperSubsystem::readPeriodicInputs);
			enabledLoops.forEach(loop -> loop.onLoop(timestamp));
			subsystems.values().forEach(SuperSubsystem::writePeriodicOutputs);
		}

		@Override
		public void onStop(double timestamp) {
			enabledLoops.forEach(loop -> loop.onStop(timestamp));
		}
	}

	/**
	 * {@link Loop} implementation for running subroutines while the robot is
	 * disabled.
	 */
	private class DisabledLoop implements Loop {
		@Override
		public void onStart(double timestamp) {
			disabledLoops.forEach(loop -> loop.onStart(timestamp));
		}

		@Override
		public void onLoop(double timestamp) {
			subsystems.values().forEach(SuperSubsystem::readPeriodicInputs);
			disabledLoops.forEach(loop -> loop.onLoop(timestamp));
			subsystems.values().forEach(SuperSubsystem::writePeriodicOutputs);
		}

		@Override
		public void onStop(double timestamp) {
			disabledLoops.forEach(loop -> loop.onStop(timestamp));
		}
	}

	/**
	 * {@link Loop} implementation for running subroutines.
	 */
	private class PeriodicLoop implements Loop {
		@Override
		public void onStart(double timestamp) {
			periodicLoops.forEach(loop -> loop.onStart(timestamp));
		}

		@Override
		public void onLoop(double timestamp) {
			subsystems.values().forEach(SuperSubsystem::readPeriodicInputs);
			periodicLoops.forEach(loop -> loop.onLoop(timestamp));
			subsystems.values().forEach(SuperSubsystem::writePeriodicOutputs);
		}

		@Override
		public void onStop(double timestamp) {
			// Should never reach here, as periodic loops will always be running.
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
		subsystems.values().forEach(s -> s.registerDisabledLoops(this));
		disabledLooper.register(new DisabledLoop());
	}

	/**
	 * Registers periodic {@link Loop loops} for every {@link SuperSubsystem}.
	 * 
	 * @param periodicLooper
	 */
	public void registerPeriodicLoops(Looper periodicLooper) {
		subsystems.values().forEach(s -> s.registerPeriodicLoops(this));
		periodicLooper.register(new PeriodicLoop());
	}

	@Override
	public void register(Loop loop) {
	}
}