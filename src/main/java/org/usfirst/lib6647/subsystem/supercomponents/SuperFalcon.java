package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.MotorCommutation;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.ctre.phoenix.sensors.SensorInitializationStrategy;
import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperFalcon;
import org.usfirst.lib6647.util.CTREUtil;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperFalcon} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link HyperFalcon HyperFalcon objects}
 * declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperFalcon {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperFalcon} instances.
	 */
	final Map<String, HyperFalcon> falcons = new HashMap<>();

	/**
	 * Method to initialize {@link HyperFalcon HyperFalcon objects} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #falcons} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initFalcons(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("falcons").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !falcons.containsKey(json.get("name").asText())
						&& json.hasNonNull("port")) {
					// Read values from JsonNode.
					var port = json.get("port").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (port < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR FALCON '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create HyperFalcon and TalonFXConfiguration objects.
					var falcon = new HyperFalcon(json.get("name").asText(), json.get("port").asInt());
					var falconConfig = new TalonFXConfiguration();

					// Additional initialization & configuration.
					if (json.hasNonNull("config")) {
						var config = json.get("config");

						falconConfig.customParam0 = config.hasNonNull("customParam0")
								? config.get("customParam0").asInt(0)
								: 0;
						falconConfig.customParam1 = config.hasNonNull("customParam1")
								? config.get("customParam1").asInt(0)
								: 0;
						falconConfig.enableOptimizations = config.hasNonNull("enableOptimizations")
								? config.get("enableOptimizations").asBoolean(true)
								: true;

						if (config.hasNonNull("slot0")) {
							var slot = config.get("slot0");

							falconConfig.slot0.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							falconConfig.slot0.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							falconConfig.slot0.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							falconConfig.slot0.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							falconConfig.slot0.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							falconConfig.slot0.allowableClosedloopError = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							falconConfig.slot0.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							falconConfig.slot0.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							falconConfig.slot0.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}
						if (config.hasNonNull("slot1")) {
							var slot = config.get("slot1");

							falconConfig.slot1.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							falconConfig.slot1.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							falconConfig.slot1.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							falconConfig.slot1.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							falconConfig.slot1.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							falconConfig.slot1.allowableClosedloopError = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							falconConfig.slot1.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							falconConfig.slot1.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							falconConfig.slot1.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}
						if (config.hasNonNull("slot2")) {
							var slot = config.get("slot2");

							falconConfig.slot2.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							falconConfig.slot2.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							falconConfig.slot2.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							falconConfig.slot2.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							falconConfig.slot2.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							falconConfig.slot2.allowableClosedloopError = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							falconConfig.slot2.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							falconConfig.slot2.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							falconConfig.slot2.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}
						if (config.hasNonNull("slot3")) {
							var slot = config.get("slot3");

							falconConfig.slot3.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							falconConfig.slot3.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							falconConfig.slot3.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							falconConfig.slot3.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							falconConfig.slot3.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							falconConfig.slot3.allowableClosedloopError = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							falconConfig.slot3.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							falconConfig.slot3.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							falconConfig.slot3.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}

						if (config.hasNonNull("remoteSensor")) {
							var remoteSensor = config.get("remoteSensor");

							if (remoteSensor.hasNonNull("remoteFilter0")) {
								var remoteFilter = remoteSensor.get("remoteFilter0");

								falconConfig.remoteFilter0.remoteSensorDeviceID = remoteFilter.hasNonNull(
										"remoteSensorDeviceID") ? remoteFilter.get("remoteSensorDeviceID").asInt() : 0;
								falconConfig.remoteFilter0.remoteSensorSource = remoteFilter
										.hasNonNull("remoteSensorSource")
												? CTREUtil.getRemoteSensorSource(
														remoteFilter.get("remoteSensorSource").asText())
												: RemoteSensorSource.Off;
							}
							if (remoteSensor.hasNonNull("remoteFilter1")) {
								var remoteFilter = remoteSensor.get("remoteFilter1");

								falconConfig.remoteFilter1.remoteSensorDeviceID = remoteFilter.hasNonNull(
										"remoteSensorDeviceID") ? remoteFilter.get("remoteSensorDeviceID").asInt() : 0;
								falconConfig.remoteFilter1.remoteSensorSource = remoteFilter
										.hasNonNull("remoteSensorSource")
												? CTREUtil.getRemoteSensorSource(
														remoteFilter.get("remoteSensorSource").asText())
												: RemoteSensorSource.Off;
							}

							falconConfig.remoteSensorClosedLoopDisableNeutralOnLOS = remoteSensor
									.hasNonNull("remoteSensorClosedLoopDisableNeutralOnLOS")
											? remoteSensor.get("remoteSensorClosedLoopDisableNeutralOnLOS").asBoolean()
											: false;
						}

						if (config.hasNonNull("loopRamp")) {
							var loopRamp = config.get("loopRamp");

							falconConfig.openloopRamp = loopRamp.hasNonNull("openloopRamp")
									? loopRamp.get("openloopRamp").asDouble()
									: 0.0;
							falconConfig.closedloopRamp = loopRamp.hasNonNull("closedloopRamp")
									? loopRamp.get("closedloopRamp").asDouble()
									: 0.0;
						}

						if (config.hasNonNull("output")) {
							var output = config.get("output");

							if (output.hasNonNull("peakOutput")) {
								var peakOutput = output.get("peakOutput");

								falconConfig.peakOutputForward = peakOutput.hasNonNull("peakOutputForward")
										? peakOutput.get("peakOutputForward").asDouble(1.0)
										: 1.0;
								falconConfig.peakOutputReverse = peakOutput.hasNonNull("peakOutputReverse")
										? peakOutput.get("peakOutputReverse").asDouble(-1.0)
										: -1.0;
							}

							if (output.hasNonNull("nominalOutput")) {
								var nominalOutput = config.get("nominalOutput");

								falconConfig.nominalOutputForward = nominalOutput.hasNonNull("nominalOutputForward")
										? nominalOutput.get("nominalOutputForward").asDouble()
										: 0.0;
								falconConfig.nominalOutputReverse = nominalOutput.hasNonNull("nominalOutputReverse")
										? nominalOutput.get("nominalOutputReverse").asDouble()
										: 0.0;
							}

							falconConfig.neutralDeadband = output.hasNonNull("neutralDeadband")
									? output.get("neutralDeadband").asDouble(0.04)
									: 0.04;
						}

						if (config.hasNonNull("voltage")) {
							var voltage = config.get("voltage");

							falconConfig.voltageCompSaturation = voltage.hasNonNull("voltageCompSaturation")
									? voltage.get("voltageCompSaturation").asDouble()
									: 0.0;
							falconConfig.voltageMeasurementFilter = voltage.hasNonNull("voltageMeasurementFilter")
									? voltage.get("voltageMeasurementFilter").asInt(32)
									: 32;
							falconConfig.velocityMeasurementPeriod = voltage.hasNonNull("velocityMeasurementPeriod")
									? CTREUtil.getVelocityMeasurementPeriod(
											voltage.get("velocityMeasurementPeriod").asText())
									: VelocityMeasPeriod.Period_100Ms;
							falconConfig.velocityMeasurementWindow = voltage.hasNonNull("velocityMeasurementWindow")
									? voltage.get("velocityMeasurementWindow").asInt(64)
									: 64;
						}

						if (config.hasNonNull("softLimit")) {
							var softLimit = config.get("softLimit");

							falconConfig.forwardSoftLimitThreshold = softLimit.hasNonNull("forwardSoftLimitThreshold")
									? softLimit.get("forwardSoftLimitThreshold").asInt()
									: 0;
							falconConfig.reverseSoftLimitThreshold = softLimit.hasNonNull("reverseSoftLimitThreshold")
									? softLimit.get("reverseSoftLimitThreshold").asInt()
									: 0;
							falconConfig.forwardSoftLimitEnable = softLimit.hasNonNull("forwardSoftLimitEnable")
									? softLimit.get("forwardSoftLimitEnable").asBoolean()
									: false;
							falconConfig.reverseSoftLimitEnable = softLimit.hasNonNull("reverseSoftLimitEnable")
									? softLimit.get("reverseSoftLimitEnable").asBoolean()
									: false;
							falconConfig.softLimitDisableNeutralOnLOS = softLimit
									.hasNonNull("softLimitDisableNeutralOnLOS")
											? softLimit.get("softLimitDisableNeutralOnLOS").asBoolean()
											: false;
						}

						falconConfig.auxPIDPolarity = config.hasNonNull("auxPIDPolarity")
								? config.get("auxPIDPolarity").asBoolean()
								: false;

						if (config.hasNonNull("motion")) {
							var motion = config.get("motion");

							falconConfig.motionCruiseVelocity = motion.hasNonNull("motionCruiseVelocity")
									? motion.get("motionCruiseVelocity").asInt()
									: 0;
							falconConfig.motionAcceleration = motion.hasNonNull("motionAcceleration")
									? motion.get("motionAcceleration").asInt()
									: 0;
							falconConfig.motionCurveStrength = motion.hasNonNull("motionCurveStrength")
									? motion.get("motionCurveStrength").asInt()
									: 0;
							falconConfig.motionProfileTrajectoryPeriod = motion
									.hasNonNull("motionProfileTrajectoryPeriod")
											? motion.get("motionProfileTrajectoryPeriod").asInt()
											: 0;
						}

						falconConfig.feedbackNotContinuous = config.hasNonNull("feedbackNotContinuous")
								? config.get("feedbackNotContinuous").asBoolean()
								: false;

						if (config.hasNonNull("clear")) {
							var clear = config.get("clear");

							falconConfig.clearPositionOnLimitF = clear.hasNonNull("clearPositionOnLimitF")
									? clear.get("clearPositionOnLimitF").asBoolean()
									: false;
							falconConfig.clearPositionOnLimitR = clear.hasNonNull("clearPositionOnLimitR")
									? clear.get("clearPositionOnLimitR").asBoolean()
									: false;
							falconConfig.clearPositionOnQuadIdx = clear.hasNonNull("clearPositionOnQuadIdx")
									? clear.get("clearPositionOnQuadIdx").asBoolean()
									: false;
						}

						if (config.hasNonNull("pulseWithPeriod")) {
							var pulseWithPeriod = config.get("pulseWithPeriod");

							falconConfig.pulseWidthPeriod_EdgesPerRot = pulseWithPeriod
									.hasNonNull("pulseWidthPeriod_EdgesPerRot")
											? pulseWithPeriod.get("pulseWidthPeriod_EdgesPerRot").asInt(1)
											: 1;
							falconConfig.pulseWidthPeriod_FilterWindowSz = pulseWithPeriod
									.hasNonNull("pulseWidthPeriod_FilterWindowSz")
											? pulseWithPeriod.get("pulseWidthPeriod_FilterWindowSz").asInt(1)
											: 1;
						}

						falconConfig.trajectoryInterpolationEnable = config.hasNonNull("trajectoryInterpolationEnable")
								? config.get("trajectoryInterpolationEnable").asBoolean(true)
								: true;

						if (config.hasNonNull("pid")) {
							var pid = config.get("pid");

							if (pid.hasNonNull("primary")) {
								var primary = pid.get("primary");

								falconConfig.primaryPID.selectedFeedbackCoefficient = primary
										.hasNonNull("selectedFeedbackCoefficient")
												? primary.get("selectedFeedbackCoefficient").asDouble(1.0)
												: 1.0;
								falconConfig.primaryPID.selectedFeedbackSensor = primary
										.hasNonNull("selectedFeedbackSensor")
												? CTREUtil.getFeedbackDevice(primary.get("selectedFeedbackSensor")
														.asText("IntegratedSensor"))
												: FeedbackDevice.IntegratedSensor;
							}
							if (pid.hasNonNull("auxiliary")) {
								var auxiliary = pid.get("auxiliary");

								falconConfig.auxiliaryPID.selectedFeedbackCoefficient = auxiliary
										.hasNonNull("selectedFeedbackCoefficient")
												? auxiliary.get("selectedFeedbackCoefficient").asDouble(1.0)
												: 1.0;
								falconConfig.auxiliaryPID.selectedFeedbackSensor = auxiliary
										.hasNonNull("selectedFeedbackSensor")
												? CTREUtil.getFeedbackDevice(auxiliary.get("selectedFeedbackSensor")
														.asText("IntegratedSensor"))
												: FeedbackDevice.IntegratedSensor;
							}
						}

						if (config.hasNonNull("limitSwitch")) {
							var limitSwitch = config.get("limitSwitch");

							if (limitSwitch.hasNonNull("forward")) {
								var forward = limitSwitch.get("forward");

								falconConfig.forwardLimitSwitchSource = forward.hasNonNull("forwardLimitSwitchSource")
										? CTREUtil.getLimitSwitchSource(
												forward.get("forwardLimitSwitchSource").asText("FeedbackConnector"))
										: LimitSwitchSource.FeedbackConnector;
								falconConfig.forwardLimitSwitchDeviceID = forward
										.hasNonNull("forwardLimitSwitchDeviceID")
												? forward.get("forwardLimitSwitchDeviceID").asInt()
												: 0;
								falconConfig.forwardLimitSwitchNormal = forward.hasNonNull("forwardLimitSwitchNormal")
										? CTREUtil.getLimitSwitchNormal(
												forward.get("forwardLimitSwitchNormal").asText("NormallyOpen"))
										: LimitSwitchNormal.NormallyOpen;
							}
							if (limitSwitch.hasNonNull("reverse")) {
								var reverse = limitSwitch.get("reverse");

								falconConfig.reverseLimitSwitchSource = reverse.hasNonNull("reverseLimitSwitchSource")
										? CTREUtil.getLimitSwitchSource(
												reverse.get("reverseLimitSwitchSource").asText("FeedbackConnector"))
										: LimitSwitchSource.FeedbackConnector;
								falconConfig.reverseLimitSwitchDeviceID = reverse
										.hasNonNull("reverseLimitSwitchDeviceID")
												? reverse.get("reverseLimitSwitchDeviceID").asInt()
												: 0;
								falconConfig.reverseLimitSwitchNormal = reverse.hasNonNull("reverseLimitSwitchNormal")
										? CTREUtil.getLimitSwitchNormal(
												reverse.get("reverseLimitSwitchNormal").asText("NormallyOpen"))
										: LimitSwitchNormal.NormallyOpen;
							}

							falconConfig.limitSwitchDisableNeutralOnLOS = limitSwitch
									.hasNonNull("limitSwitchDisableNeutralOnLOS")
											? limitSwitch.get("limitSwitchDisableNeutralOnLOS").asBoolean()
											: false;
						}

						if (config.hasNonNull("sumDiff")) {
							var sumDiff = config.get("sumDiff");

							falconConfig.sum0Term = sumDiff.hasNonNull("sum0Term")
									? CTREUtil.getFeedbackDevice(sumDiff.get("sum0Term").asText("IntegratedSensor"))
									: FeedbackDevice.IntegratedSensor;
							falconConfig.sum1Term = sumDiff.hasNonNull("sum1Term")
									? CTREUtil.getFeedbackDevice(sumDiff.get("sum1Term").asText("IntegratedSensor"))
									: FeedbackDevice.IntegratedSensor;
							falconConfig.diff0Term = sumDiff.hasNonNull("diff0Term")
									? CTREUtil.getFeedbackDevice(sumDiff.get("diff0Term").asText("IntegratedSensor"))
									: FeedbackDevice.IntegratedSensor;
							falconConfig.diff1Term = sumDiff.hasNonNull("diff1Term")
									? CTREUtil.getFeedbackDevice(sumDiff.get("diff1Term").asText("IntegratedSensor"))
									: FeedbackDevice.IntegratedSensor;
						}

						if (config.hasNonNull("currentLimit")) {
							var currentLimit = config.get("currentLimit");

							if (currentLimit.hasNonNull("supply")) {
								var supply = currentLimit.get("supply");

								falconConfig.supplyCurrLimit.enable = supply.hasNonNull("enable")
										? supply.get("enable").asBoolean(false)
										: false;
								falconConfig.supplyCurrLimit.currentLimit = supply.hasNonNull("currentLimit")
										? supply.get("currentLimit").asDouble()
										: 0.0;

								if (supply.hasNonNull("triggerThreshhold")) {
									var triggerThreshhold = supply.get("triggerThreshhold");

									falconConfig.supplyCurrLimit.triggerThresholdCurrent = triggerThreshhold
											.hasNonNull("triggerThresholdCurrent")
													? triggerThreshhold.get("triggerThresholdCurrent").asDouble()
													: 0.0;
									falconConfig.supplyCurrLimit.enable = triggerThreshhold.hasNonNull("enable")
											? triggerThreshhold.get("enable").asBoolean(false)
											: false;
								}
							}
							if (currentLimit.hasNonNull("stator")) {
								var stator = currentLimit.get("stator");

								falconConfig.statorCurrLimit.enable = stator.hasNonNull("enable")
										? stator.get("enable").asBoolean(false)
										: false;
								falconConfig.statorCurrLimit.currentLimit = stator.hasNonNull("currentLimit")
										? stator.get("currentLimit").asDouble()
										: 0.0;

								if (stator.hasNonNull("triggerThreshhold")) {
									var triggerThreshhold = stator.get("triggerThreshhold");

									falconConfig.statorCurrLimit.triggerThresholdCurrent = triggerThreshhold
											.hasNonNull("triggerThresholdCurrent")
													? triggerThreshhold.get("triggerThresholdCurrent").asDouble()
													: 0.0;
									falconConfig.statorCurrLimit.enable = triggerThreshhold.hasNonNull("enable")
											? triggerThreshhold.get("enable").asBoolean(false)
											: false;
								}
							}
						}
						falconConfig.motorCommutation = config.hasNonNull("motorCommutation")
								? CTREUtil.getMotorCommutation(config.get("motorCommutation").asText("Trapezoidal"))
								: MotorCommutation.Trapezoidal;
						falconConfig.absoluteSensorRange = config.hasNonNull("absoluteSensorRange")
								? CTREUtil.getAbsoluteSensorRange(
										config.get("absoluteSensorRange").asText("Unsigned_0_to_360"))
								: AbsoluteSensorRange.Unsigned_0_to_360;
						falconConfig.integratedSensorOffsetDegrees = config.hasNonNull("integratedSensorOffsetDegrees")
								? config.get("integratedSensorOffsetDegrees").asDouble()
								: 0.0;
						falconConfig.initializationStrategy = config.hasNonNull("initializationStrategy")
								? CTREUtil.getSensorInitializationStrategy(
										config.get("initializationStrategy").asText("BootToZero"))
								: SensorInitializationStrategy.BootToZero;
					}

					falcon.configAllSettings(falconConfig);
					falcon.setName(json.get("name").asText());

					var limiter = json.hasNonNull("limiter") ? json.get("limiter").asDouble(1.0) : 1.0;
					falcon.setLimiter(limiter < 0.0 ? 0.0 : limiter > 1.0 ? 1.0 : limiter);

					falcon.setNeutralMode(
							json.hasNonNull("neutralMode") ? CTREUtil.getNeutralMode(json.get("neutralMode").asText())
									: NeutralMode.Coast);
					falcon.setInverted(json.hasNonNull("inverted") ? json.get("inverted").asBoolean() : false);

					if (json.hasNonNull("sensors")) {
						var sensors = json.get("sensors");

						falcon.setSensorPhase(sensors.hasNonNull("phase") ? sensors.get("phase").asBoolean() : false);

						if (sensors.hasNonNull("primary")) {
							var primary = sensors.get("primary");

							falcon.configSelectedFeedbackSensor(
									primary.hasNonNull("feedbackDevice")
											? CTREUtil.getFeedbackDevice(
													primary.get("feedbackDevice").asText("IntegratedSensor"))
											: FeedbackDevice.IntegratedSensor,
									0, primary.hasNonNull("timeoutMs") ? primary.get("timeoutMs").asInt(0) : 0);
							falcon.setSelectedSensorPosition(
									primary.hasNonNull("sensorPos") ? primary.get("sensorPos").asInt() : 0, 0,
									primary.hasNonNull("timeoutMs") ? primary.get("timeoutMs").asInt(0) : 0);
						}
						if (sensors.hasNonNull("auxiliary")) {
							var auxiliary = sensors.get("auxiliary");

							falcon.configSelectedFeedbackSensor(
									auxiliary.hasNonNull("feedbackDevice")
											? CTREUtil.getFeedbackDevice(
													auxiliary.get("feedbackDevice").asText("IntegratedSensor"))
											: FeedbackDevice.IntegratedSensor,
									1, auxiliary.hasNonNull("timeoutMs") ? auxiliary.get("timeoutMs").asInt(0) : 0);
							falcon.setSelectedSensorPosition(
									auxiliary.hasNonNull("sensorPos") ? auxiliary.get("sensorPos").asInt() : 0, 1,
									auxiliary.hasNonNull("timeoutMs") ? auxiliary.get("timeoutMs").asInt(0) : 0);
						}
					}

					falcon.stopMotor();
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					falcons.put(json.get("name").asText(), falcon);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY FALCON ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				Logger.getGlobal().severe(e::getLocalizedMessage);
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link HyperFalcon} from the {@link #falcons} HashMap.
	 * 
	 * @param falconName The name of the {@link HyperFalcon}
	 * @return The requested {@link HyperFalcon}, if found
	 */
	default HyperFalcon getFalcon(String falconName) {
		return falcons.get(falconName);
	}
}