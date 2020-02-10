package org.usfirst.lib6647.util;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.fasterxml.jackson.databind.JsonNode;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;

/**
 * Utility class containing a couple useful methods for CTRE motor
 * initialization.
 */
public class MotorUtil {
	/**
	 * Get a {@link NeutralMode} value from a String.
	 * 
	 * <p>
	 * There are three types of {@link NeutralMode NeutralModes}:
	 * <p>
	 * - <b>{@link NeutralMode#Coast Coast}</b>
	 * <p>
	 * - <b>{@link NeutralMode#Brake Brake}</b>
	 * <p>
	 * - <b>{@link NeutralMode#EEPROMSetting EEPROMSetting}</b>
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param neutralMode The desired {@link NeutralMode}, as a String value
	 * @return The {@link NeutralMode}, as a valid enum
	 */
	public static NeutralMode getNeutralMode(String neutralMode) {
		switch (neutralMode) {
		case "Coast":
			return NeutralMode.Coast;
		case "Brake":
			return NeutralMode.Brake;
		case "EEPROMSetting":
			return NeutralMode.EEPROMSetting;
		default:
			return null;
		}
	}

	/**
	 * Get a {@link FeedbackDevice} value from a String.
	 * 
	 * <p>
	 * There are eleven types of {@link FeedbackDevice FeedbackDevices}:
	 * <p>
	 * - <b>{@link FeedbackDevice#QuadEncoder}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#Analog}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#Tachometer}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#PulseWidthEncodedPosition}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#SensorSum}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#SensorDifference}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#RemoteSensor0}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#RemoteSensor1}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#SoftwareEmulatedSensor}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#CTRE_MagEncoder_Absolute}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#CTRE_MagEncoder_Relative}</b>
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param feedbackDevice The desired {@link FeedbackDevice}, as a String value
	 * @return The {@link FeedbackDevice}, as a valid enum
	 */
	public static FeedbackDevice getFeedbackDevice(String feedbackDevice) {
		switch (feedbackDevice) {
		case "QuadEncoder":
			return FeedbackDevice.QuadEncoder;
		case "Analog":
			return FeedbackDevice.Analog;
		case "Tachometer":
			return FeedbackDevice.Tachometer;
		case "PulseWidthEncodedPosition":
			return FeedbackDevice.PulseWidthEncodedPosition;
		case "SensorSum":
			return FeedbackDevice.SensorSum;
		case "SensorDifference":
			return FeedbackDevice.SensorDifference;
		case "RemoteSensor0":
			return FeedbackDevice.RemoteSensor0;
		case "RemoteSensor1":
			return FeedbackDevice.RemoteSensor1;
		case "SoftwareEmulatedSensor":
			return FeedbackDevice.SoftwareEmulatedSensor;
		case "CTRE_MagEncoder_Absolute":
			return FeedbackDevice.CTRE_MagEncoder_Absolute;
		case "CTRE_MagEncoder_Relative":
			return FeedbackDevice.CTRE_MagEncoder_Relative;
		default:
			return null;
		}
	}

	/**
	 * Get an {@link EncodingType} value from a String.
	 * 
	 * @param encodingType The desired {@link EncodingType}, as a String value
	 * @return The {@link EncodingType}, as a valid enum
	 */
	public static EncodingType getEncodingType(String encodingType) {
		switch (encodingType) {
		case "k1X":
			return EncodingType.k1X;
		case "k2X":
			return EncodingType.k2X;
		case "k4X":
			return EncodingType.k4X;
		default:
			return null;
		}
	}

	/**
	 * This class must not be instantiated.
	 */
	private MotorUtil() {
		throw new AssertionError("utility class");
	}
}