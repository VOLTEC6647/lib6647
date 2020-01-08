package org.usfirst.lib6647.loops;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This code runs all of the robot's loops. Loop objects are stored in a List
 * object. They are started when the robot powers up and stopped after the
 * match. Copied over from:
 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/frc2019/loops/Looper.java
 */
public class Looper implements ILooper {
	public final double period;
	private boolean running;

	private final Notifier notifier;
	private final List<Loop> loops;

	private final Object lock = new Object();
	private double timestamp = 0, dt = 0;

	public Looper(double period) {
		this.period = period;

		loops = new ArrayList<>();
		notifier = new Notifier(() -> {
			synchronized (lock) {
				if (running) {
					double now = Timer.getFPGATimestamp();

					loops.forEach(l -> l.onLoop(now));

					dt = now - timestamp;
					timestamp = now;
				}
			}
		});
		running = false;
	}

	public Looper() {
		this(0.01);
	}

	@Override
	public synchronized void register(Loop loop) {
		synchronized (lock) {
			loops.add(loop);
		}
	}

	/**
	 * Start registered {@link Loop Loops} in {@link Looper#loops}.
	 */
	public synchronized void start() {
		if (!running) {
			System.out.println("Starting loops...");

			synchronized (lock) {
				timestamp = Timer.getFPGATimestamp();
				loops.forEach(l -> l.onStart(timestamp));
				running = true;
			}

			notifier.startPeriodic(period);
		}
	}

	/**
	 * Stop registered {@link Loop Loops} in {@link Looper#loops}.
	 */
	public synchronized void stop() {
		if (running) {
			System.out.println("Stopping loops...");
			notifier.stop();

			synchronized (lock) {
				timestamp = Timer.getFPGATimestamp();
				loops.forEach(l -> l.onStop(timestamp));
				running = false;
			}
		}
	}

	/**
	 * Output {@link Looper#dt DT} to SmartDashboard.
	 */
	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("looper_dt", dt);
	}
}