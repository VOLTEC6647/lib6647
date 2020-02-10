package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperVictor;
import org.usfirst.lib6647.util.MotorUtil;

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

					// Additional initialization configuration.
					victor.setName(json.get("name").asText());

					if (json.hasNonNull("limiter"))
						setLimiter(json, victor);

					if (json.hasNonNull("neutralMode"))
						setNeutralMode(json, victor);

					if (json.hasNonNull("inverted"))
						setInverted(json, victor);

					if (json.hasNonNull("loopRamp")) {
						setClosedloopRamp(json, victor);
						setOpenloopRamp(json, victor);
					}

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
	 * Sets a given {@link HyperVictor}'s {@link HyperVictor#limiter limiter} value
	 * from a {@link JsonNode}. Max value is 1, min value is 0 (which would make the
	 * {@link HyperVictor} stop entirely).
	 * 
	 * @param json   The node to read
	 * @param victor The {@link HyperVictor} to configure
	 */
	private void setLimiter(JsonNode json, HyperVictor victor) {
		var limiter = json.get("limiter").asDouble();
		victor.setLimiter(limiter < 0.0 ? 0.0 : limiter > 1.0 ? 1.0 : limiter);
	}

	/**
	 * Sets a given {@link HyperVictor}'s {@link NeutralMode} from a
	 * {@link JsonNode}.
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
	 * @param json   The node to read
	 * @param victor The {@link HyperVictor} to configure
	 * @throws ComponentInitException When {@link JsonNode} key is defined, but
	 *                                empty or invalid
	 */
	private void setNeutralMode(JsonNode json, HyperVictor victor) throws ComponentInitException {
		if (MotorUtil.getNeutralMode(json.get("neutralMode").asText()) == null)
			throw new ComponentInitException(
					String.format("[!] INVALID OR EMPTY NEUTRAL MODE CONFIGURATION FOR VICTOR '%s'.",
							json.get("name").asText().toUpperCase()));

		victor.setNeutralMode(MotorUtil.getNeutralMode(json.get("neutralMode").asText()));
	}

	/**
	 * Sets a given {@link HyperVictor}'s inverted value from a {@link JsonNode}.
	 * 
	 * @param json   The node to read
	 * @param victor The {@link HyperVictor} to configure
	 */
	private void setInverted(JsonNode json, HyperVictor victor) {
		victor.setInverted(json.get("inverted").asBoolean());
	}

	/**
	 * Sets a given {@link HyperVictor}'s Closed-loop ramp from a {@link JsonNode}.
	 * 
	 * @param json   The node to read
	 * @param victor The {@link HyperVictor} to configure
	 */
	private void setClosedloopRamp(JsonNode json, HyperVictor victor) {
		var closed = json.get("loopRamp").get("closed");

		if (closed.hasNonNull("timeoutMs"))
			victor.configClosedloopRamp(closed.get("secondsFromNeutralToFull").asDouble(),
					closed.get("timeoutMs").asInt());
		else
			victor.configClosedloopRamp(closed.get("secondsFromNeutralToFull").asDouble());
	}

	/**
	 * Sets a given {@link HyperVictor}'s Open-loop ramp from a {@link JsonNode}.
	 * 
	 * @param json   The node to read
	 * @param victor The {@link HyperVictor} to configure
	 */
	private void setOpenloopRamp(JsonNode json, HyperVictor victor) {
		var open = json.get("loopRamp").get("open");

		if (open.hasNonNull("timeoutMs"))
			victor.configOpenloopRamp(open.get("secondsFromNeutralToFull").asDouble(), open.get("timeoutMs").asInt());
		else
			victor.configOpenloopRamp(open.get("secondsFromNeutralToFull").asDouble());
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