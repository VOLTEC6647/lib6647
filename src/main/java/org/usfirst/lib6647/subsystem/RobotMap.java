package org.usfirst.lib6647.subsystem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.LoopType;
import org.usfirst.lib6647.loops.Looper;

/**
 * Class holding instances of objects required to read values from a JSON file,
 * for {@link SuperSubsystem} usage. Also contains an ArrayList holding each of
 * the {@link SuperSubsystem subsystems}. Think of this class as a
 * {@link SuperSubsystem} manager.
 */
public class RobotMap implements ILooper {
	/** Map holding every {@link SuperSubsystem}, with its name as its key. */
	private final Map<String, SuperSubsystem> subsystems = new HashMap<>();
	/** Lists holding every {@link Loop}. */
	private final List<Loop> enabledLoops = new ArrayList<>(), teleopLoops = new ArrayList<>(),
			autoLoops = new ArrayList<>(), disabledLoops = new ArrayList<>();

	/**
	 * Return a {@link Collection} of every declared {@link SuperSubsystem}.
	 * 
	 * @return A {@link Collection} of every {@link SuperSubsystem} in the
	 *         {@link #subsystems} HashMap
	 */
	public synchronized Collection<SuperSubsystem> getSubsystems() {
		return subsystems.values();
	}

	/**
	 * Get a specific {@link SuperSubsystem}.
	 * 
	 * @param name The name of the {@link SuperSubsystem} to look for
	 * @return The specified {@link SuperSubsystem}, if found in the
	 *         {@link #subsystems} HashMap
	 */
	public synchronized SuperSubsystem getSubsystem(String name) {
		return subsystems.get(name);
	}

	/**
	 * Adds one or many {@link SuperSubsystem Subsystems} to the {@link #subsystems}
	 * map.
	 * 
	 * @param subsystems Each {@link SuperSubsystem} to register
	 */
	public synchronized void registerSubsystem(SuperSubsystem... subsystems) {
		for (SuperSubsystem s : subsystems)
			this.subsystems.putIfAbsent(s.getName(), s);
	}

	/**
	 * {@link Loop} implementation for running subroutines while the robot is
	 * enabled.
	 */
	private class EnabledLoop implements Loop {
		@Override
		public void onFirstStart(double timestamp) {
			enabledLoops.forEach(loop -> loop.onFirstStart(timestamp));
		}

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

		@Override
		public LoopType getType() {
			return LoopType.ENABLED;
		}
	}

	/**
	 * {@link Loop} implementation for running subroutines while the robot is
	 * enabled and in teleop mode.
	 */
	private class TeleopLoop implements Loop {
		@Override
		public void onFirstStart(double timestamp) {
			teleopLoops.forEach(loop -> loop.onFirstStart(timestamp));
		}

		@Override
		public void onStart(double timestamp) {
			teleopLoops.forEach(loop -> loop.onStart(timestamp));
		}

		@Override
		public void onLoop(double timestamp) {
			teleopLoops.forEach(loop -> loop.onLoop(timestamp));
		}

		@Override
		public void onStop(double timestamp) {
			teleopLoops.forEach(loop -> loop.onStop(timestamp));
		}

		@Override
		public LoopType getType() {
			return LoopType.TELEOP;
		}
	}

	/**
	 * {@link Loop} implementation for running subroutines while the robot is
	 * enabled and in auto mode.
	 */
	private class AutoLoop implements Loop {
		@Override
		public void onFirstStart(double timestamp) {
			autoLoops.forEach(loop -> loop.onFirstStart(timestamp));
		}

		@Override
		public void onStart(double timestamp) {
			autoLoops.forEach(loop -> loop.onStart(timestamp));
		}

		@Override
		public void onLoop(double timestamp) {
			autoLoops.forEach(loop -> loop.onLoop(timestamp));
		}

		@Override
		public void onStop(double timestamp) {
			autoLoops.forEach(loop -> loop.onStop(timestamp));
		}

		@Override
		public LoopType getType() {
			return LoopType.AUTO;
		}
	}

	/**
	 * {@link Loop} implementation for running subroutines while the robot is
	 * disabled.
	 */
	private class DisabledLoop implements Loop {
		@Override
		public void onFirstStart(double timestamp) {
			disabledLoops.forEach(loop -> loop.onFirstStart(timestamp));
		}

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

		@Override
		public LoopType getType() {
			return LoopType.DISABLED;
		}
	}

	/**
	 * Registers {@link Loop loops} for every {@link SuperSubsystem}.
	 * 
	 * @param enabledLooper  The enabled {@link Looper}
	 * @param teleopLooper   The teleop {@link Looper}
	 * @param autoLooper     The auto {@link Looper}
	 * @param disabledLooper The disabled {@link Looper}
	 */
	public void registerLoops(ILooper enabledLooper, ILooper teleopLooper, ILooper autoLooper, ILooper disabledLooper) {
		subsystems.values().forEach(s -> s.registerLoops(this));

		enabledLooper.register(new EnabledLoop());
		teleopLooper.register(new TeleopLoop());
		autoLooper.register(new AutoLoop());
		disabledLooper.register(new DisabledLoop());
	}

	/**
	 * Used to add {@link Loop Loops} to a List, in order to be later run depending
	 * on its {@link LoopType}.
	 * 
	 * @param loops {@link Loop Loops} to be registered
	 */
	@Override
	public void register(Loop... loops) {
		for (Loop loop : loops) {
			switch (loop.getType()) {
			case ENABLED:
				enabledLoops.add(loop);
				break;
			case TELEOP:
				teleopLoops.add(loop);
				break;
			case AUTO:
				autoLoops.add(loop);
				break;
			case DISABLED:
				disabledLoops.add(loop);
				break;
			default:
			}
		}
	}
}