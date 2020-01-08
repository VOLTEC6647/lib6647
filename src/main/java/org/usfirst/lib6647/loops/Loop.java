package org.usfirst.lib6647.loops;

/**
 * Interface for loops, which are routine that run periodically in the robot
 * code (such as periodic gyroscope calibration, etc.) Copied over from:
 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/frc2019/loops/Loop.java
 */
public interface Loop {
	/**
	 * Initialization code to run at the start of the {@link Loop}.
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
	 * Return type of {@link Loop}. Currently we only use three: Enabled, which runs
	 * while the {@link Robot LooperRobot} is enabled; Disabled, which runs while
	 * the {@link Robot LooperRobot} is disabled; and Periodic, which runs forever.
	 * 
	 * @return type
	 */
	public LoopType getType();
}