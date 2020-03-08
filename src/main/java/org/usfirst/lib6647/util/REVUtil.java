package org.usfirst.lib6647.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.revrobotics.AlternateEncoderType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.EncoderType;

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
	 * Get a {@link EncoderType} value from a String.
	 * 
	 * <p>
	 * There are four {@link EncoderType encoder types}:
	 * <p>
	 * - <b>{@link EncoderType#kHallSensor}</b>
	 * <p>
	 * - <b>{@link EncoderType#kQuadrature}</b>
	 * <p>
	 * - <b>{@link EncoderType#kSensorless}</b>
	 * <p>
	 * - <b>{@link EncoderType#kNoSensor}</b>
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param encoderType The desired {@link EncoderType}, as a String value
	 * @return The {@link EncoderType}, as a valid enum
	 */
	public static EncoderType getEncoderType(String encoderType) {
		switch (encoderType) {
			case "HallSensor":
				return EncoderType.kHallSensor;
			case "Quadrature":
				return EncoderType.kQuadrature;
			case "Sensorless":
				return EncoderType.kSensorless;
			case "None":
			case "NoSensor":
			default:
				return EncoderType.kNoSensor;
		}
	}

	/**
	 * Get a {@link AlternateEncoderType} value from a String.
	 * 
	 * <p>
	 * There is currently only one {@link AlternateEncoderType alternate encoder
	 * type}:
	 * <p>
	 * - <b>{@link AlternateEncoderType#kQuadrature}</b>
	 * <p>
	 * It must share the same name in the {@link JsonNode}.
	 * 
	 * @param alternateEncoderType The desired {@link AlternateEncoderType}, as a
	 *                             String value
	 * @return The {@link AlternateEncoderType}, as a valid enum
	 */
	public static AlternateEncoderType getAlternateEncoderType(String alternateEncoderType) {
		switch (alternateEncoderType) {
			case "Quadrature":
			default:
				return AlternateEncoderType.kQuadrature;
		}
	}

	/**
	 * This class must not be instantiated.
	 */
	private REVUtil() {
		throw new AssertionError("utility class");
	}
}