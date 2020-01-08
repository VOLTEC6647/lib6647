package org.usfirst.lib6647.loops;

/**
 * Enum holding possible {@link Loop} types. Currently we only use three types:
 * Enabled, which runs while the {@link Robot LooperRobot} is enabled; Disabled,
 * which runs while the {@link Robot LooperRobot} is disabled; and Periodic,
 * which runs forever.
 */
public enum LoopType {
	DISABLED, ENABLED, PERIODIC
}