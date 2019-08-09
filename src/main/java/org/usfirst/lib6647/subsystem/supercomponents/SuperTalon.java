package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.stream.Stream;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.usfirst.lib6647.subsystem.PIDSuperSubsystem;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperTalon;
import org.usfirst.lib6647.util.ComponentInitException;
import org.usfirst.lib6647.util.MotorUtils;

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
	public HashMap<String, HyperTalon> talons = new HashMap<String, HyperTalon>();

	/**
	 * Method to initialize {@link HyperTalon HyperTalons} declared in the
	 * {@link SuperSubsystem#robotMap robotMap} JSON file, and add them to the
	 * {@link #talons} HashMap using its declared name as its key.
	 * 
	 * @param {@link SuperSubsystem#robotMap}
	 * @param {@link SuperSubsystem#getName}
	 */
	default void initTalons(JSONObject robotMap, String subsystemName) {
		// Create a JSONArray out of the declared objects.
		JSONArray talonArray = (JSONArray) ((JSONObject) ((JSONObject) robotMap.get("subsystems")).get(subsystemName))
				.get("talons");

		// Create a parallel stream from the JSONArray.
		Stream<?> stream = talonArray.parallelStream();
		// Cast each entry into a JSONObject, and configure it using the values declared
		// in the JSON file.
		stream.map(json -> (JSONObject) json).forEach(json -> {
			try {
				if (json.containsKey("name") && json.containsKey("port")) {

					HyperTalon talon;
					try {
						// Try to initialize an object from an index in the JSONArray.
						talon = new HyperTalon(Integer.parseInt(json.get("port").toString()));
					} catch (NumberFormatException e) {
						throw new ComponentInitException(
								String.format("[!] INVALID OR EMPTY PORT VALUE FOR TALON '%1$s' IN SUBSYSTEM '%2$s'",
										json.get("name").toString(), subsystemName));
					}
					talon.setName(json.get("name").toString());

					if (json.containsKey("limiter"))
						setLimiter(json, talon);

					if (json.containsKey("neutralMode"))
						setNeutralMode(json, talon);

					if (json.containsKey("inverted"))
						setInverted(json, talon);

					if (json.containsKey("loopRamp")) {
						setClosedloopRamp(json, talon);
						setOpenloopRamp(json, talon);
					}

					if (json.containsKey("sensor"))
						setSensors(json, talon);

					if (json.containsKey("pid"))
						setPIDValues(json, talon);

					talon.stopMotor();

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					talons.put(json.get("name").toString(), talon);
				} else {
					System.out.println(String.format("[!] UNDECLARED OR EMPTY TALON ENTRY IN SUBSYSTEM '%s'",
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
		talonArray.clear();
	}

	/**
	 * Sets a given {@link HyperTalon}'s {@link HyperTalon#limiter limiter} value
	 * from a {@link JSONObject}. Max value is 1, min value is 0 (which would make
	 * the {@link HyperTalon} stop).
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperTalon}
	 * @throws ComponentInitException if {@link JSONObject} key is defined, but
	 *                                empty or not a number.
	 */
	private void setLimiter(JSONObject json, HyperTalon talon) throws ComponentInitException {
		try {
			double limiter = Double.parseDouble(json.get("limiter").toString());
			talon.setLimiter(limiter < 0.0 ? 0.0 : limiter > 1.0 ? 1.0 : limiter);
		} catch (NullPointerException e) {
			throw new ComponentInitException(String.format("[!] EMPTY LIMITER VALUE FOR TALON '%s'.",
					json.get("name").toString().toUpperCase()));
		} catch (NumberFormatException e) {
			throw new ComponentInitException(String.format("[!] INVALID LIMITER VALUE FOR TALON '%s'.",
					json.get("name").toString().toUpperCase()));
		}
	}

	/**
	 * Sets a given {@link HyperTalon}'s inverted value from a {@link JSONObject}.
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperTalon}
	 * @throws ComponentInitException if {@link JSONObject} key is defined, but
	 *                                empty.
	 */
	private void setInverted(JSONObject json, HyperTalon talon) throws ComponentInitException {
		if (json.get("inverted").toString().isEmpty())
			throw new ComponentInitException(String.format("[!] EMPTY INVERTED VALUE FOR TALON '%s'.",
					json.get("name").toString().toUpperCase()));

		talon.setInverted(Boolean.parseBoolean(json.get("inverted").toString()));
	}

	/**
	 * Sets a given {@link HyperTalon}'s {@link NeutralMode} from a
	 * {@link JSONObject} key.
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperTalon}
	 * @throws ComponentInitException if {@link JSONObject} key is defined, but
	 *                                empty or invalid.
	 * 
	 * @note There are three types of {@link NeutralMode NeutralModes}:
	 *       {@link NeutralMode#Coast Coast}, {@link NeutralMode#Brake Brake}, and
	 *       {@link NeutralMode#EEPROMSetting EEPROMSetting}. All of which must
	 *       share the same name in the {@link JSONObject}.
	 */
	private void setNeutralMode(JSONObject json, HyperTalon talon) throws ComponentInitException {
		if (getNeutralMode(json.get("neutralMode").toString()) == null)
			throw new ComponentInitException(
					String.format("[!] INVALID OR EMPTY NEUTRAL MODE CONFIGURATION FOR TALON '%s'.",
							json.get("name").toString().toUpperCase()));

		talon.setNeutralMode(getNeutralMode(json.get("neutralMode").toString()));
	}

	/**
	 * Sets a given {@link HyperTalon}'s ClosedloopRamp from a {@link JSONObject}
	 * key.
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperTalon}
	 * @throws ComponentInitException if {@link JSONObject} key is not found, or its
	 *                                subkeys are invalid or empty.
	 */
	private void setClosedloopRamp(JSONObject json, HyperTalon talon) throws ComponentInitException {
		try {
			JSONObject closed = (JSONObject) ((JSONObject) json.get("loopRamp")).get("closed");

			if (closed.containsKey("timeoutMs"))
				talon.configClosedloopRamp(Double.parseDouble(closed.get("secondsFromNeutralToFull").toString()),
						Integer.parseInt(closed.get("timeoutMs").toString()));
			else
				talon.configClosedloopRamp(Double.parseDouble(closed.get("secondsFromNeutralToFull").toString()));
		} catch (NullPointerException e) {
			throw new ComponentInitException(String.format("[!] EMPTY CLOSEDLOOP RAMP VALUE(S) FOR TALON '%s'.",
					json.get("name").toString().toUpperCase()));
		} catch (NumberFormatException e) {
			throw new ComponentInitException(String.format("[!] INVALID CLOSEDLOOP RAMP VALUE(S) FOR TALON '%s'.",
					json.get("name").toString().toUpperCase()));
		}
	}

	/**
	 * Sets a given {@link HyperTalon}'s OpenloopRamp from a {@link JSONObject} key.
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperTalon}
	 * @throws ComponentInitException if {@link JSONObject} key is not found, or its
	 *                                subkeys are invalid or empty.
	 */
	private void setOpenloopRamp(JSONObject json, HyperTalon talon) throws ComponentInitException {
		try {
			JSONObject open = (JSONObject) ((JSONObject) json.get("loopRamp")).get("open");

			if (open.containsKey("timeoutMs"))
				talon.configOpenloopRamp(Double.parseDouble(open.get("secondsFromNeutralToFull").toString()),
						Integer.parseInt(open.get("timeoutMs").toString()));
			else
				talon.configOpenloopRamp(Double.parseDouble(open.get("secondsFromNeutralToFull").toString()));
		} catch (NullPointerException e) {
			throw new ComponentInitException(String.format("[!] EMPTY OPENLOOP RAMP VALUE(S) FOR TALON '%s'.",
					json.get("name").toString().toUpperCase()));
		} catch (NumberFormatException e) {
			throw new ComponentInitException(String.format("[!] INVALID OPENLOOP RAMP VALUE(S) FOR TALON '%s'.",
					json.get("name").toString().toUpperCase()));
		}
	}

	/**
	 * Sets a given {@link HyperTalon}'s sensors from a {@link JSONObject} key
	 * (fairly limited in terms of configuration at the moment).
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperTalon}
	 * @throws ComponentInitException if {@link JSONObject} key is not found, or its
	 *                                subkeys are invalid or empty.
	 */
	private void setSensors(JSONObject json, HyperTalon talon) throws ComponentInitException {
		try {
			JSONObject sensor = (JSONObject) json.get("sensor");
			JSONObject feedback = (JSONObject) sensor.get("feedback");

			talon.configSelectedFeedbackSensor(getFeedbackDevice(feedback.get("feedbackDevice").toString()),
					Integer.parseInt(feedback.get("pidIdx").toString()),
					Integer.parseInt(feedback.get("timeoutMs").toString()));

			talon.setSensorPhase(Boolean.parseBoolean(sensor.get("phase").toString()));

			talon.setSelectedSensorPosition(Integer.parseInt(sensor.get("sensorPos").toString()),
					Integer.parseInt(sensor.get("pidIdx").toString()),
					Integer.parseInt(sensor.get("timeoutMs").toString()));
		} catch (NullPointerException e) {
			throw new ComponentInitException(String.format("[!] EMPTY SENSOR VALUE(S) FOR TALON '%s'.",
					json.get("name").toString().toUpperCase()));
		} catch (NumberFormatException e) {
			throw new ComponentInitException(String.format("[!] INVALID SENSOR VALUE(S) FOR TALON '%s'.",
					json.get("name").toString().toUpperCase()));
		}
	}

	/**
	 * Sets a given {@link HyperTalon}'s PID values from a {@link JSONObject} key.
	 * 
	 * @param {@link JSONObject}
	 * @param {@link HyperTalon}
	 * @throws ComponentInitException if {@link JSONObject} key is not found, or its
	 *                                subkeys are invalid or empty.
	 */
	private void setPIDValues(JSONObject json, HyperTalon talon) throws ComponentInitException {
		try {
			JSONObject pid = (JSONObject) json.get("pid");

			talon.config_kP(Integer.parseInt(pid.get("slotIdx").toString()),
					Double.parseDouble(pid.get("p").toString()));
			talon.config_kI(Integer.parseInt(pid.get("slotIdx").toString()),
					Double.parseDouble(pid.get("i").toString()));
			talon.config_kD(Integer.parseInt(pid.get("slotIdx").toString()),
					Double.parseDouble(pid.get("d").toString()));
			talon.config_kF(Integer.parseInt(pid.get("slotIdx").toString()),
					Double.parseDouble(pid.get("f").toString()));
		} catch (NullPointerException e) {
			throw new ComponentInitException(
					String.format("[!] EMPTY PID VALUE(S) FOR TALON '%s'.", json.get("name").toString().toUpperCase()));
		} catch (NumberFormatException e) {
			throw new ComponentInitException(String.format("[!] INVALID PID VALUE(S) FOR TALON '%s'.",
					json.get("name").toString().toUpperCase()));
		}
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