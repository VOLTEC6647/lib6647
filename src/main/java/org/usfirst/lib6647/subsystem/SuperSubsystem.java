package org.usfirst.lib6647.subsystem;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.util.JSONInitException;
import org.usfirst.lib6647.util.JSONReader;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Subsystem;

/**
 * Abstract class to allow usage of {@link #robotMap JSON files} for
 * {@link Subsystem} creation.
 */
public abstract class SuperSubsystem implements Subsystem {
	/** Bread and butter of {@link SuperSubsystem}. */
	protected JsonNode robotMap;
	/** Name of the {@link SuperSubsystem}. */
	private final String name;

	/**
	 * Constructor for {@link SuperSubsystem}.
	 * 
	 * @param name (of the {@link Subsystem})
	 */
	public SuperSubsystem(String name) {
		this.name = name;

		try {
			robotMap = JSONReader.getInstance().getNode("RobotMap", name);
		} catch (JSONInitException e) {
			String error = String.format("[!] SUBSYSTEM '%1$s' JSON INIT ERROR:\n\t%2$s", name.toUpperCase(),
					e.getLocalizedMessage());

			System.out.println(error);
			DriverStation.reportError(error, false);

			System.exit(1);
		}
	}

	/**
	 * Gets {@link SuperSubsystem}'s {@link SuperSubsystem#name name}.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	// Optional design pattern for caching periodic reads to avoid hammering the
	// HAL/CAN.
	public void readPeriodicInputs() {
	}

	// Optional design pattern for caching periodic writes to avoid hammering the
	// HAL/CAN.
	public void writePeriodicOutputs() {
	}

	public void registerLoops(ILooper looper) {
	}

	public void zeroSensors() {
	}
}