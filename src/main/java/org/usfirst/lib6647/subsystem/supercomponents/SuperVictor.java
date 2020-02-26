package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.RemoteFeedbackDevice;
import com.ctre.phoenix.motorcontrol.RemoteLimitSwitchSource;
import com.ctre.phoenix.motorcontrol.RemoteSensorSource;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.VictorSPXConfiguration;
import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperVictor;
import org.usfirst.lib6647.util.CTREUtil;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperVictor} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link HyperTalon HyperVictor objects}
 * declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperVictor {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperVictor} instances.
	 */
	final HashMap<String, HyperVictor> victors = new HashMap<>();

	/**
	 * Method to initialize {@link HyperVictor HyperVictor objects} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #victors} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initVictors(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("victors").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !victors.containsKey(json.get("name").asText())
						&& json.hasNonNull("port")) {
					// Read values from JsonNode.
					var port = json.get("port").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (port < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR VICTOR '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create HyperVictor object.
					var victor = new HyperVictor(json.get("port").asInt());
					var victorConfig = new VictorSPXConfiguration();

					// Additional initialization configuration.
					if (json.hasNonNull("config")) {
						var config = json.get("config");

						victorConfig.customParam0 = config.hasNonNull("customParam0")
								? config.get("customParam0").asInt(0)
								: 0;
						victorConfig.customParam1 = config.hasNonNull("customParam1")
								? config.get("customParam1").asInt(0)
								: 0;
						victorConfig.enableOptimizations = config.hasNonNull("enableOptimizations")
								? config.get("enableOptimizations").asBoolean(true)
								: true;

						if (config.hasNonNull("slot0")) {
							var slot = config.get("slot0");

							victorConfig.slot0.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							victorConfig.slot0.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							victorConfig.slot0.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							victorConfig.slot0.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							victorConfig.slot0.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							victorConfig.slot0.allowableClosedloopError = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							victorConfig.slot0.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							victorConfig.slot0.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							victorConfig.slot0.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}
						if (config.hasNonNull("slot1")) {
							var slot = config.get("slot1");

							victorConfig.slot1.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							victorConfig.slot1.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							victorConfig.slot1.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							victorConfig.slot1.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							victorConfig.slot1.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							victorConfig.slot1.allowableClosedloopError = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							victorConfig.slot1.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							victorConfig.slot1.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							victorConfig.slot1.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}
						if (config.hasNonNull("slot2")) {
							var slot = config.get("slot2");

							victorConfig.slot2.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							victorConfig.slot2.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							victorConfig.slot2.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							victorConfig.slot2.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							victorConfig.slot2.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							victorConfig.slot2.allowableClosedloopError = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							victorConfig.slot2.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							victorConfig.slot2.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							victorConfig.slot2.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}
						if (config.hasNonNull("slot3")) {
							var slot = config.get("slot3");

							victorConfig.slot3.kP = slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0;
							victorConfig.slot3.kI = slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0;
							victorConfig.slot3.kD = slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0;
							victorConfig.slot3.kF = slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0;

							victorConfig.slot3.integralZone = slot.hasNonNull("integralZone")
									? slot.get("integralZone").asInt()
									: 0;
							victorConfig.slot3.allowableClosedloopError = slot.hasNonNull("allowableClosedloopError")
									? slot.get("allowableClosedloopError").asInt()
									: 0;

							victorConfig.slot3.maxIntegralAccumulator = slot.hasNonNull("maxIntegralAccumulator")
									? slot.get("maxIntegralAccumulator").asDouble()
									: 0.0;
							victorConfig.slot3.closedLoopPeakOutput = slot.hasNonNull("closedLoopPeakOutput")
									? slot.get("closedLoopPeakOutput").asDouble(1.0)
									: 1.0;

							victorConfig.slot3.closedLoopPeriod = slot.hasNonNull("closedLoopPeriod")
									? slot.get("closedLoopPeriod").asInt(1)
									: 1;
						}

						if (config.hasNonNull("remoteSensor")) {
							var remoteSensor = config.get("remoteSensor");

							if (remoteSensor.hasNonNull("remoteFilter0")) {
								var remoteFilter = remoteSensor.get("remoteFilter0");

								victorConfig.remoteFilter0.remoteSensorDeviceID = remoteFilter.hasNonNull(
										"remoteSensorDeviceID") ? remoteFilter.get("remoteSensorDeviceID").asInt() : 0;
								victorConfig.remoteFilter0.remoteSensorSource = remoteFilter
										.hasNonNull("remoteSensorSource")
												? CTREUtil.getRemoteSensorSource(
														remoteFilter.get("remoteSensorSource").asText())
												: RemoteSensorSource.Off;
							}
							if (remoteSensor.hasNonNull("remoteFilter1")) {
								var remoteFilter = remoteSensor.get("remoteFilter1");

								victorConfig.remoteFilter1.remoteSensorDeviceID = remoteFilter.hasNonNull(
										"remoteSensorDeviceID") ? remoteFilter.get("remoteSensorDeviceID").asInt() : 0;
								victorConfig.remoteFilter1.remoteSensorSource = remoteFilter
										.hasNonNull("remoteSensorSource")
												? CTREUtil.getRemoteSensorSource(
														remoteFilter.get("remoteSensorSource").asText())
												: RemoteSensorSource.Off;
							}

							victorConfig.remoteSensorClosedLoopDisableNeutralOnLOS = remoteSensor
									.hasNonNull("remoteSensorClosedLoopDisableNeutralOnLOS")
											? remoteSensor.get("remoteSensorClosedLoopDisableNeutralOnLOS").asBoolean()
											: false;
						}

						if (config.hasNonNull("loopRamp")) {
							var loopRamp = config.get("loopRamp");

							victorConfig.openloopRamp = loopRamp.hasNonNull("openloopRamp")
									? loopRamp.get("openloopRamp").asDouble()
									: 0.0;
							victorConfig.closedloopRamp = loopRamp.hasNonNull("closedloopRamp")
									? loopRamp.get("closedloopRamp").asDouble()
									: 0.0;
						}

						if (config.hasNonNull("output")) {
							var output = config.get("output");

							if (output.hasNonNull("peakOutput")) {
								var peakOutput = output.get("peakOutput");

								victorConfig.peakOutputForward = peakOutput.hasNonNull("peakOutputForward")
										? peakOutput.get("peakOutputForward").asDouble(1.0)
										: 1.0;
								victorConfig.peakOutputReverse = peakOutput.hasNonNull("peakOutputReverse")
										? peakOutput.get("peakOutputReverse").asDouble(-1.0)
										: -1.0;
							}

							if (output.hasNonNull("nominalOutput")) {
								var nominalOutput = config.get("nominalOutput");

								victorConfig.nominalOutputForward = nominalOutput.hasNonNull("nominalOutputForward")
										? nominalOutput.get("nominalOutputForward").asDouble()
										: 0.0;
								victorConfig.nominalOutputReverse = nominalOutput.hasNonNull("nominalOutputReverse")
										? nominalOutput.get("nominalOutputReverse").asDouble()
										: 0.0;
							}

							victorConfig.neutralDeadband = output.hasNonNull("neutralDeadband")
									? output.get("neutralDeadband").asDouble(0.04)
									: 0.04;
						}

						if (config.hasNonNull("voltage")) {
							var voltage = config.get("voltage");

							victorConfig.voltageCompSaturation = voltage.hasNonNull("voltageCompSaturation")
									? voltage.get("voltageCompSaturation").asDouble()
									: 0.0;
							victorConfig.voltageMeasurementFilter = voltage.hasNonNull("voltageMeasurementFilter")
									? voltage.get("voltageMeasurementFilter").asInt(32)
									: 32;
							victorConfig.velocityMeasurementPeriod = voltage.hasNonNull("velocityMeasurementPeriod")
									? CTREUtil.getVelocityMeasurementPeriod(
											voltage.get("velocityMeasurementPeriod").asText())
									: VelocityMeasPeriod.Period_100Ms;
							victorConfig.velocityMeasurementWindow = voltage.hasNonNull("velocityMeasurementWindow")
									? voltage.get("velocityMeasurementWindow").asInt(64)
									: 64;
						}

						if (config.hasNonNull("softLimit")) {
							var softLimit = config.get("softLimit");

							victorConfig.forwardSoftLimitThreshold = softLimit.hasNonNull("forwardSoftLimitThreshold")
									? softLimit.get("forwardSoftLimitThreshold").asInt()
									: 0;
							victorConfig.reverseSoftLimitThreshold = softLimit.hasNonNull("reverseSoftLimitThreshold")
									? softLimit.get("reverseSoftLimitThreshold").asInt()
									: 0;
							victorConfig.forwardSoftLimitEnable = softLimit.hasNonNull("forwardSoftLimitEnable")
									? softLimit.get("forwardSoftLimitEnable").asBoolean()
									: false;
							victorConfig.reverseSoftLimitEnable = softLimit.hasNonNull("reverseSoftLimitEnable")
									? softLimit.get("reverseSoftLimitEnable").asBoolean()
									: false;
							victorConfig.softLimitDisableNeutralOnLOS = softLimit
									.hasNonNull("softLimitDisableNeutralOnLOS")
											? softLimit.get("softLimitDisableNeutralOnLOS").asBoolean()
											: false;
						}

						victorConfig.auxPIDPolarity = config.hasNonNull("auxPIDPolarity")
								? config.get("auxPIDPolarity").asBoolean()
								: false;

						if (config.hasNonNull("motion")) {
							var motion = config.get("motion");

							victorConfig.motionCruiseVelocity = motion.hasNonNull("motionCruiseVelocity")
									? motion.get("motionCruiseVelocity").asInt()
									: 0;
							victorConfig.motionAcceleration = motion.hasNonNull("motionAcceleration")
									? motion.get("motionAcceleration").asInt()
									: 0;
							victorConfig.motionCurveStrength = motion.hasNonNull("motionCurveStrength")
									? motion.get("motionCurveStrength").asInt()
									: 0;
							victorConfig.motionProfileTrajectoryPeriod = motion
									.hasNonNull("motionProfileTrajectoryPeriod")
											? motion.get("motionProfileTrajectoryPeriod").asInt()
											: 0;
						}

						victorConfig.feedbackNotContinuous = config.hasNonNull("feedbackNotContinuous")
								? config.get("feedbackNotContinuous").asBoolean()
								: false;

						if (config.hasNonNull("clear")) {
							var clear = config.get("clear");

							victorConfig.clearPositionOnLimitF = clear.hasNonNull("clearPositionOnLimitF")
									? clear.get("clearPositionOnLimitF").asBoolean()
									: false;
							victorConfig.clearPositionOnLimitR = clear.hasNonNull("clearPositionOnLimitR")
									? clear.get("clearPositionOnLimitR").asBoolean()
									: false;
							victorConfig.clearPositionOnQuadIdx = clear.hasNonNull("clearPositionOnQuadIdx")
									? clear.get("clearPositionOnQuadIdx").asBoolean()
									: false;
						}

						if (config.hasNonNull("pulseWithPeriod")) {
							var pulseWithPeriod = config.get("pulseWithPeriod");

							victorConfig.pulseWidthPeriod_EdgesPerRot = pulseWithPeriod
									.hasNonNull("pulseWidthPeriod_EdgesPerRot")
											? pulseWithPeriod.get("pulseWidthPeriod_EdgesPerRot").asInt(1)
											: 1;
							victorConfig.pulseWidthPeriod_FilterWindowSz = pulseWithPeriod
									.hasNonNull("pulseWidthPeriod_FilterWindowSz")
											? pulseWithPeriod.get("pulseWidthPeriod_FilterWindowSz").asInt(1)
											: 1;
						}

						victorConfig.trajectoryInterpolationEnable = config.hasNonNull("trajectoryInterpolationEnable")
								? config.get("trajectoryInterpolationEnable").asBoolean(true)
								: true;

						if (config.hasNonNull("pid")) {
							var pid = config.get("pid");

							if (pid.hasNonNull("primary")) {
								var primary = pid.get("primary");

								victorConfig.primaryPID.selectedFeedbackCoefficient = primary
										.hasNonNull("selectedFeedbackCoefficient")
												? primary.get("selectedFeedbackCoefficient").asDouble(1.0)
												: 1.0;
								victorConfig.primaryPID.selectedFeedbackSensor = primary
										.hasNonNull("selectedFeedbackSensor")
												? CTREUtil.getRemoteFeedbackDevice(
														primary.get("selectedFeedbackSensor").asText("None"))
												: RemoteFeedbackDevice.None;
							}
							if (pid.hasNonNull("auxiliary")) {
								var auxiliary = pid.get("auxiliary");

								victorConfig.auxiliaryPID.selectedFeedbackCoefficient = auxiliary
										.hasNonNull("selectedFeedbackCoefficient")
												? auxiliary.get("selectedFeedbackCoefficient").asDouble(1.0)
												: 1.0;
								victorConfig.auxiliaryPID.selectedFeedbackSensor = auxiliary
										.hasNonNull("selectedFeedbackSensor")
												? CTREUtil.getRemoteFeedbackDevice(
														auxiliary.get("selectedFeedbackSensor").asText("None"))
												: RemoteFeedbackDevice.None;
							}
						}

						if (config.hasNonNull("limitSwitch")) {
							var limitSwitch = config.get("limitSwitch");

							if (limitSwitch.hasNonNull("forward")) {
								var forward = limitSwitch.get("forward");

								victorConfig.forwardLimitSwitchSource = forward.hasNonNull("forwardLimitSwitchSource")
										? CTREUtil.getRemoteLimitSwitchSource(
												forward.get("forwardLimitSwitchSource").asText("Deactivated"))
										: RemoteLimitSwitchSource.Deactivated;
								victorConfig.forwardLimitSwitchDeviceID = forward
										.hasNonNull("forwardLimitSwitchDeviceID")
												? forward.get("forwardLimitSwitchDeviceID").asInt()
												: 0;
								victorConfig.forwardLimitSwitchNormal = forward.hasNonNull("forwardLimitSwitchNormal")
										? CTREUtil.getLimitSwitchNormal(
												forward.get("forwardLimitSwitchNormal").asText("NormallyOpen"))
										: LimitSwitchNormal.NormallyOpen;
							}
							if (limitSwitch.hasNonNull("reverse")) {
								var reverse = limitSwitch.get("reverse");

								victorConfig.reverseLimitSwitchSource = reverse.hasNonNull("reverseLimitSwitchSource")
										? CTREUtil.getRemoteLimitSwitchSource(
												reverse.get("reverseLimitSwitchSource").asText("Deactivated"))
										: RemoteLimitSwitchSource.Deactivated;
								victorConfig.reverseLimitSwitchDeviceID = reverse
										.hasNonNull("reverseLimitSwitchDeviceID")
												? reverse.get("reverseLimitSwitchDeviceID").asInt()
												: 0;
								victorConfig.reverseLimitSwitchNormal = reverse.hasNonNull("reverseLimitSwitchNormal")
										? CTREUtil.getLimitSwitchNormal(
												reverse.get("reverseLimitSwitchNormal").asText("NormallyOpen"))
										: LimitSwitchNormal.NormallyOpen;
							}

							victorConfig.limitSwitchDisableNeutralOnLOS = limitSwitch
									.hasNonNull("limitSwitchDisableNeutralOnLOS")
											? limitSwitch.get("limitSwitchDisableNeutralOnLOS").asBoolean()
											: false;
						}

						if (config.hasNonNull("sumDiff")) {
							var sumDiff = config.get("sumDiff");

							victorConfig.sum0Term = sumDiff.hasNonNull("sum0Term")
									? CTREUtil.getRemoteFeedbackDevice(sumDiff.get("sum0Term").asText("None"))
									: RemoteFeedbackDevice.None;
							victorConfig.sum1Term = sumDiff.hasNonNull("sum1Term")
									? CTREUtil.getRemoteFeedbackDevice(sumDiff.get("sum1Term").asText("None"))
									: RemoteFeedbackDevice.None;
							victorConfig.diff0Term = sumDiff.hasNonNull("diff0Term")
									? CTREUtil.getRemoteFeedbackDevice(sumDiff.get("diff0Term").asText("None"))
									: RemoteFeedbackDevice.None;
							victorConfig.diff1Term = sumDiff.hasNonNull("diff1Term")
									? CTREUtil.getRemoteFeedbackDevice(sumDiff.get("diff1Term").asText("None"))
									: RemoteFeedbackDevice.None;
						}
					}

					victor.configAllSettings(victorConfig);
					victor.setName(json.get("name").asText());

					var limiter = json.hasNonNull("limiter") ? json.get("limiter").asDouble(1.0) : 1.0;
					victor.setLimiter(limiter < 0.0 ? 0.0 : limiter > 1.0 ? 1.0 : limiter);

					victor.setNeutralMode(
							json.hasNonNull("neutralMode") ? CTREUtil.getNeutralMode(json.get("neutralMode").asText())
									: NeutralMode.Coast);
					victor.setInverted(json.hasNonNull("inverted") ? json.get("inverted").asBoolean() : false);

					victor.stopMotor();
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					victors.put(json.get("name").asText(), victor);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY VICTOR ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link HyperVictor} from the {@link #victors} HashMap.
	 * 
	 * @param victorName The name of the {@link HyperVictor}
	 * @return The requested {@link HyperVictor}, if found
	 */
	default HyperVictor getVictor(String victorName) {
		return victors.get(victorName);
	}
}