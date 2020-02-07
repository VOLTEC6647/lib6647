package org.usfirst.lib6647.loops;

import org.usfirst.lib6647.wpilib.LooperRobot;

/**
 * Enum holding possible {@link Loop} types.
 * 
 * <p>
 * Currently we use four different types of {@link Loop Loops}:
 * 
 * <p>
 * - <b>ENABLED</b>, which runs while the {@link LooperRobot Robot} is enabled.
 * <p>
 * - <b>TELEOP</b>, which runs while the {@link LooperRobot Robot} is enabled
 * AND in teleoperated mode.
 * <p>
 * - <b>DISABLED</b>, which runs while the {@link LooperRobot Robot} is enabled
 * AND in autonomous mode.
 * <p>
 * - <b>AUTO</b>, which runs while the {@link LooperRobot Robot} is disabled.
 */
public enum LoopType {
	ENABLED, TELEOP, AUTO, DISABLED
}