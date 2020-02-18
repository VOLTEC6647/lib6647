package org.usfirst.lib6647.util;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.MotorCommutation;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.fasterxml.jackson.databind.JsonNode;

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
	 * - <b>{@link NeutralMode#Coast}</b>, which is default
	 * <p>
	 * - <b>{@link NeutralMode#Brake}</b>
	 * <p>
	 * - <b>{@link NeutralMode#EEPROMSetting}</b>
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param neutralMode The desired {@link NeutralMode}, as a String value
	 * @return The {@link NeutralMode}, as a valid enum
	 */
	public static NeutralMode getNeutralMode(String neutralMode) {
		switch (neutralMode) {
			case "Brake":
				return NeutralMode.Brake;
			case "EEPROMSetting":
				return NeutralMode.EEPROMSetting;
			case "Coast":
			default:
				return NeutralMode.Coast;
		}
	}

	/**
	 * Get a {@link FeedbackDevice} value from a String.
	 * 
	 * <p>
	 * There are thirteen types of {@link FeedbackDevice FeedbackDevices}:
	 * <p>
	 * - <b>{@link FeedbackDevice#QuadEncoder}</b>
	 * <p>
	 * - <b>{@link FeedbackDevice#IntegratedSensor}</b>
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
	 * - <b>{@link FeedbackDevice#None}</b>, which is default
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
			case "IntegratedSensor":
				return FeedbackDevice.IntegratedSensor;
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
			case "None":
			default:
				return FeedbackDevice.None;
		}
	}

	/**
	 * Get a {@link RemoteFeedbackDevice} value from a String.
	 * 
	 * <p>
	 * There are seven types of {@link RemoteFeedbackDevice RemoteFeedbackDevices}:
	 * <p>
	 * - <b>{@link RemoteFeedbackDevice#SensorSum}</b>
	 * <p>
	 * - <b>{@link RemoteFeedbackDevice#SensorDifference}</b>
	 * <p>
	 * - <b>{@link RemoteFeedbackDevice#RemoteSensor0}</b>
	 * <p>
	 * - <b>{@link RemoteFeedbackDevice#RemoteSensor1}</b>
	 * <p>
	 * - <b>{@link RemoteFeedbackDevice#SoftwareEmulatedSensor}</b>
	 * <p>
	 * - <b>{@link RemoteFeedbackDevice#None}</b>, which is default
	 * <p>
	 * - <b>{@link RemoteFeedbackDevice#FactoryDefaultOff}</b>, which is deprecated
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param remoteFeedbackDevice The desired {@link RemoteFeedbackDevice}, as a
	 *                             String value
	 * @return The {@link RemoteFeedbackDevice}, as a valid enum
	 */
	public static RemoteFeedbackDevice getRemoteFeedbackDevice(String remoteFeedbackDevice) {
		switch (remoteFeedbackDevice) {
			case "SensorSum":
				return RemoteFeedbackDevice.SensorSum;
			case "SensorDifference":
				return RemoteFeedbackDevice.SensorDifference;
			case "RemoteSensor0":
				return RemoteFeedbackDevice.RemoteSensor0;
			case "RemoteSensor1":
				return RemoteFeedbackDevice.RemoteSensor1;
			case "SoftwareEmulatedSensor":
				return RemoteFeedbackDevice.SoftwareEmulatedSensor;
			case "None":
			case "FactoryDefaultOff":
			default:
				return RemoteFeedbackDevice.None;
		}
	}

	/**
	 * Get a {@link RemoteSensorSource} value from a String.
	 * 
	 * <p>
	 * There are fifteen types of {@link RemoteSensorSource RemoteSensorSources}:
	 * <p>
	 * - <b>{@link RemoteSensorSource#TalonSRX_SelectedSensor}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#TalonFX_SelectedSensor}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#Pigeon_Yaw}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#Pigeon_Pitch}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#Pigeon_Roll}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#CANifier_Quadrature}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#CANifier_PWMInput0}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#CANifier_PWMInput1}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#CANifier_PWMInput2}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#CANifier_PWMInput3}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#GadgeteerPigeon_Yaw}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#GadgeteerPigeon_Pitch}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#GadgeteerPigeon_Roll}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#CANCoder}</b>
	 * <p>
	 * - <b>{@link RemoteSensorSource#Off}</b>, which is default
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param remoteSensorSource The desired {@link RemoteSensorSource}, as a String
	 *                           value
	 * @return The {@link RemoteSensorSource}, as a valid enum
	 */
	public static RemoteSensorSource getRemoteSensorSource(String remoteSensorSource) {
		switch (remoteSensorSource) {
			case "TalonSRX_SelectedSensor":
				return RemoteSensorSource.TalonSRX_SelectedSensor;
			case "TalonFX_SelectedSensor":
				return RemoteSensorSource.TalonFX_SelectedSensor;
			case "Pigeon_Yaw":
				return RemoteSensorSource.Pigeon_Yaw;
			case "Pigeon_Pitch":
				return RemoteSensorSource.Pigeon_Pitch;
			case "Pigeon_Roll":
				return RemoteSensorSource.Pigeon_Roll;
			case "CANifier_Quadrature":
				return RemoteSensorSource.CANifier_Quadrature;
			case "CANifier_PWMInput0":
				return RemoteSensorSource.CANifier_PWMInput0;
			case "CANifier_PWMInput1":
				return RemoteSensorSource.CANifier_PWMInput1;
			case "CANifier_PWMInput2":
				return RemoteSensorSource.CANifier_PWMInput2;
			case "CANifier_PWMInput3":
				return RemoteSensorSource.CANifier_PWMInput3;
			case "GadgeteerPigeon_Yaw":
				return RemoteSensorSource.GadgeteerPigeon_Yaw;
			case "GadgeteerPigeon_Pitch":
				return RemoteSensorSource.GadgeteerPigeon_Pitch;
			case "GadgeteerPigeon_Roll":
				return RemoteSensorSource.GadgeteerPigeon_Roll;
			case "CANCoder":
				return RemoteSensorSource.CANCoder;
			case "Off":
			default:
				return RemoteSensorSource.Off;
		}
	}

	/**
	 * Get a {@link VelocityMeasPeriod} value from a String.
	 * 
	 * <p>
	 * There are eight types of {@link VelocityMeasPeriod VelocityMeasPeriods}:
	 * <p>
	 * - <b>{@link VelocityMeasPeriod#Period_1Ms}</b>
	 * <p>
	 * - <b>{@link VelocityMeasPeriod#Period_2Ms}</b>
	 * <p>
	 * - <b>{@link VelocityMeasPeriod#Period_5Ms}</b>
	 * <p>
	 * - <b>{@link VelocityMeasPeriod#Period_10Ms}</b>
	 * <p>
	 * - <b>{@link VelocityMeasPeriod#Period_20Ms}</b>
	 * <p>
	 * - <b>{@link VelocityMeasPeriod#Period_25Ms}</b>
	 * <p>
	 * - <b>{@link VelocityMeasPeriod#Period_50Ms}</b>
	 * <p>
	 * - <b>{@link VelocityMeasPeriod#Period_100Ms}</b>, which is default
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param velocityMeasurementPeriod The desired {@link VelocityMeasPeriod}, as a
	 *                                  String value
	 * @return The {@link VelocityMeasPeriod}, as a valid enum
	 */
	public static VelocityMeasPeriod getVelocityMeasurementPeriod(String velocityMeasurementPeriod) {
		switch (velocityMeasurementPeriod) {
			case "Period_1Ms":
				return VelocityMeasPeriod.Period_1Ms;
			case "Period_2Ms":
				return VelocityMeasPeriod.Period_2Ms;
			case "Period_5Ms":
				return VelocityMeasPeriod.Period_5Ms;
			case "Period_10Ms":
				return VelocityMeasPeriod.Period_10Ms;
			case "Period_20Ms":
				return VelocityMeasPeriod.Period_20Ms;
			case "Period_25Ms":
				return VelocityMeasPeriod.Period_25Ms;
			case "Period_50Ms":
				return VelocityMeasPeriod.Period_50Ms;
			case "Period_100Ms":
			default:
				return VelocityMeasPeriod.Period_100Ms;
		}
	}

	/**
	 * Get a {@link LimitSwitchSource} value from a String.
	 * 
	 * <p>
	 * There are four types of {@link LimitSwitchSource LimitSwitchSources}:
	 * <p>
	 * - <b>{@link LimitSwitchSource#FeedbackConnector}</b>
	 * <p>
	 * - <b>{@link LimitSwitchSource#RemoteTalonSRX}</b>
	 * <p>
	 * - <b>{@link LimitSwitchSource#RemoteTalonSRX}</b>
	 * <p>
	 * - <b>{@link LimitSwitchSource#Deactivated}</b>, which is default
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param limitSwitchSource The desired {@link LimitSwitchSource}, as a String
	 *                          value
	 * @return The {@link LimitSwitchSource}, as a valid enum
	 */
	public static LimitSwitchSource getLimitSwitchSource(String limitSwitchSource) {
		switch (limitSwitchSource) {
			case "FeedbackConnector":
				return LimitSwitchSource.FeedbackConnector;
			case "RemoteTalonSRX":
				return LimitSwitchSource.RemoteTalonSRX;
			case "RemoteCANifier":
				return LimitSwitchSource.RemoteCANifier;
			case "Deactivated":
			default:
				return LimitSwitchSource.Deactivated;
		}
	}

	/**
	 * Get a {@link RemoteLimitSwitchSource} value from a String.
	 * 
	 * <p>
	 * There are three types of {@link RemoteLimitSwitchSource
	 * RemoteLimitSwitchSources}:
	 * <p>
	 * - <b>{@link RemoteLimitSwitchSource#RemoteTalonSRX}</b>
	 * <p>
	 * - <b>{@link RemoteLimitSwitchSource#RemoteTalonSRX}</b>
	 * <p>
	 * - <b>{@link RemoteLimitSwitchSource#Deactivated}</b>, which is default
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param remoteLimitSwitchSource The desired {@link RemoteLimitSwitchSource},
	 *                                as a String value
	 * @return The {@link RemoteLimitSwitchSource}, as a valid enum
	 */
	public static RemoteLimitSwitchSource getRemoteLimitSwitchSource(String remoteLimitSwitchSource) {
		switch (remoteLimitSwitchSource) {
			case "RemoteTalonSRX":
				return RemoteLimitSwitchSource.RemoteTalonSRX;
			case "RemoteCANifier":
				return RemoteLimitSwitchSource.RemoteCANifier;
			case "Deactivated":
			default:
				return RemoteLimitSwitchSource.Deactivated;
		}
	}

	/**
	 * Get a {@link LimitSwitchNormal} value from a String.
	 * 
	 * <p>
	 * There are three types of {@link LimitSwitchNormal LimitSwitchNormals}:
	 * <p>
	 * - <b>{@link LimitSwitchNormal#NormallyOpen}</b>
	 * <p>
	 * - <b>{@link LimitSwitchNormal#NormallyClosed}</b>
	 * <p>
	 * - <b>{@link LimitSwitchNormal#Disabled}</b>, which is default
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param limitSwitchNormal The desired {@link LimitSwitchNormal}, as a String
	 *                          value
	 * @return The {@link LimitSwitchNormal}, as a valid enum
	 */
	public static LimitSwitchNormal getLimitSwitchNormal(String limitSwitchNormal) {
		switch (limitSwitchNormal) {
			case "NormallyOpen":
				return LimitSwitchNormal.NormallyOpen;
			case "NormallyClosed":
				return LimitSwitchNormal.NormallyClosed;
			case "Disabled":
			default:
				return LimitSwitchNormal.Disabled;
		}
	}

	/**
	 * Get a {@link MotorCommutation} value from a String.
	 * 
	 * <p>
	 * There is currently one type of {@link MotorCommutation}:
	 * <p>
	 * - <b>{@link MotorCommutation#Trapezoidal}</b>, which is default
	 * <p>
	 * It must share the same name in the {@link JsonNode}.
	 * 
	 * @param motorCommutation The desired {@link MotorCommutation}, as a String
	 *                         value
	 * @return The {@link MotorCommutation}, as a valid enum
	 */
	public static MotorCommutation getMotorCommutation(String motorCommutation) {
		switch (motorCommutation) {
			case "Trapezoidal":
			default:
				return MotorCommutation.Trapezoidal;
		}
	}

	/**
	 * Get an {@link AbsoluteSensorRange} value from a String.
	 * 
	 * <p>
	 * There are two types of {@link AbsoluteSensorRange AbsoluteSensorRanges}:
	 * <p>
	 * - <b>{@link AbsoluteSensorRange#Signed_PlusMinus180}</b>
	 * <p>
	 * - <b>{@link AbsoluteSensorRange#Unsigned_0_to_360}</b>, which is default
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param absoluteSensorRange The desired {@link AbsoluteSensorRange}, as a
	 *                            String value
	 * @return The {@link AbsoluteSensorRange}, as a valid enum
	 */
	public static AbsoluteSensorRange getAbsoluteSensorRange(String absoluteSensorRange) {
		switch (absoluteSensorRange) {
			case "Signed_PlusMinus180":
				return AbsoluteSensorRange.Signed_PlusMinus180;
			case "Unsigned_0_to_360":
			default:
				return AbsoluteSensorRange.Unsigned_0_to_360;
		}
	}

	/**
	 * Get an {@link SensorInitializationStrategy} value from a String.
	 * 
	 * <p>
	 * There are two types of {@link SensorInitializationStrategy
	 * SensorInitializationStrategies}:
	 * <p>
	 * - <b>{@link SensorInitializationStrategy#BootToAbsolutePosition}</b>
	 * <p>
	 * - <b>{@link SensorInitializationStrategy#BootToZero}</b>, which is default
	 * <p>
	 * All of which must share the same name in the {@link JsonNode}.
	 * 
	 * @param sensorInitializationStrategy The desired
	 *                                     {@link SensorInitializationStrategy}, as
	 *                                     a String value
	 * @return The {@link SensorInitializationStrategy}, as a valid enum
	 */
	public static SensorInitializationStrategy getSensorInitializationStrategy(String sensorInitializationStrategy) {
		switch (sensorInitializationStrategy) {
			case "BootToAbsolutePosition":
				return SensorInitializationStrategy.BootToAbsolutePosition;
			case "BootToZero":
			default:
				return SensorInitializationStrategy.BootToZero;
		}
	}

	/**
	 * This class must not be instantiated.
	 */
	private MotorUtil() {
		throw new AssertionError("utility class");
	}
}