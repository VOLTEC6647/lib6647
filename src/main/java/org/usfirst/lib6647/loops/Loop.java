package org.usfirst.lib6647.loops;

/**
 * Interface for loops, which are routine that run periodically in the robot
 * code (such as periodic gyroscope calibration, etc.) Copied over from:
 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/frc2019/loops/Loop.java
 */
public interface Loop {
	public void onStart(double timestamp);

	public void onLoop(double timestamp);

	public void onStop(double timestamp);
}