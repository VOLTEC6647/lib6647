package org.usfirst.lib6647.loops;

/**
 * Enum holding possible {@link Loop} types. Currently we only use four
 * different types of loops: ENABLED, which runs while the {@link LooperRobot
 * Robot} is enabled; TELEOP, which runs while the {@link LooperRobot Robot} is
 * enabled AND in teleoperated mode; DISABLED, which runs while the
 * {@link LooperRobot Robot} is enabled and in autonomous mode; and AUTO, which
 * runs while the {@link LooperRobot Robot} is disabled.
 */
public enum LoopType {
	ENABLED, TELEOP, AUTO, DISABLED
}