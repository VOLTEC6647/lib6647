package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.SoftLimitDirection;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.REVUtil;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link CANSparkMax} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link CANSparkMax CANSparkMax objects}
 * declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperSparkMax {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link CANSparkMax} instances.
	 */
	final Map<String, CANSparkMax> sparks = new HashMap<>();
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link CANPIDController}
	 * instances.
	 */
	final Map<String, CANPIDController> sparkPIDcontrollers = new HashMap<>();
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link CANEncoder} instances.
	 */
	final Map<String, CANEncoder> sparkEncoders = new HashMap<>();

	/**
	 * Method to initialize {@link CANSparkMax CANSparkMax objects} declared in the
	 * {@link SuperSubsystem#robotMap JSON file}, and add them to the
	 * {@link #sparks} HashMap using its declared name as its key.
	 * 
	 * @param robotMap      The inherited {@link SuperSubsystem#robotMap} location
	 * @param subsystemName The {@link SuperSubsystem}'s name; you can just pass on
	 *                      the {@link SuperSubsystem#getName} method
	 */
	default void initSparks(JsonNode robotMap, String subsystemName) {

		// Spliterate through each of the elements in the JsonNode.
		robotMap.get("sparks").spliterator().forEachRemaining(json -> {
			try {
				if (json.hasNonNull("name") && !sparks.containsKey(json.get("name").asText()) && json.hasNonNull("port")
						&& json.hasNonNull("type")) {
					var type = REVUtil.getMotorType(json.get("type").asText());

					// Check if the required JsonNode values to initialize the object are present.
					if (type == null)
						throw new ComponentInitException(String.format(
								"[!] INVALID OR EMPTY MOTORTYPE VALUE FOR SPARK '%1$s' IN SUBSYSTEM '%2$s'",
								json.get("name").asText(), subsystemName));

					// Create CANSparkMax object.
					var spark = new CANSparkMax(json.get("port").asInt(), type);
					var controller = spark.getPIDController();
					var encoder = spark.getEncoder();

					spark.restoreFactoryDefaults();

					// Additional initialization configuration.
					if (json.hasNonNull("idleMode")) {
						var idleMode = REVUtil.getIdleMode(json.get("idleMode").asText());

						if (idleMode == null)
							throw new ComponentInitException(String.format(
									"[!] INVALID OR EMPTY IDLEMODE VALUE FOR SPARK '%1$s' IN SUBSYSTEM '%2$s'",
									json.get("name").asText(), subsystemName));

						spark.setIdleMode(idleMode);
					}

					if (json.hasNonNull("softLimitForward")) {
						spark.enableSoftLimit(SoftLimitDirection.kForward, true);
						spark.setSoftLimit(SoftLimitDirection.kForward, json.get("softLimitForward").floatValue());
					}
					if (json.hasNonNull("softLimitReverse")) {
						spark.enableSoftLimit(SoftLimitDirection.kReverse, true);
						spark.setSoftLimit(SoftLimitDirection.kReverse, json.get("softLimitReverse").floatValue());
					}

					if (json.hasNonNull("pid")) {
						var pid = json.get("pid");

						for (int i = 0; i < 4; i++) {
							if (pid.hasNonNull("slot" + i)) {
								var slot = pid.get("slot" + i);

								controller.setP(slot.get("p").asDouble(), i);
								controller.setI(slot.get("i").asDouble(), i);
								controller.setD(slot.get("d").asDouble(), i);
								controller.setFF(slot.get("f").asDouble(), i);

								controller.setIZone(slot.get("iZone").asDouble());
								controller.setOutputRange(slot.get("outputMin").asDouble(-1),
										slot.get("outputMax").asDouble(1));
							}
						}
					}
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					sparks.put(json.get("name").asText(), spark);
					sparkPIDcontrollers.put(json.get("name").asText(), controller);
					sparkEncoders.put(json.get("name").asText(), encoder);
				}
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link CANSparkMax} from the {@link #sparks} HashMap.
	 * 
	 * @param sparkName The name of the {@link CANSparkMax}
	 * @return The requested {@link CANSparkMax}, if found
	 */
	default CANSparkMax getSpark(String sparkName) {
		return sparks.get(sparkName);
	}

	/**
	 * Gets the specified {@link CANSparkMax}'s {@link CANPIDController} from the
	 * {@link #sparkPIDcontrollers} HashMap.
	 * 
	 * @param sparkName The name of the {@link CANSparkMax}
	 * @return The requested {@link CANPIDController}, if found
	 */
	default CANPIDController getSparkPID(String sparkName) {
		return sparkPIDcontrollers.get(sparkName);
	}

	/**
	 * Gets the specified {@link CANSparkMax}'s {@link CANEncoder} from the
	 * {@link #sparkEncoders} HashMap.
	 * 
	 * @param sparkName The name of the {@link CANSparkMax}
	 * @return The requested {@link CANEncoder}, if found
	 */
	default CANEncoder getSparkEncoder(String sparkName) {
		return sparkEncoders.get(sparkName);
	}
}