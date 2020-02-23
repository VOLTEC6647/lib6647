/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.lib6647.wpilib;

import edu.wpi.first.hal.FRCNetComm.tInstances;
import edu.wpi.first.hal.FRCNetComm.tResourceType;
import edu.wpi.first.hal.HAL;
import edu.wpi.first.hal.NotifierJNI;
import edu.wpi.first.wpilibj.RobotController;

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
 * Notifier instance.
 */
public class TimedRobot extends IterativeRobotBase {
	/** {@link TimedRobot}'s default period time. */
	public static final double defaultPeriod = 0.02;

	/**
	 * The C pointer to the notifier object. We don't use it directly, it is just
	 * passed to the JNI bindings.
	 */
	private final int notifier = NotifierJNI.initializeNotifier();

	/** The absolute expiration time. */
	private double expirationTime;

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

		if (isSimulation()) {
			simulationInit();
		}

		// Tell the DS that the robot is ready to be enabled
		HAL.observeUserProgramStarting();

		expirationTime = RobotController.getFPGATime() * 1e-6 + period;
		updateAlarm();

		// Loop forever, calling the appropriate mode-dependent function
		while (true) {
			long curTime = NotifierJNI.waitForNotifierAlarm(notifier);
			if (curTime == 0) {
				break;
			}

			expirationTime += period;
			updateAlarm();

			loopFunc();
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
	 * Update the alarm hardware to reflect the next alarm.
	 */
	@SuppressWarnings("UnsafeFinalization")
	private void updateAlarm() {
		NotifierJNI.updateNotifierAlarm(notifier, (long) (expirationTime * 1e6));
	}
}
