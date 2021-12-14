package org.usfirst.lib6647.loops;

/**
 * Enum holding possible {@link Loop} types.
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
 */
public enum LoopType {
	ENABLED, TELEOP, AUTO, DISABLED
}