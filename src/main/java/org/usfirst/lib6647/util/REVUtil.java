package org.usfirst.lib6647.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

/**
 * Utility class containing a couple useful methods for REV motor
 * initialization.
 */
public class REVUtil {
	/**
	 * Get a {@link MotorType} value from a String.
	 * 
	 * <p>
	 * There are two {@link MotorType motor types}:
	 * <p>
	 * - <b>{@link MotorType#kBrushed}</b>
	 * <p>
	 * - <b>{@link MotorType#kBrushless}</b>
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param motorType The desired {@link MotorType}, as a String value
	 * @return The {@link MotorType}, as a valid enum
	 */
	public static MotorType getMotorType(String motorType) {
		switch (motorType) {
			case "Brushed":
				return MotorType.kBrushed;
			case "Brushless":
				return MotorType.kBrushless;
			default:
				return null;
		}
	}

	/**
	 * Get a {@link IdleMode} value from a String.
	 * 
	 * <p>
	 * There are two {@link IdleMode idle modes}:
	 * <p>
	 * - <b>{@link IdleMode#kCoast}</b>
	 * <p>
	 * - <b>{@link IdleMode#kBrake}</b>
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param idleMode The desired {@link IdleMode}, as a String value
	 * @return The {@link IdleMode}, as a valid enum
	 */
	public static IdleMode getIdleMode(String idleMode) {
		switch (idleMode) {
			case "Coast":
				return IdleMode.kCoast;
			case "Brake":
				return IdleMode.kBrake;
			default:
				return null;
		}
	}

	/**
	 * This class must not be instantiated.
	 */
	private REVUtil() {
		throw new AssertionError("utility class");
	}
}