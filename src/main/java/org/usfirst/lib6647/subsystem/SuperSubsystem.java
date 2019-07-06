package org.usfirst.lib6647.subsystem;

import java.io.FileReader;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * Abstract class to allow usage of {@link #robotMap JSON files} for
 * {@link Subsystem} creation.
 */
public abstract class SuperSubsystem extends Subsystem {

	/**
	 * Bread and butter of {@link SuperSubsystem}.
	 */
	protected JSONObject robotMap;

	/**
	 * Constructor for {@link SuperSubsystem}.
	 * 
	 * @param name     (of the {@link Subsystem})
	 * @param fileName (to {@link #robotMap JSON file})
	 */
	public SuperSubsystem(String name, String fileName) {
		super(name);

		initJSON(fileName);
	}

	/**
	 * Method to initialize {@link #robotMap} at the given path.
	 * 
	 * @param fileName (to {@link #robotMap JSON file})
	 */
	private void initJSON(String fileName) {
		try {
			JSONParser parser = new JSONParser();
			Reader file = new FileReader(fileName);
			robotMap = (JSONObject) parser.parse(file);
			file.close();
		} catch (Exception e) {
			System.out.println("[!] SUBSYSTEM '" + getName().toUpperCase() + "' JSON INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Method to clear {@link #robotMap}.
	 */
	public void finishedJSONInit() {
		robotMap.clear();
	}
}