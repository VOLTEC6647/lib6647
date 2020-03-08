package org.usfirst.lib6647.subsystem.supercomponents;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;

import org.usfirst.lib6647.subsystem.ComponentInitException;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.subsystem.hypercomponents.HyperSparkMax;
import org.usfirst.lib6647.util.REVUtil;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Interface to allow {@link HyperSparkMax} initialization via JSON.
 * 
 * <p>
 * Subsystems declared need to extend {@link SuperSubsystem} and implement this
 * interface in order to initialize {@link HyperSparkMax HyperSparkMax objects}
 * declared in {@link SuperSubsystem#robotMap}.
 */
public interface SuperSparkMax {
	/**
	 * HashMap storing the {@link SuperSubsystem}'s {@link HyperSparkMax} instances.
	 */
	final Map<String, HyperSparkMax> sparks = new HashMap<>();

	/**
	 * Method to initialize {@link HyperSparkMax HyperSparkMax objects} declared in
	 * the {@link SuperSubsystem#robotMap JSON file}, and add them to the
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

					// Create HyperSparkMax object.
					var spark = new HyperSparkMax(json.get("name").asText(), json.get("port").asInt(), type);
					var controller = spark.getPIDController();

					spark.restoreFactoryDefaults();

					// TODO: Mirror actual SparkMax configuration from http://www.revrobotics.com/sparkmax-users-manual/#appendix-a.
					// Additional initialization configuration.
					spark.setInverted(json.hasNonNull("inverted") ? json.get("inverted").asBoolean() : false);

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

					if (json.hasNonNull("encoder") && json.get("encoder").hasNonNull("type")
							&& json.get("encoder").hasNonNull("countsPerRev")) {
						var encoder = json.get("encoder");

						spark.setEncoder(REVUtil.getEncoderType(encoder.get("type").asText()),
								encoder.get("countsPerRev").asInt());
					} else
						spark.setEncoder();

					if (json.hasNonNull("alternateEncoder") && json.get("alternateEncoder").hasNonNull("type")
							&& json.get("alternateEncoder").hasNonNull("countsPerRev")) {
						var alternateEncoder = json.get("alternateEncoder");

						spark.setAlternateEncoder(
								REVUtil.getAlternateEncoderType(alternateEncoder.get("type").asText()),
								alternateEncoder.get("countsPerRev").asInt());
					} else
						spark.setAlternateEncoder();

					if (json.hasNonNull("pid")) {
						var pid = json.get("pid");

						for (int i = 0; i < 4; i++) {
							if (pid.hasNonNull("slot" + i)) {
								var slot = pid.get("slot" + i);

								controller.setP(slot.hasNonNull("p") ? slot.get("p").asDouble() : 0.0, i);
								controller.setI(slot.hasNonNull("i") ? slot.get("i").asDouble() : 0.0, i);
								controller.setD(slot.hasNonNull("d") ? slot.get("d").asDouble() : 0.0, i);
								controller.setFF(slot.hasNonNull("f") ? slot.get("f").asDouble() : 0.0, i);

								controller.setIZone(slot.hasNonNull("iZone") ? slot.get("iZone").asDouble() : 0.0);
								controller.setOutputRange(
										slot.hasNonNull("outputMin") ? slot.get("outputMin").asDouble(-1) : -1,
										slot.hasNonNull("outputMax") ? slot.get("outputMax").asDouble(1) : 1);
							}
						}
					}
					// ...

					// Put object in HashMap with its declared name as key after initialization and
					// configuration.
					sparks.put(json.get("name").asText(), spark);
				}
			} catch (Exception e) {
				System.out.println(e.getLocalizedMessage());
				DriverStation.reportError(e.getLocalizedMessage(), false);
			}
		});
	}

	/**
	 * Gets specified {@link HyperSparkMax} from the {@link #sparks} HashMap.
	 * 
	 * @param sparkName The name of the {@link HyperSparkMax}
	 * @return The requested {@link HyperSparkMax}, if found
	 */
	default HyperSparkMax getSpark(String sparkName) {
		return sparks.get(sparkName);
	}
}