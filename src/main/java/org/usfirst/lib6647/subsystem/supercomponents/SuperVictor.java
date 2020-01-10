package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperVictor;
import org.usfirst.lib6647.util.ComponentInitException;
import org.usfirst.lib6647.util.MotorUtils;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperVictor} initialization via JSON. Subsystems
 * declared need to extend {@link SuperSubsystem} or {@link PIDSuperSubsystem}
 * and implement this interface in order to initialize {@link HyperTalon
 * HyperVictors} declared in {@link SuperSubsystem#robotMap robotMap}.
 */
public interface SuperVictor extends MotorUtils {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperVictor
	 * HyperVictors}.
	 */
	public HashMap<String, HyperVictor> victors = new HashMap<>();

	/**
	 * Method to initialize {@link HyperVictor HyperVictors} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #victors} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initVictors(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("victors").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !victors.containsKey(json.get("name").asText())
						&& json.hasNonNull("port")) {
					// Read values from JsonNode.
					int port = json.get("port").asInt(-1);

					// Check if the required JsonNode values to initialize the object are present.
					if (port < 0)
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR VICTOR '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").asText(), subsystemName));

					// Create HyperVictor object.
					HyperVictor victor = new HyperVictor(json.get("port").asInt());

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
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
				DriverStation.reportError(e.getMessage(), false);
			}
		});
	}

	/**
	 * Sets a given {@link HyperVictor}'s {@link HyperVictor#limiter limiter} value
	 * from a {@link JsonNode}. Max value is 1, min value is 0 (which would make the
	 * {@link HyperVictor} stop).
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperVictor}
	 */
	private void setLimiter(JsonNode json, HyperVictor victor) {
		double limiter = json.get("limiter").asDouble();
		victor.setLimiter(limiter < 0.0 ? 0.0 : limiter > 1.0 ? 1.0 : limiter);
	}

	/**
	 * Sets a given {@link HyperVictor}'s inverted value from a {@link JsonNode}.
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperVictor}
	 */
	private void setInverted(JsonNode json, HyperVictor victor) {
		victor.setInverted(json.get("inverted").asBoolean());
	}

	/**
	 * Sets a given {@link HyperVictor}'s {@link NeutralMode} from a
	 * {@link JsonNode} key.
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperVictor}
	 * @throws ComponentInitException if {@link JsonNode} key is defined, but empty
	 *                                or invalid.
	 * 
	 * @note There are three types of {@link NeutralMode NeutralModes}:
	 *       {@link NeutralMode#Coast Coast}, {@link NeutralMode#Brake Brake}, and
	 *       {@link NeutralMode#EEPROMSetting EEPROMSetting}. All of which must
	 *       share the same name in the {@link JsonNode}.
	 */
	private void setNeutralMode(JsonNode json, HyperVictor victor) throws ComponentInitException {
		if (getNeutralMode(json.get("neutralMode").asText()) == null)
			throw new ComponentInitException(
					String.format("[!] INVALID OR EMPTY NEUTRAL MODE CONFIGURATION FOR VICTOR '%s'.",
							json.get("name").asText().toUpperCase()));

		victor.setNeutralMode(getNeutralMode(json.get("neutralMode").asText()));
	}

	/**
	 * Sets a given {@link HyperVictor}'s ClosedloopRamp from a {@link JsonNode}
	 * key.
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperVictor}
	 */
	private void setClosedloopRamp(JsonNode json, HyperVictor victor) {
		JsonNode closed = json.get("loopRamp").get("closed");

		if (closed.hasNonNull("timeoutMs"))
			victor.configClosedloopRamp(closed.get("secondsFromNeutralToFull").asDouble(),
					closed.get("timeoutMs").asInt());
		else
			victor.configClosedloopRamp(closed.get("secondsFromNeutralToFull").asDouble());
	}

	/**
	 * Sets a given {@link HyperVictor}'s OpenloopRamp from a {@link JsonNode} key.
	 * 
	 * @param {@link JsonNode}
	 * @param {@link HyperVictor}
	 */
	private void setOpenloopRamp(JsonNode json, HyperVictor victor) {
		JsonNode open = json.get("loopRamp").get("open");

		if (open.hasNonNull("timeoutMs"))
			victor.configOpenloopRamp(open.get("secondsFromNeutralToFull").asDouble(), open.get("timeoutMs").asInt());
		else
			victor.configOpenloopRamp(open.get("secondsFromNeutralToFull").asDouble());
	}

	/**
	 * Gets specified {@link HyperVictor}.
	 * 
	 * @return {@link HyperVictor}
	 * @param victorName
	 */
	default HyperVictor getVictor(String victorName) {
		return victors.get(victorName);
	}
}