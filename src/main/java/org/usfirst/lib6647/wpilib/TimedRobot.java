/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.lib6647.wpilib;

import java.util.PriorityQueue;

import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.NotifierJNI;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

/**
 * {@link TimedRobot} implements the {@link IterativeRobotBase} robot program
 * framework.
 *
 * <p>
 * The {@link TimedRobot} class is intended to be subclassed by a user creating
 * a robot program.
 *
 * <p>
 * periodic() functions from the base class are called on an interval by a
 * {@link Notifier} instance.
 */
public class TimedRobot extends IterativeRobotBase {
	@SuppressWarnings("MemberName")
	class Callback implements Comparable<Callback> {
		public Runnable func;
		public double period;
		public double expirationTime;

		/**
		 * Construct a callback container.
		 *
		 * @param func             The callback to run.
		 * @param startTimeSeconds The common starting point for all callback scheduling
		 *                         in seconds.
		 * @param periodSeconds    The period at which to run the callback in seconds.
		 * @param offsetSeconds    The offset from the common starting time in seconds.
		 */
		Callback(Runnable func, double startTimeSeconds, double periodSeconds, double offsetSeconds) {
			this.func = func;
			this.period = periodSeconds;
			this.expirationTime = startTimeSeconds
					+ offsetSeconds
					+ Math.floor((Timer.getFPGATimestamp() - startTimeSeconds) / this.period)
							* this.period
					+ this.period;
		}

		@Override
		public int compareTo(Callback rhs) {
			// Elements with sooner expiration times are sorted as lesser. The head of
			// Java's PriorityQueue is the least element.
			return Double.compare(expirationTime, rhs.expirationTime);
		}
	}

	/** {@link TimedRobot}'s default period time. */
	public static final double defaultPeriod = 0.02;

	/**
	 * The C pointer to the notifier object. We don't use it directly, it is just
	 * passed to the JNI bindings.
	 */
	private final int notifier = NotifierJNI.initializeNotifier();

	private double startTime;
	private final PriorityQueue<Callback> callbacks = new PriorityQueue<>();

	/**
	 * Constructor for {@link TimedRobot}.
	 */
	protected TimedRobot() {
		this(defaultPeriod);
	}

	/**
	 * Constructor for {@link TimedRobot}.
	 *
	 * @param period Period in seconds
	 */
	protected TimedRobot(double period) {
		super(period);
		startTime = Timer.getFPGATimestamp();

		addPeriodic(this::loopFunc, period);
		NotifierJNI.setNotifierName(notifier, "TimedRobot");

		HAL.report(tResourceType.kResourceType_Framework, tInstances.kFramework_Timed);
	}

	@Override
	@SuppressWarnings("NoFinalizer")
	protected void finalize() {
		NotifierJNI.stopNotifier(notifier);
		NotifierJNI.cleanNotifier(notifier);
	}

	/**
	 * Provide an alternate "main loop" via {@link #startCompetition()}.
	 */
	@Override
	@SuppressWarnings("UnsafeFinalization")
	public void startCompetition() {
		robotInit();

		if (isSimulation())
			simulationInit();

		// Tell the DS that the robot is ready to be enabled
		HAL.observeUserProgramStarting();

		// Loop forever, calling the appropriate mode-dependent function
		while (true) {
			// We don't have to check there's an element in the queue first because
			// there's always at least one (the constructor adds one). It's reenqueued
			// at the end of the loop.
			var callback = callbacks.poll();

			NotifierJNI.updateNotifierAlarm(notifier, (long) (callback.expirationTime * 1e6));

			long curTime = NotifierJNI.waitForNotifierAlarm(notifier);
			if (curTime == 0)
				break;

			callback.func.run();
			callback.expirationTime += callback.period;
			callbacks.add(callback);

			// Process all other callbacks that are ready to run
			while ((long) (callbacks.peek().expirationTime * 1e6) <= curTime) {
				callback = callbacks.poll();

				callback.func.run();

				callback.expirationTime += callback.period;
				callbacks.add(callback);
			}
		}
	}

	/**
	 * Ends the main loop in {@link #startCompetition()}.
	 */
	@Override
	public void endCompetition() {
		NotifierJNI.stopNotifier(notifier);
	}

	/**
	 * Get time period between calls to Periodic() functions.
	 * 
	 * @return This {@link TimedRobot}'s period time, in seconds
	 */
	public double getPeriod() {
		return period;
	}

	/**
	 * Add a callback to run at a specific period.
	 *
	 * <p>
	 * This is scheduled on {@link TimedRobot}'s {@link Notifier}, so
	 * {@link TimedRobot} and the callback run synchronously.
	 * Interactions between them are thread-safe.
	 *
	 * @param callback      The callback to run.
	 * @param periodSeconds The period at which to run the callback in seconds.
	 */
	public void addPeriodic(Runnable callback, double periodSeconds) {
		callbacks.add(new Callback(callback, startTime, periodSeconds, 0.0));
	}

	/**
	 * Add a callback to run at a specific period with a starting time offset.
	 *
	 * <p>
	 * This is scheduled on {@link TimedRobot}'s {@link Notifier}, so
	 * {@link TimedRobot} and the callback run synchronously.
	 * Interactions between them are thread-safe.
	 *
	 * @param callback      The callback to run.
	 * @param periodSeconds The period at which to run the callback in seconds.
	 * @param offsetSeconds The offset from the common starting time in seconds.
	 *                      This is useful for
	 *                      scheduling a callback in a different timeslot relative
	 *                      to {@link TimedRobot}.
	 */
	public void addPeriodic(Runnable callback, double periodSeconds, double offsetSeconds) {
		callbacks.add(new Callback(callback, startTime, periodSeconds, offsetSeconds));
	}
}
