package org.usfirst.lib6647.subsystem;

import java.io.FileReader;
import java.io.Reader;

import com.fasterxml.jackson.databind.JsonNode;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Abstract class to allow usage of {@link #robotMap JSON files} for
 * {@link Subsystem} creation.
 */
public abstract class SuperSubsystem extends Subsystem {

	/**
	 * Bread and butter of {@link SuperSubsystem}.
	 */
	protected JsonNode robotMap;

	/**
	 * Constructor for {@link SuperSubsystem}.
	 * 
	 * @param name (of the {@link Subsystem})
	 */
	public SuperSubsystem(String name) {
		super(name);

		try (Reader file = new FileReader(RobotMap.getInstance().getFilePath())) {
			robotMap = RobotMap.getInstance().getMapper().readTree(file).get(getName());
		} catch (Exception e) {
			System.out.println("[!] SUBSYSTEM '" + getName().toUpperCase() + "' JSON INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
	}
}