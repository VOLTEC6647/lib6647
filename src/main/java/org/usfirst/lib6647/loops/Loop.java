package org.usfirst.lib6647.loops;

/**
 * Interface for {@link Loop Loops}, which are routine that run periodically in
 * the robot code (such as periodic gyroscope calibration, etc.).
 * 
 * <p>
 * Copied over from:
 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/frc2019/loops/Loop.java.
 */
public interface Loop {
	/**
	 * Initialization code to run at the very first start of the {@link Loop}.
	 * 
	 * @param timestamp Usually the FPGA timestamp
	 */
	public void onFirstStart(double timestamp);

	/**
	 * Initialization code to run at each start of the {@link Loop}.
	 * 
	 * @param timestamp Usually the FPGA timestamp
	 */
	public void onStart(double timestamp);

	/**
	 * Code that is actually looped. Useful for gyro calibration or vision tracking
	 * at higher refresh rates.
	 * 
	 * @param timestamp Usually the FPGA timestamp
	 */
	public void onLoop(double timestamp);

	/**
	 * Code to run at the end of the {@link Loop}. Mostly useful for telemetry
	 * and/or garbage collection.
	 * 
	 * @param timestamp Usually the FPGA timestamp
	 */
	public void onStop(double timestamp);

	/**
	 * Return type of {@link Loop}.
	 * 
	 * <p>
	 * Currently we use four different types of {@link Loop Loops}:
	 * 
	 * <p>
	 * - <b>ENABLED</b>, which runs while the Robot is enabled.
	 * <p>
	 * - <b>TELEOP</b>, which runs while the Robot is enabled AND in teleoperated
	 * mode.
	 * <p>
	 * - <b>DISABLED</b>, which runs while the Robot is enabled AND in autonomous
	 * mode.
	 * <p>
	 * - <b>AUTO</b>, which runs while the Robot is disabled.
	 * 
	 * @return type The type of {@link Loop}
	 */
	public LoopType getType();
}