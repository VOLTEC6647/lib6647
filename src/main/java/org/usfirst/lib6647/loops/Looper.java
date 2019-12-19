package org.usfirst.lib6647.loops;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;
import java.util.List;

/**
 * This code runs all of the robot's loops. Loop objects are stored in a List
 * object. They are started when the robot powers up and stopped after the
 * match.
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
		running = false;
		loops = new ArrayList<>();
		notifier = new Notifier(new NotifierRunnable() {
			@Override
			public void run() {
				synchronized (lock) {
					if (running) {
						double now = Timer.getFPGATimestamp();

						for (Loop loop : loops)
							loop.onLoop(now);

						dt = now - timestamp;
						timestamp = now;
					}
				}
			}
		});
	}

	public Looper() {
		this(0.2);
	}

	@Override
	public synchronized void register(Loop loop) {
		synchronized (lock) {
			loops.add(loop);
		}
	}

	public synchronized void start() {
		if (!running) {
			System.out.println("Starting loops");

			synchronized (lock) {
				timestamp = Timer.getFPGATimestamp();
				for (Loop loop : loops) {
					loop.onStart(timestamp);
				}
				running = true;
			}

			notifier.startPeriodic(period);
		}
	}

	public synchronized void stop() {
		if (running) {
			System.out.println("Stopping loops");
			notifier.stop();

			synchronized (lock) {
				running = false;
				timestamp = Timer.getFPGATimestamp();
				for (Loop loop : loops) {
					System.out.println("Stopping " + loop);
					loop.onStop(timestamp);
				}
			}
		}
	}

	public void outputToSmartDashboard() {
		SmartDashboard.putNumber("looper_dt", dt);
	}
}