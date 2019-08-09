package org.usfirst.lib6647.util;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.json.simple.JSONObject;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;

/**
 * SuperInterface for converting String values from a {@link JSONObject} to enum
 * types.
 */
public interface MotorUtils {

	/**
	 * Method to convert a String value to its respective {@link NeutralMode}.
	 * 
	 * @param neutralMode
	 * @return {@link NeutralMode}
	 * 
	 * @note There are three types of {@link NeutralMode NeutralModes}:
	 *       {@link NeutralMode#Coast}, {@link NeutralMode#Brake}, and
	 *       {@link NeutralMode#EEPROMSetting}. All of which should share the same
	 *       name in the {@link JSONObject}.
	 */
	default NeutralMode getNeutralMode(String neutralMode) {
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
	 * Method to convert a String to its respective {@link FeedbackDevice}.
	 * 
	 * @param feedbackDevice
	 * @return {@link FeedbackDevice}
	 * 
	 * @note There are eleven types of {@link FeedbackDevice FeedbackDevices}:
	 *       {@link FeedbackDevice#QuadEncoder}, {@link FeedbackDevice#Analog},
	 *       {@link FeedbackDevice#Tachometer},
	 *       {@link FeedbackDevice#PulseWidthEncodedPosition},
	 *       {@link FeedbackDevice#SensorSum},
	 *       {@link FeedbackDevice#SensorDifference},
	 *       {@link FeedbackDevice#RemoteSensor0},
	 *       {@link FeedbackDevice#RemoteSensor1},
	 *       {@link FeedbackDevice#SoftwareEmulatedSensor},
	 *       {@link FeedbackDevice#CTRE_MagEncoder_Absolute},
	 *       {@link FeedbackDevice#CTRE_MagEncoder_Relative}
	 */
	default FeedbackDevice getFeedbackDevice(String feedbackDevice) {
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
	 * Method to convert a String to its respective {@link EncodingType}.
	 * 
	 * @param encodingType
	 * @return {@link EncodingType}
	 */
	default EncodingType getEncodingType(String encodingType) {
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

}