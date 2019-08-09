package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.stream.Stream;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperVictor;
import org.usfirst.lib6647.util.ComponentInitException;
import org.usfirst.lib6647.util.MotorUtils;

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
	public HashMap<String, HyperVictor> victors = new HashMap<String, HyperVictor>();

	/**
	 * Method to initialize {@link HyperVictor HyperVictors} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #victors} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initVictors(JSONObject robotMap, String subsystemName) {
		// Create a JSONArray out of the declared objects.
		JSONArray victorArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems")).get(subsystemName))
				.get("victors");

		// Create a parallel stream from the JSONArray.
		Stream<?> stream = victorArray.parallelStream();
		// Cast each entry into a JSONObject, and configure it using the values declared
		// in the JSON file.
		stream.map(json -> (JSONObject) json).forEach(json -> {
			try {
				if (json.containsKey("name") && json.containsKey("port")) {

					HyperVictor victor;
					try {
						// Create an object out of one index in the JSONArray.
						victor = new HyperVictor(Integer.parseInt(json.get("port").toString()));
					} catch (NumberFormatException e) {
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR VICTOR '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").toString(), subsystemName));
					}
					victor.setName(json.get("name").toString());

					if (json.containsKey("limiter"))
						setLimiter(json, victor);

					if (json.containsKey("neutralMode"))
						setNeutralMode(json, victor);

					if (json.containsKey("inverted"))
						setInverted(json, victor);

					if (json.containsKey("loopRamp")) {
						setClosedloopRamp(json, victor);
						setOpenloopRamp(json, victor);
					}

					victor.stopMotor();

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					victors.put(json.get("name").toString(), victor);
				} else {
					System.out.println(String.format("[!] UNDECLARED OR EMPTY VICTOR ENTRY IN SUBSYSTEM '%s'",
							subsystemName.toUpperCase()));
				}
			} catch (ComponentInitException e) {
				System.out.println(e.getMessage());
			} finally {
				// Clear JSONObject after use, not sure if it does anything, but it might free
				// some unused memory.
				json.clear();
			}
		});

		// Clear JSONArray after use, not sure if it does anything, but it might free
		// some unused memory.
		victorArray.clear();
	}

	/**
	 * Sets a given {@link HyperVictor}'s {@link HyperVictor#limiter limiter} value
	 * from a {@link JSONObject}. Max value is 1, min value is 0 (which would make
	 * the {@link HyperVictor} stop).
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperVictor}
	 * @throws ComponentInitException if {@link JSONObject} key is defined, but
	 *                                empty or not a number.
	 */
	private void setLimiter(JSONObject json, HyperVictor victor) throws ComponentInitException {
		try {
			double limiter = Double.parseDouble(json.get("limiter").toString());
			victor.setLimiter(limiter < 0.0 ? 0.0 : limiter > 1.0 ? 1.0 : limiter);
		} catch (NullPointerException e) {
			throw new ComponentInitException(String.format("[!] EMPTY LIMITER VALUE FOR VICTOR '%s'.",
					json.get("name").toString().toUpperCase()));
		} catch (NumberFormatException e) {
			throw new ComponentInitException(String.format("[!] INVALID LIMITER VALUE FOR VICTOR '%s'.",
					json.get("name").toString().toUpperCase()));
		}
	}

	/**
	 * Sets a given {@link HyperVictor}'s inverted value from a {@link JSONObject}.
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperVictor}
	 * @throws ComponentInitException if {@link JSONObject} key is defined, but
	 *                                empty.
	 */
	private void setInverted(JSONObject json, HyperVictor victor) throws ComponentInitException {
		if (json.get("inverted").toString().isEmpty())
			throw new ComponentInitException(String.format("[!] EMPTY INVERTED VALUE FOR VICTOR '%s'.",
					json.get("name").toString().toUpperCase()));

		victor.setInverted(Boolean.parseBoolean(json.get("inverted").toString()));
	}

	/**
	 * Sets a given {@link HyperVictor}'s {@link NeutralMode} from a
	 * {@link JSONObject} key.
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperVictor}
	 * @throws ComponentInitException if {@link JSONObject} key is defined, but
	 *                                empty or invalid.
	 * 
	 * @note There are three types of {@link NeutralMode NeutralModes}:
	 *       {@link NeutralMode#Coast Coast}, {@link NeutralMode#Brake Brake}, and
	 *       {@link NeutralMode#EEPROMSetting EEPROMSetting}. All of which must
	 *       share the same name in the {@link JSONObject}.
	 */
	private void setNeutralMode(JSONObject json, HyperVictor victor) throws ComponentInitException {
		if (getNeutralMode(json.get("neutralMode").toString()) == null)
			throw new ComponentInitException(
					String.format("[!] INVALID OR EMPTY NEUTRAL MODE CONFIGURATION FOR VICTOR '%s'.",
							json.get("name").toString().toUpperCase()));

		victor.setNeutralMode(getNeutralMode(json.get("neutralMode").toString()));
	}

	/**
	 * Sets a given {@link HyperVictor}'s ClosedloopRamp from a {@link JSONObject}
	 * key.
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperVictor}
	 * @throws ComponentInitException if {@link JSONObject} key is not found, or its
	 *                                subkeys are invalid or empty.
	 */
	private void setClosedloopRamp(JSONObject json, HyperVictor victor) throws ComponentInitException {
		try {
			JSONObject closed = (JSONObject) ((JSONObject) json.get("loopRamp")).get("closed");

			if (closed.containsKey("timeoutMs"))
				victor.configClosedloopRamp(Double.parseDouble(closed.get("secondsFromNeutralToFull").toString()),
						Integer.parseInt(closed.get("timeoutMs").toString()));
			else
				victor.configClosedloopRamp(Double.parseDouble(closed.get("secondsFromNeutralToFull").toString()));
		} catch (NullPointerException e) {
			throw new ComponentInitException(String.format("[!] EMPTY CLOSEDLOOP RAMP VALUE(S) FOR VICTOR '%s'.",
					json.get("name").toString().toUpperCase()));
		} catch (NumberFormatException e) {
			throw new ComponentInitException(String.format("[!] INVALID CLOSEDLOOP RAMP VALUE(S) FOR VICTOR '%s'.",
					json.get("name").toString().toUpperCase()));
		}
	}

	/**
	 * Sets a given {@link HyperVictor}'s OpenloopRamp from a {@link JSONObject}
	 * key.
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperVictor}
	 * @throws ComponentInitException if {@link JSONObject} key is not found, or its
	 *                                subkeys are invalid or empty.
	 */
	private void setOpenloopRamp(JSONObject json, HyperVictor victor) throws ComponentInitException {
		try {
			JSONObject open = (JSONObject) ((JSONObject) json.get("loopRamp")).get("open");

			if (open.containsKey("timeoutMs"))
				victor.configOpenloopRamp(Double.parseDouble(open.get("secondsFromNeutralToFull").toString()),
						Integer.parseInt(open.get("timeoutMs").toString()));
			else
				victor.configOpenloopRamp(Double.parseDouble(open.get("secondsFromNeutralToFull").toString()));
		} catch (NullPointerException e) {
			throw new ComponentInitException(String.format("[!] EMPTY OPENLOOP RAMP VALUE(S) FOR VICTOR '%s'.",
					json.get("name").toString().toUpperCase()));
		} catch (NumberFormatException e) {
			throw new ComponentInitException(String.format("[!] INVALID OPENLOOP RAMP VALUE(S) FOR VICTOR '%s'.",
					json.get("name").toString().toUpperCase()));
		}
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