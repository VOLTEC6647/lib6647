package org.usfirst.lib6647.subsystem;

import java.io.FileReader;
import java.io.Reader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Abstract class to allow usage of {@link #robotMap JSON files} for
 * {@link PIDSubsystem} creation, with additional PID initialization.
 */
public abstract class PIDSuperSubsystem extends PIDSubsystem {

	/**
	 * Bread and butter of {@link PIDSuperSubsystem}.
	 */
	protected JSONObject robotMap;
	protected float p = 0.0f, i = 0.0f, d = 0.0f;
	protected double pidOutput;

	/**
	 * Constructor for {@link PIDSuperSubsystem}. Initializes the
	 * {@link PIDSubsystem} with 0.0f, 0.0f, and 0.0f as PID values, then replaces
	 * them with the values declared in {@link #robotMap}
	 * 
	 * @param name     (of the {@link PIDSubsystem})
	 * @param fileName (to {@link #robotMap JSON file})
	 */
	public PIDSuperSubsystem(String name, String fileName) {
		super(name, 0.0f, 0.0f, 0.0f);

		initJSON(fileName);
		initPID();
		outputPIDValues(getName(), p, i, d);
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
			DriverStation.reportError(
					"[!] SUBSYSTEM '" + getName().toUpperCase() + "' JSON INIT ERROR: " + e.getMessage(), false);
			System.out.println("[!] SUBSYSTEM '" + getName().toUpperCase() + "' JSON INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Method to initialize and set {@link PIDSuperSubsystem}'s PID values and
	 * configuration.
	 */
	private void initPID() {
		try {
			// Create a JSONObject out of the 'pid' key.
			JSONObject pid = (JSONObject) ((JSONObject) ((JSONObject) robotMap.get("subsystems")).get(getName()))
					.get("pid");

			// Update current PID values.
			p = Float.parseFloat(pid.get("p").toString());
			i = Float.parseFloat(pid.get("i").toString());
			d = Float.parseFloat(pid.get("d").toString());

			// Update PIDSubsystem PID values and configuration.
			getPIDController().setPID(p, i, d);
			setInputRange(Double.parseDouble(pid.get("inputMin").toString()),
					Double.parseDouble(pid.get("inputMax").toString()));
			setOutputRange(Double.parseDouble(pid.get("outputMin").toString()),
					Double.parseDouble(pid.get("outputMax").toString()));
			setAbsoluteTolerance(Double.parseDouble(pid.get("absoluteTolerance").toString()));
			getPIDController().setContinuous(Boolean.parseBoolean(pid.get("continuous").toString()));

			// Clear JSONObject after use, not sure if it does anything, but it might free
			// some unused memory.
			pid.clear();
		} catch (Exception e) {
			DriverStation.reportError(
					"[!] SUBSYSTEM '" + getName().toUpperCase() + "' PID INIT ERROR: " + e.getMessage(), false);
			System.out.println("[!] SUBSYSTEM '" + getName().toUpperCase() + "' PID INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
	}

	/**
	 * Method to clear {@link #robotMap}.
	 */
	public void finishedJSONInit() {
		robotMap.clear();
	}

	/**
	 * Method to output {@link #p}, {@link #i}, and {@link #d} values from the
	 * {@link PIDSuperSubsystem} to the {@link SmartDashboard}.
	 * 
	 * @param subsystemName
	 * @param {@link        #p}
	 * @param {@link        #i}
	 * @param {@link        #d}
	 */
	private void outputPIDValues(String subsystemName, float p, float i, float d) {
		SmartDashboard.putString(subsystemName + "P", p + "");
		SmartDashboard.putString(subsystemName + "I", i + "");
		SmartDashboard.putString(subsystemName + "D", d + "");
	}

	/**
	 * Method to update {@link #p}, {@link #i}, and {@link #d} values as float from
	 * the {@link SmartDashboard}.
	 */
	public void updatePIDValues() {
		p = Float.parseFloat(SmartDashboard.getString(getName() + "P", p + ""));
		i = Float.parseFloat(SmartDashboard.getString(getName() + "I", i + ""));
		d = Float.parseFloat(SmartDashboard.getString(getName() + "D", d + ""));

		getPIDController().setPID(p, i, d);
	}
}