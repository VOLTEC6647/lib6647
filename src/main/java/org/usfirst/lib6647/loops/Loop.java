package org.usfirst.lib6647.loops;

import org.usfirst.lib6647.wpilib.LooperRobot;

/**
 * Interface for {@link Loop Loops}, which are routine that run periodically in
 * the robot code (such as periodic gyroscope calibration, etc.). Copied over
 * from:
 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/frc2019/loops/Loop.java.
 */
public interface Loop {
	/**
	 * Initialization code to run at the very first start of the {@link Loop}.
	 * 
	 * @param timestamp
	 */
	public void onFirstStart(double timestamp);

	/**
	 * Initialization code to run at each start of the {@link Loop}.
	 * 
	 * @param timestamp
	 */
	public void onStart(double timestamp);

	/**
	 * Code that is actually looped. Useful for gyro calibration or vision tracking
	 * at higher refresh rates.
	 * 
	 * @param timestamp
	 */
	public void onLoop(double timestamp);

	/**
	 * Code to run at the end of the {@link Loop}. Mostly useful for telemetry
	 * and/or garbage collection.
	 * 
	 * @param timestamp
	 */
	public void onStop(double timestamp);

	/**
	 * Return type of {@link Loop}. Currently we use four different types of
	 * {@link Loop Loops}: ENABLED, which runs while the {@link LooperRobot Robot}
	 * is enabled; TELEOP, which runs while the {@link LooperRobot Robot} is enabled
	 * AND in teleoperated mode; DISABLED, which runs while the {@link LooperRobot
	 * Robot} is enabled AND in autonomous mode; and AUTO, which runs while the
	 * {@link LooperRobot Robot} is disabled.
	 * 
	 * @return type
	 */
	public LoopType getType();
}