package org.usfirst.lib6647.subsystem;

import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.json.JSONInitException;
import org.usfirst.lib6647.json.JSONReader;
import org.usfirst.lib6647.loops.ILooper;
import org.usfirst.lib6647.loops.Loop;
import org.usfirst.lib6647.loops.Looper;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Abstract class to allow usage of {@link #robotMap JSON files} for
 * {@link SubsystemBase Subsystem} creation.
 */
public abstract class SuperSubsystem extends SubsystemBase {
	/** Bread and butter of {@link SuperSubsystem}. */
	protected JsonNode robotMap;
	/** Name of the {@link SuperSubsystem}. */
	private final String name;
	/**
	 * The {@link ShuffleboardLayout layout} to update in the {@link Shuffleboard}.
	 */
	protected ShuffleboardLayout layout;

	/**
	 * Constructor for {@link SuperSubsystem}.
	 * 
	 * @param name The name of the {@link SuperSubsystem}
	 */
	public SuperSubsystem(String name) {
		this.name = name;

		layout = Shuffleboard.getTab("Robot").getLayout(name.substring(0, 1).toUpperCase() + name.substring(1),
				BuiltInLayouts.kList);

		try {
			robotMap = JSONReader.getInstance().getNode("RobotMap", name);
		} catch (JSONInitException e) {
			String error = String.format("[!] SUBSYSTEM '%1$s' JSON INIT ERROR:%n\t%2$s", name.toUpperCase(),
					e.getLocalizedMessage());

			Logger.getGlobal().severe(() -> error);
			DriverStation.reportError(error, false);

			System.exit(1);
		}
	}

	/**
	 * Gets {@link SuperSubsystem}'s {@link #name}.
	 * 
	 * @return The {@link SuperSubsystem}'s name
	 */
	public String getSubsystemName() {
		return name;
	}

	// TODO: Rewrite these to actually be useful with our project structure.

	/**
	 * Optional design pattern for caching periodic reads to avoid hammering the
	 * HAL/CAN.
	 */
	public synchronized void readPeriodicInputs() {
	}

	/**
	 * Optional design pattern for caching periodic writes to avoid hammering the
	 * HAL/CAN.
	 */
	public synchronized void writePeriodicOutputs() {
	}

	/**
	 * {@link Loop Loops} are to be registered in this method.
	 * 
	 * @param looper The {@link Looper} with which to register the {@link Loop
	 *               Loops}
	 */
	public void registerLoops(ILooper looper) {
	}

	/**
	 * Optional method to reset sensors right before a {@link Loop} begins.
	 */
	public void zeroSensors() {
	}

	/**
	 * Optional method to run any {@link Shuffleboard} {@link #layout} data output.
	 */
	public void outputToShuffleboard() {
	}
}