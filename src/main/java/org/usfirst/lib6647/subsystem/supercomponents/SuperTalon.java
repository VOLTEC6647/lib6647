package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.util.MotorUtils;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperTalon} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link HyperTalon
 * HyperTalons} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperTalon extends MotorUtils {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperTalon HyperTalons}.
	 */
	final HashMap<String, HyperTalon> talons = new HashMap<>();

	/**
	 * Method to initialize {@link HyperTalon HyperTalons} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #talons} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initTalons(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("talons").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !talons.containsKey(json.get("name").asText())
						&& json.hasNonNull("port")) {
					// Read values from JsonNode.
					int port = json.get("port").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (port < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR TALON '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create HyperTalon object.
					HyperTalon talon = new HyperTalon(json.get("port").asInt());

					// Additional initialization configuration.
					talon.setName(json.get("name").asText());

					if (json.hasNonNull("limiter"))
						setLimiter(json, talon);

					if (json.hasNonNull("neutralMode"))
						setNeutralMode(json, talon);

					if (json.hasNonNull("inverted"))
						setInverted(json, talon);

					if (json.hasNonNull("loopRamp")) {
						setClosedloopRamp(json, talon);
						setOpenloopRamp(json, talon);
					}

					if (json.hasNonNull("sensor"))
						setSensors(json, talon);

					if (json.hasNonNull("pid"))
						setPIDValues(json, talon);

					talon.stopMotor();
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					talons.put(json.get("name").asText(), talon);
				} else
					throw new ComponentInitException(
							String.format("[!] UNDECLARED, DUPLICATE, OR EMPTY TALON ENTRY IN SUBSYSTEM '%s'",
									subsystemName.toUpperCase()));
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
				DriverStation.reportError(e.getMessage(), false);
			}
		});
	}

	/**
	 * Sets a given {@link HyperTalon}'s {@link HyperTalon#limiter limiter} value
	 * from a {@link JsonNode}. Max value is 1, min value is 0 (which would make the
	 * {@link HyperTalon} stop entirely).
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperTalon}
	 */
	private void setLimiter(JsonNode json, HyperTalon talon) {
		double limiter = json.get("limiter").asDouble();
		talon.setLimiter(limiter < 0.0 ? 0.0 : limiter > 1.0 ? 1.0 : limiter);
	}

	/**
	 * Sets a given {@link HyperTalon}'s {@link NeutralMode} from a
	 * {@link JsonNode}.
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperTalon}
	 * @throws ComponentInitException if {@link JsonNode} key is defined, but empty
	 *                                or invalid.
	 * 
	 * @note There are three types of {@link NeutralMode NeutralModes}:
	 *       {@link NeutralMode#Coast Coast}, {@link NeutralMode#Brake Brake}, and
	 *       {@link NeutralMode#EEPROMSetting EEPROMSetting}. All of which must
	 *       share the same name in the {@link JsonNode}.
	 */
	private void setNeutralMode(JsonNode json, HyperTalon talon) throws ComponentInitException {
		if (getNeutralMode(json.get("neutralMode").asText()) == null)
			throw new ComponentInitException(
					String.format("[!] INVALID OR EMPTY NEUTRAL MODE CONFIGURATION FOR TALON '%s'.",
							json.get("name").asText().toUpperCase()));

		talon.setNeutralMode(getNeutralMode(json.get("neutralMode").asText()));
	}

	/**
	 * Sets a given {@link HyperTalon}'s inverted value from a {@link JsonNode}.
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperTalon}
	 */
	private void setInverted(JsonNode json, HyperTalon talon) {
		talon.setInverted(json.get("inverted").asBoolean());
	}

	/**
	 * Sets a given {@link HyperTalon}'s ClosedloopRamp from a {@link JsonNode} key.
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperTalon}
	 */
	private void setClosedloopRamp(JsonNode json, HyperTalon talon) {
		JsonNode closed = json.get("loopRamp").get("closed");

		if (closed.hasNonNull("timeoutMs"))
			talon.configClosedloopRamp(closed.get("secondsFromNeutralToFull").asDouble(),
					closed.get("timeoutMs").asInt());
		else
			talon.configClosedloopRamp(closed.get("secondsFromNeutralToFull").asDouble());
	}

	/**
	 * Sets a given {@link HyperTalon}'s OpenloopRamp from a {@link JsonNode}.
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperTalon}
	 */
	private void setOpenloopRamp(JsonNode json, HyperTalon talon) {
		JsonNode open = json.get("loopRamp").get("open");

		if (open.hasNonNull("timeoutMs"))
			talon.configOpenloopRamp(open.get("secondsFromNeutralToFull").asDouble(), open.get("timeoutMs").asInt());
		else
			talon.configOpenloopRamp(open.get("secondsFromNeutralToFull").asDouble());
	}

	/**
	 * Sets a given {@link HyperTalon}'s sensors from a {@link JsonNode} key (fairly
	 * limited in terms of configuration at the moment).
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperTalon}
	 */
	private void setSensors(JsonNode json, HyperTalon talon) {
		JsonNode sensor = json.get("sensor");
		JsonNode feedback = sensor.get("feedback");

		talon.configSelectedFeedbackSensor(getFeedbackDevice(feedback.get("feedbackDevice").asText()),
				feedback.get("pidIdx").asInt(), feedback.get("timeoutMs").asInt());

		talon.setSensorPhase(sensor.get("phase").asBoolean());

		talon.setSelectedSensorPosition(sensor.get("sensorPos").asInt(), sensor.get("pidIdx").asInt(),
				sensor.get("timeoutMs").asInt());
	}

	/**
	 * Sets a given {@link HyperTalon}'s PID values from a {@link JsonNode} key.
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperTalon}
	 */
	private void setPIDValues(JsonNode json, HyperTalon talon) {
		JsonNode pid = json.get("pid");
		int slotIdx = pid.get("slotIdx").asInt();

		talon.config_kP(slotIdx, pid.get("p").asDouble());
		talon.config_kI(slotIdx, pid.get("i").asDouble());
		talon.config_kD(slotIdx, pid.get("d").asDouble());
		talon.config_kF(slotIdx, pid.get("f").asDouble());
	}

	/**
	 * Gets specified {@link HyperTalon}.
	 * 
	 * @return {@link HyperTalon}
	 * @param talonName
	 */
	default HyperTalon getTalon(String talonName) {
		return talons.get(talonName);
	}
}