package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.util.MotorUtil;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperTalon} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link HyperTalon HyperTalon objects}
 * declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperTalon {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperTalon} instances.
	 */
	final HashMap<String, HyperTalon> talons = new HashMap<>();

	/**
	 * Method to initialize {@link HyperTalon HyperTalon objects} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #talons} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initTalons(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("talons").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !talons.containsKey(json.get("name").asText())
						&& json.hasNonNull("port")) {
					// Read values from JsonNode.
					var port = json.get("port").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (port < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR TALON '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create HyperTalon object.
					var talon = new HyperTalon(json.get("port").asInt());
					var talonConfig = new TalonSRXConfiguration();

					// Additional initialization configuration.
					if (json.hasNonNull("config")) {
						var config = json.get("config");

						talonConfig.customParam0 = config.hasNonNull("customParam0")
								? config.get("customParam0").asInt(0)
								: 0;
						talonConfig.customParam1 = config.hasNonNull("customParam1")
								? config.get("customParam1").asInt(0)
								: 0;
						talonConfig.enableOptimizations = config.hasNonNull("enableOptimizations")
								? config.get("enableOptimizations").asBoolean(true)
								: true;

						if (config.hasNonNull("slot0")) {
							var slot = config.get("slot0");

							talonConfig.slot0.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							talonConfig.slot0.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							talonConfig.slot0.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							talonConfig.slot0.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							talonConfig.slot0.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							talonConfig.slot0.integralZone = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							talonConfig.slot0.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							talonConfig.slot0.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							talonConfig.slot0.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}
						if (config.hasNonNull("slot1")) {
							var slot = config.get("slot1");

							talonConfig.slot1.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							talonConfig.slot1.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							talonConfig.slot1.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							talonConfig.slot1.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							talonConfig.slot1.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							talonConfig.slot1.integralZone = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							talonConfig.slot1.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							talonConfig.slot1.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							talonConfig.slot1.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}
						if (config.hasNonNull("slot2")) {
							var slot = config.get("slot2");

							talonConfig.slot2.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							talonConfig.slot2.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							talonConfig.slot2.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							talonConfig.slot2.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							talonConfig.slot2.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							talonConfig.slot2.integralZone = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							talonConfig.slot2.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							talonConfig.slot2.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							talonConfig.slot2.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}
						if (config.hasNonNull("slot3")) {
							var slot = config.get("slot3");

							talonConfig.slot3.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							talonConfig.slot3.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							talonConfig.slot3.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							talonConfig.slot3.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							talonConfig.slot3.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							talonConfig.slot3.integralZone = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							talonConfig.slot3.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							talonConfig.slot3.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							talonConfig.slot3.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}

						if (config.hasNonNull("remoteSensor")) {
							var remoteSensor = config.get("remoteSensor");

							if (remoteSensor.hasNonNull("remoteFilter0")) {
								var remoteFilter = remoteSensor.get("remoteFilter0");

								talonConfig.remoteFilter0.remoteSensorDeviceID = remoteFilter.hasNonNull(
										"remoteSensorDeviceID") ? remoteFilter.get("remoteSensorDeviceID").asInt() : 0;
								talonConfig.remoteFilter0.remoteSensorSource = remoteFilter
										.hasNonNull("remoteSensorSource")
												? MotorUtil.getRemoteSensorSource(
														remoteFilter.get("remoteSensorSource").asText())
												: RemoteSensorSource.Off;
							}
							if (remoteSensor.hasNonNull("remoteFilter1")) {
								var remoteFilter = remoteSensor.get("remoteFilter1");

								talonConfig.remoteFilter1.remoteSensorDeviceID = remoteFilter.hasNonNull(
										"remoteSensorDeviceID") ? remoteFilter.get("remoteSensorDeviceID").asInt() : 0;
								talonConfig.remoteFilter1.remoteSensorSource = remoteFilter
										.hasNonNull("remoteSensorSource")
												? MotorUtil.getRemoteSensorSource(
														remoteFilter.get("remoteSensorSource").asText())
												: RemoteSensorSource.Off;
							}

							talonConfig.remoteSensorClosedLoopDisableNeutralOnLOS = remoteSensor
									.hasNonNull("remoteSensorClosedLoopDisableNeutralOnLOS")
											? remoteSensor.get("remoteSensorClosedLoopDisableNeutralOnLOS").asBoolean()
											: false;
						}

						if (config.hasNonNull("loopRamp")) {
							var loopRamp = config.get("loopRamp");

							talonConfig.openloopRamp = loopRamp.hasNonNull("openloopRamp")
									? loopRamp.get("openloopRamp").asDouble()
									: 0.0;
							talonConfig.closedloopRamp = loopRamp.hasNonNull("closedloopRamp")
									? loopRamp.get("closedloopRamp").asDouble()
									: 0.0;
						}

						if (config.hasNonNull("output")) {
							var output = config.get("output");

							if (output.hasNonNull("peakOutput")) {
								var peakOutput = output.get("peakOutput");

								talonConfig.peakOutputForward = peakOutput.hasNonNull("peakOutputForward")
										? peakOutput.get("peakOutputForward").asDouble(1.0)
										: 1.0;
								talonConfig.peakOutputReverse = peakOutput.hasNonNull("peakOutputReverse")
										? peakOutput.get("peakOutputReverse").asDouble(-1.0)
										: -1.0;
							}

							if (output.hasNonNull("nominalOutput")) {
								var nominalOutput = config.get("nominalOutput");

								talonConfig.nominalOutputForward = nominalOutput.hasNonNull("nominalOutputForward")
										? nominalOutput.get("nominalOutputForward").asDouble()
										: 0.0;
								talonConfig.nominalOutputReverse = nominalOutput.hasNonNull("nominalOutputReverse")
										? nominalOutput.get("nominalOutputReverse").asDouble()
										: 0.0;
							}

							talonConfig.neutralDeadband = output.hasNonNull("neutralDeadband")
									? output.get("neutralDeadband").asDouble(0.04)
									: 0.04;
						}

						if (config.hasNonNull("voltage")) {
							var voltage = config.get("voltage");

							talonConfig.voltageCompSaturation = voltage.hasNonNull("voltageCompSaturation")
									? voltage.get("voltageCompSaturation").asDouble()
									: 0.0;
							talonConfig.voltageMeasurementFilter = voltage.hasNonNull("voltageMeasurementFilter")
									? voltage.get("voltageMeasurementFilter").asInt(32)
									: 32;
							talonConfig.velocityMeasurementPeriod = voltage.hasNonNull("velocityMeasurementPeriod")
									? MotorUtil.getVelocityMeasurementPeriod(
											voltage.get("velocityMeasurementPeriod").asText())
									: VelocityMeasPeriod.Period_100Ms;
							talonConfig.velocityMeasurementWindow = voltage.hasNonNull("velocityMeasurementWindow")
									? voltage.get("velocityMeasurementWindow").asInt(64)
									: 64;
						}

						if (config.hasNonNull("softLimit")) {
							var softLimit = config.get("softLimit");

							talonConfig.forwardSoftLimitThreshold = softLimit.hasNonNull("forwardSoftLimitThreshold")
									? softLimit.get("forwardSoftLimitThreshold").asInt()
									: 0;
							talonConfig.reverseSoftLimitThreshold = softLimit.hasNonNull("reverseSoftLimitThreshold")
									? softLimit.get("reverseSoftLimitThreshold").asInt()
									: 0;
							talonConfig.forwardSoftLimitEnable = softLimit.hasNonNull("forwardSoftLimitEnable")
									? softLimit.get("forwardSoftLimitEnable").asBoolean()
									: false;
							talonConfig.reverseSoftLimitEnable = softLimit.hasNonNull("reverseSoftLimitEnable")
									? softLimit.get("reverseSoftLimitEnable").asBoolean()
									: false;
							talonConfig.softLimitDisableNeutralOnLOS = softLimit
									.hasNonNull("softLimitDisableNeutralOnLOS")
											? softLimit.get("softLimitDisableNeutralOnLOS").asBoolean()
											: false;
						}

						talonConfig.auxPIDPolarity = config.hasNonNull("auxPIDPolarity")
								? config.get("auxPIDPolarity").asBoolean()
								: false;

						if (config.hasNonNull("motion")) {
							var motion = config.get("motion");

							talonConfig.motionCruiseVelocity = motion.hasNonNull("motionCruiseVelocity")
									? motion.get("motionCruiseVelocity").asInt()
									: 0;
							talonConfig.motionAcceleration = motion.hasNonNull("motionAcceleration")
									? motion.get("motionAcceleration").asInt()
									: 0;
							talonConfig.motionCurveStrength = motion.hasNonNull("motionCurveStrength")
									? motion.get("motionCurveStrength").asInt()
									: 0;
							talonConfig.motionProfileTrajectoryPeriod = motion
									.hasNonNull("motionProfileTrajectoryPeriod")
											? motion.get("motionProfileTrajectoryPeriod").asInt()
											: 0;
						}

						talonConfig.feedbackNotContinuous = config.hasNonNull("feedbackNotContinuous")
								? config.get("feedbackNotContinuous").asBoolean()
								: false;

						if (config.hasNonNull("clear")) {
							var clear = config.get("clear");

							talonConfig.clearPositionOnLimitF = clear.hasNonNull("clearPositionOnLimitF")
									? clear.get("clearPositionOnLimitF").asBoolean()
									: false;
							talonConfig.clearPositionOnLimitR = clear.hasNonNull("clearPositionOnLimitR")
									? clear.get("clearPositionOnLimitR").asBoolean()
									: false;
							talonConfig.clearPositionOnQuadIdx = clear.hasNonNull("clearPositionOnQuadIdx")
									? clear.get("clearPositionOnQuadIdx").asBoolean()
									: false;
						}

						if (config.hasNonNull("pulseWithPeriod")) {
							var pulseWithPeriod = config.get("pulseWithPeriod");

							talonConfig.pulseWidthPeriod_EdgesPerRot = pulseWithPeriod
									.hasNonNull("pulseWidthPeriod_EdgesPerRot")
											? pulseWithPeriod.get("pulseWidthPeriod_EdgesPerRot").asInt(1)
											: 1;
							talonConfig.pulseWidthPeriod_FilterWindowSz = pulseWithPeriod
									.hasNonNull("pulseWidthPeriod_FilterWindowSz")
											? pulseWithPeriod.get("pulseWidthPeriod_FilterWindowSz").asInt(1)
											: 1;
						}

						talonConfig.trajectoryInterpolationEnable = config.hasNonNull("trajectoryInterpolationEnable")
								? config.get("trajectoryInterpolationEnable").asBoolean(true)
								: true;

						if (config.hasNonNull("pid")) {
							var pid = config.get("pid");

							if (pid.hasNonNull("primary")) {
								var primary = pid.get("primary");

								talonConfig.primaryPID.selectedFeedbackCoefficient = primary
										.hasNonNull("selectedFeedbackCoefficient")
												? primary.get("selectedFeedbackCoefficient").asDouble(1.0)
												: 1.0;
								talonConfig.primaryPID.selectedFeedbackSensor = primary
										.hasNonNull("selectedFeedbackSensor")
												? MotorUtil.getFeedbackDevice(
														primary.get("selectedFeedbackSensor").asText("QuadEncoder"))
												: FeedbackDevice.QuadEncoder;
							}
							if (pid.hasNonNull("auxiliary")) {
								var auxiliary = pid.get("auxiliary");

								talonConfig.auxiliaryPID.selectedFeedbackCoefficient = auxiliary
										.hasNonNull("selectedFeedbackCoefficient")
												? auxiliary.get("selectedFeedbackCoefficient").asDouble(1.0)
												: 1.0;
								talonConfig.auxiliaryPID.selectedFeedbackSensor = auxiliary
										.hasNonNull("selectedFeedbackSensor")
												? MotorUtil.getFeedbackDevice(
														auxiliary.get("selectedFeedbackSensor").asText("QuadEncoder"))
												: FeedbackDevice.QuadEncoder;
							}
						}

						if (config.hasNonNull("limitSwitch")) {
							var limitSwitch = config.get("limitSwitch");

							if (limitSwitch.hasNonNull("forward")) {
								var forward = limitSwitch.get("forward");

								talonConfig.forwardLimitSwitchSource = forward.hasNonNull("forwardLimitSwitchSource")
										? MotorUtil.getLimitSwitchSource(
												forward.get("forwardLimitSwitchSource").asText("FeedbackConnector"))
										: LimitSwitchSource.FeedbackConnector;
								talonConfig.forwardLimitSwitchDeviceID = forward
										.hasNonNull("forwardLimitSwitchDeviceID")
												? forward.get("forwardLimitSwitchDeviceID").asInt()
												: 0;
								talonConfig.forwardLimitSwitchNormal = forward.hasNonNull("forwardLimitSwitchNormal")
										? MotorUtil.getLimitSwitchNormal(
												forward.get("forwardLimitSwitchNormal").asText("NormallyOpen"))
										: LimitSwitchNormal.NormallyOpen;
							}
							if (limitSwitch.hasNonNull("reverse")) {
								var reverse = limitSwitch.get("reverse");

								talonConfig.reverseLimitSwitchSource = reverse.hasNonNull("reverseLimitSwitchSource")
										? MotorUtil.getLimitSwitchSource(
												reverse.get("reverseLimitSwitchSource").asText("FeedbackConnector"))
										: LimitSwitchSource.FeedbackConnector;
								talonConfig.reverseLimitSwitchDeviceID = reverse
										.hasNonNull("reverseLimitSwitchDeviceID")
												? reverse.get("reverseLimitSwitchDeviceID").asInt()
												: 0;
								talonConfig.reverseLimitSwitchNormal = reverse.hasNonNull("reverseLimitSwitchNormal")
										? MotorUtil.getLimitSwitchNormal(
												reverse.get("reverseLimitSwitchNormal").asText("NormallyOpen"))
										: LimitSwitchNormal.NormallyOpen;
							}

							talonConfig.limitSwitchDisableNeutralOnLOS = limitSwitch
									.hasNonNull("limitSwitchDisableNeutralOnLOS")
											? limitSwitch.get("limitSwitchDisableNeutralOnLOS").asBoolean()
											: false;
						}

						if (config.hasNonNull("sumDiff")) {
							var sumDiff = config.get("sumDiff");

							talonConfig.sum0Term = sumDiff.hasNonNull("sum0Term")
									? MotorUtil.getFeedbackDevice(sumDiff.get("sum0Term").asText("QuadEncoder"))
									: FeedbackDevice.QuadEncoder;
							talonConfig.sum1Term = sumDiff.hasNonNull("sum1Term")
									? MotorUtil.getFeedbackDevice(sumDiff.get("sum1Term").asText("QuadEncoder"))
									: FeedbackDevice.QuadEncoder;
							talonConfig.diff0Term = sumDiff.hasNonNull("diff0Term")
									? MotorUtil.getFeedbackDevice(sumDiff.get("diff0Term").asText("QuadEncoder"))
									: FeedbackDevice.QuadEncoder;
							talonConfig.diff1Term = sumDiff.hasNonNull("diff1Term")
									? MotorUtil.getFeedbackDevice(sumDiff.get("diff1Term").asText("QuadEncoder"))
									: FeedbackDevice.QuadEncoder;
						}

						if (config.hasNonNull("current")) {
							var current = config.get("current");

							talonConfig.peakCurrentLimit = current.hasNonNull("peakCurrentLimit")
									? current.get("peakCurrentLimit").asInt(1)
									: 1;
							talonConfig.peakCurrentDuration = current.hasNonNull("peakCurrentDuration")
									? current.get("peakCurrentDuration").asInt(1)
									: 1;
							talonConfig.continuousCurrentLimit = current.hasNonNull("continuousCurrentLimit")
									? current.get("continuousCurrentLimit").asInt(1)
									: 1;
						}
					}

					talon.configAllSettings(talonConfig);
					talon.setName(json.get("name").asText());

					var limiter = json.hasNonNull("limiter") ? json.get("limiter").asDouble(1.0) : 1.0;
					talon.setLimiter(limiter < 0.0 ? 0.0 : limiter > 1.0 ? 1.0 : limiter);

					talon.setNeutralMode(
							json.hasNonNull("neutralMode") ? MotorUtil.getNeutralMode(json.get("neutralMode").asText())
									: NeutralMode.Coast);
					talon.setInverted(json.hasNonNull("inverted") ? json.get("inverted").asBoolean() : false);

					if (json.hasNonNull("sensors")) {
						var sensors = json.get("sensors");

						talon.setSensorPhase(sensors.hasNonNull("phase") ? sensors.get("phase").asBoolean() : false);

						if (sensors.hasNonNull("primary")) {
							var primary = sensors.get("primary");

							talon.configSelectedFeedbackSensor(
									primary.hasNonNull("feedbackDevice")
											? MotorUtil.getFeedbackDevice(primary.get("feedbackDevice").asText("None"))
											: FeedbackDevice.None,
									0, primary.hasNonNull("timeoutMs") ? primary.get("timeoutMs").asInt(0) : 0);
							talon.setSelectedSensorPosition(
									primary.hasNonNull("sensorPos") ? primary.get("sensorPos").asInt() : 0, 0,
									primary.hasNonNull("timeoutMs") ? primary.get("timeoutMs").asInt(0) : 0);
						}
						if (sensors.hasNonNull("auxiliary")) {
							var auxiliary = sensors.get("auxiliary");

							talon.configSelectedFeedbackSensor(
									auxiliary.hasNonNull("feedbackDevice")
											? MotorUtil
													.getFeedbackDevice(auxiliary.get("feedbackDevice").asText("None"))
											: FeedbackDevice.None,
									1, auxiliary.hasNonNull("timeoutMs") ? auxiliary.get("timeoutMs").asInt(0) : 0);
							talon.setSelectedSensorPosition(
									auxiliary.hasNonNull("sensorPos") ? auxiliary.get("sensorPos").asInt() : 0, 1,
									auxiliary.hasNonNull("timeoutMs") ? auxiliary.get("timeoutMs").asInt(0) : 0);
						}
					}

					talon.stopMotor();
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					talons.put(json.get("name").asText(), talon);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY TALON ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link HyperTalon} from the {@link #talons} HashMap.
	 * 
	 * @param talonName The name of the {@link HyperTalon}
	 * @return The requested {@link HyperTalon}, if found
	 */
	default HyperTalon getTalon(String talonName) {
		return talons.get(talonName);
	}
}