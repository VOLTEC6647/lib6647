package org.usfirst.lib6647.loops;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This code runs all of the robot's loops. {@link Loop} objects are stored in a
 * {@link #loops list}. They are started when the robot powers up and stopped
 * after the match. Copied over from:
 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/frc2019/loops/Looper.java.
 */
public class Looper implements ILooper {
	/** Period at which to run each {@link Loop}. */
	private final double period;
	/** Name of {@link Notifier}/{@link Looper} instance. */
	private final String name;

	/** Check to see if the {@link Looper} is currently running. */
	private boolean running;

	/** Handles running {@link Loops} in a separate Thread. */
	private final Notifier notifier;
	/** List holding each of this {@link Looper} instance's {@link Loop Loops}. */
	private final List<Loop> loops;

	/** Object to ensure asynchronicity from every method. */
	private final Object lock = new Object();
	private double timestamp = 0, dt = 0;

	/** Check for whether it's the first time the {@link Loop} runs. */
	private boolean firstStart = true, firstRun = true;

	/**
	 * Constructor for {@link Looper}. Runs each declared {@link Loop} at the
	 * provided rate.
	 * 
	 * @param period
	 * @param name
	 */
	public Looper(double period, String name) {
		this.period = period;
		this.name = name;

		loops = new ArrayList<>();
		notifier = new Notifier(() -> {
			synchronized (lock) {
				if (running) {
					if (firstRun) {
						Thread.currentThread().setName(name);
						Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
						firstRun = false;
					}

					double now = Timer.getFPGATimestamp();

					loops.forEach(loop -> loop.onLoop(now));

					dt = now - timestamp;
					timestamp = now;
				}
			}
		});

		notifier.setName(name);
		running = false;
	}

	/**
	 * Constructor for {@link Looper}. Runs each declared {@link Loop} at the
	 * default {@link #period rate} of 0.01 (10ms).
	 * 
	 * @param name
	 */
	public Looper(String name) {
		this(0.01, name);
	}

	/**
	 * Adds each given {@link Loop} to the {@link #loops list}.
	 * 
	 * @param loops
	 */
	@Override
	public synchronized void register(Loop... loops) {
		synchronized (lock) {
			for (Loop loop : loops) {
				this.loops.add(loop);
			}
		}
	}

	/**
	 * Start registered {@link Loop Loops} in {@link #loops}.
	 */
	public synchronized void start() {
		if (!running) {
			System.out.println("Starting " + name + " loops...");

			synchronized (lock) {
				if (firstStart) {
					timestamp = Timer.getFPGATimestamp();
					loops.forEach(loop -> loop.onFirstStart(timestamp));
				}

				timestamp = Timer.getFPGATimestamp();
				loops.forEach(loop -> loop.onStart(timestamp));

				running = true;
				firstStart = false;
			}

			notifier.startPeriodic(period);
		}
	}

	/**
	 * Stop registered {@link Loop Loops} in {@link #loops}.
	 */
	public synchronized void stop() {
		if (running) {
			System.out.println("Stopping " + name + " loops...");
			notifier.stop();

			synchronized (lock) {
				timestamp = Timer.getFPGATimestamp();
				loops.forEach(loop -> loop.onStop(timestamp));
				running = false;
			}
		}
	}

	/**
	 * Output {@link #dt DT} to SmartDashboard.
	 */
	public void outputToSmartDashboard() {
		SmartDashboard.putNumber(name + "_looper_dt", dt);
	}
}