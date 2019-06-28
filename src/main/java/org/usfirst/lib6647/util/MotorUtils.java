package org.usfirst.lib6647.util;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperVictor;

/**
 * SuperInterface for converting String values from a {@link JSONObject} to
 * configure a {@link HyperTalon} or a {@link HyperVictor}.
 */
public interface MotorUtils {

	/**
	 * Sets a given {@link HyperTalon}'s limiter value from a {@link JSONObject}.
	 * Max value is 1, min value is 0 (which would make the {@link HyperTalon}
	 * stop).
	 * 
	 * @param JSONObject
	 * @param HyperTalon
	 * @throws NullPointerException  if {@link JSONObject} key does not exist or is
	 *                               empty.
	 * @throws NumberFormatException if {@link JSONObject} is not a number.
	 */
	default void setLimiter(JSONObject json, HyperTalon talon) throws NullPointerException, NumberFormatException {
		double limiter = Double.parseDouble(json.get("limiter").toString());
		talon.setLimiter(limiter < 0.0 ? 0.0 : limiter > 1.0 ? 1.0 : limiter);
	}

	/**
	 * Sets a given {@link HyperVictor}'s limiter value from a {@link JSONObject}.
	 * Max value is 1, min value is 0 (which would make the {@link HyperVictor}
	 * stop).
	 * 
	 * @param JSONObject
	 * @param HyperVictor
	 * @throws NullPointerException  if {@link JSONObject} key does not exist or is
	 *                               empty.
	 * @throws NumberFormatException if {@link JSONObject} is not a number.
	 */
	default void setLimiter(JSONObject json, HyperVictor victor) throws NullPointerException, NumberFormatException {
		double limiter = Double.parseDouble(json.get("limiter").toString());
		victor.setLimiter(limiter < 0.0 ? 0.0 : limiter > 1.0 ? 1.0 : limiter);
	}

	/**
	 * Sets a given {@link HyperTalon}'s inverted value from a {@link JSONObject}.
	 * 
	 * @param JSONObject
	 * @param HyperTalon
	 * @throws NullPointerException if {@link JSONObject} key does not exist or is
	 *                              empty.
	 */
	default void setInverted(JSONObject json, HyperTalon HyperTalon) throws NullPointerException {
		HyperTalon.setInverted(Boolean.parseBoolean(json.get("inverted").toString()));
	}

	/**
	 * Sets a given {@link HyperVictor}'s inverted value from a {@link JSONObject}
	 * key.
	 * 
	 * @param JSONObject
	 * @param HyperVictor
	 * @throws NullPointerException if {@link JSONObject} key does not exist or is
	 *                              empty.
	 */
	default void setInverted(JSONObject json, HyperVictor HyperVictor) throws NullPointerException {
		HyperVictor.setInverted(Boolean.parseBoolean(json.get("inverted").toString()));
	}

	/**
	 * Method to convert a String value to its respective {@link NeutralMode}.
	 * 
	 * @param String
	 * @return NeutralMode
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
	 * Sets a given {@link HyperTalon}'s {@link NeutralMode} from a
	 * {@link JSONObject} key.
	 * 
	 * @param JSONObject
	 * @param HyperTalon
	 * @throws NullPointerException if {@link JSONObject} key does not exist, is
	 *                              empty, or is not valid.
	 * 
	 * @note There are three types of {@link NeutralMode NeutralModes}:
	 *       {@link NeutralMode#Coast}, {@link NeutralMode#Brake}, and
	 *       {@link NeutralMode#EEPROMSetting}. All of which should share the same
	 *       name in the {@link JSONObject}.
	 */
	default void setNeutralMode(JSONObject json, HyperTalon HyperTalon) throws NullPointerException {
		HyperTalon.setNeutralMode(getNeutralMode(json.get("neutralMode").toString()));
	}

	/**
	 * Sets a given {@link HyperVictor}'s {@link NeutralMode} from a
	 * {@link JSONObject} key.
	 * 
	 * @param JSONObject
	 * @param HyperVictor
	 * @throws NullPointerException if {@link JSONObject} key does not exist, is
	 *                              empty, or is not valid.
	 * 
	 * @note There are three types of {@link NeutralMode NeutralModes}:
	 *       {@link NeutralMode#Coast}, {@link NeutralMode#Brake}, and
	 *       {@link NeutralMode#EEPROMSetting}. All of which should share the same
	 *       name in the {@link JSONObject}.
	 */
	default void setNeutralMode(JSONObject json, HyperVictor HyperVictor) throws NullPointerException {
		HyperVictor.setNeutralMode(getNeutralMode(json.get("neutralMode").toString()));
	}

	/**
	 * Sets a given {@link HyperTalon}'s loopramp from a {@link JSONObject} key.
	 * 
	 * @param JSONObject
	 * @param HyperTalon
	 * @throws NullPointerException  if {@link JSONObject} keys do not exist or are
	 *                               empty.
	 * @throws NumberFormatException if {@link JSONObject} keys are not numbers.
	 */
	default void setLoopRamp(JSONObject json, HyperTalon HyperTalon) throws NullPointerException {
		JSONObject closed = (JSONObject) ((JSONObject) json.get("loopRamp")).get("closed"),
				open = (JSONObject) ((JSONObject) json.get("loopRamp")).get("open");

		if (closed.containsKey("timeoutMs") && open.containsKey("timeoutMs")) {
			HyperTalon.configClosedloopRamp(Double.parseDouble(closed.get("secondsFromNeutralToFull").toString()),
					Integer.parseInt(closed.get("timeoutMs").toString()));
			HyperTalon.configOpenloopRamp(Double.parseDouble(open.get("secondsFromNeutralToFull").toString()),
					Integer.parseInt(open.get("timeoutMs").toString()));
		} else {
			HyperTalon.configClosedloopRamp(Double.parseDouble(closed.get("secondsFromNeutralToFull").toString()));
			HyperTalon.configOpenloopRamp(Double.parseDouble(open.get("secondsFromNeutralToFull").toString()));
		}
	}

	/**
	 * Sets a given {@link HyperVictor}'s loopramp from the JSON configuration.
	 * 
	 * @param JSONObject
	 * @param HyperVictor
	 * @throws NullPointerException  if {@link JSONObject} keys do not exist or are
	 *                               empty.
	 * @throws NumberFormatException if {@link JSONObject} keys are not numbers.
	 */
	default void setLoopRamp(JSONObject json, HyperVictor HyperVictor) throws NullPointerException {
		JSONObject closed = (JSONObject) ((JSONObject) json.get("loopRamp")).get("closed"),
				open = (JSONObject) ((JSONObject) json.get("loopRamp")).get("open");

		if (closed.containsKey("timeoutMs") && open.containsKey("timeoutMs")) {
			HyperVictor.configClosedloopRamp(Double.parseDouble(closed.get("secondsFromNeutralToFull").toString()),
					Integer.parseInt(closed.get("timeoutMs").toString()));
			HyperVictor.configOpenloopRamp(Double.parseDouble(open.get("secondsFromNeutralToFull").toString()),
					Integer.parseInt(open.get("timeoutMs").toString()));
		} else {
			HyperVictor.configClosedloopRamp(Double.parseDouble(closed.get("secondsFromNeutralToFull").toString()));
			HyperVictor.configOpenloopRamp(Double.parseDouble(open.get("secondsFromNeutralToFull").toString()));
		}
	}

	/**
	 * Method to convert a String to its respective {@link FeedbackDevice}.
	 * 
	 * @param feedbackDevice
	 * @return FeedbackDevice
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
	 * Sets a given {@link HyperTalon}'s sensors from a {@link JSONObject} key
	 * (fairly limited in terms of configuration at the moment).
	 * 
	 * @param JSONObject
	 * @param HyperTalon
	 * @throws NullPointerException  if {@link JSONObject} keys do not exist or are
	 *                               empty.
	 * @throws NumberFormatException if some {@link JSONObject} keys are not
	 *                               numbers.
	 */
	default void setSensors(JSONObject json, HyperTalon HyperTalon) throws NullPointerException {
		JSONObject sensor = (JSONObject) json.get("sensor");
		JSONObject feedback = (JSONObject) sensor.get("feedback");

		HyperTalon.configSelectedFeedbackSensor(getFeedbackDevice(feedback.get("feedbackDevice").toString()),
				Integer.parseInt(feedback.get("pidIdx").toString()),
				Integer.parseInt(feedback.get("timeoutMs").toString()));

		HyperTalon.setSensorPhase(Boolean.parseBoolean(sensor.get("phase").toString()));

		HyperTalon.setSelectedSensorPosition(Integer.parseInt(sensor.get("sensorPos").toString()),
				Integer.parseInt(sensor.get("pidIdx").toString()),
				Integer.parseInt(sensor.get("timeoutMs").toString()));
	}

	/**
	 * Sets a given {@link HyperTalon}'s PID values from a {@link JSONObject} key.
	 * 
	 * @param JSONObject
	 * @param HyperTalon
	 * @throws NullPointerException  if {@link JSONObject} keys do not exist or are
	 *                               empty.
	 * @throws NumberFormatException if some {@link JSONObject} keys are not
	 *                               numbers.
	 */
	default void setPIDValues(JSONObject json, HyperTalon HyperTalon) throws NullPointerException {
		JSONObject pid = (JSONObject) json.get("pid");

		HyperTalon.config_kP(Integer.parseInt(pid.get("slotIdx").toString()),
				Double.parseDouble(pid.get("p").toString()));
		HyperTalon.config_kI(Integer.parseInt(pid.get("slotIdx").toString()),
				Double.parseDouble(pid.get("i").toString()));
		HyperTalon.config_kD(Integer.parseInt(pid.get("slotIdx").toString()),
				Double.parseDouble(pid.get("d").toString()));
		HyperTalon.config_kF(Integer.parseInt(pid.get("slotIdx").toString()),
				Double.parseDouble(pid.get("f").toString()));
	}
}