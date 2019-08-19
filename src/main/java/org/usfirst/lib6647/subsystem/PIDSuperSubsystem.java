package org.usfirst.lib6647.subsystem;

import java.io.FileReader;
import java.io.Reader;

import com.fasterxml.jackson.databind.JsonNode;

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
	protected JsonNode robotMap;
	protected double p = 0.0, i = 0.0, d = 0.0;
	protected double pidOutput;

	/**
	 * Constructor for {@link PIDSuperSubsystem}. Initializes the
	 * {@link PIDSubsystem} with 0.0f, 0.0f, and 0.0f as PID values, then replaces
	 * them with the values declared in {@link #robotMap}
	 * 
	 * @param name     (of the {@link PIDSubsystem})
	 * @param fileName (to {@link #robotMap JSON file})
	 */
	public PIDSuperSubsystem(String name) {
		super(name, 0.0, 0.0, 0.0);

		try (Reader file = new FileReader(RobotMap.getInstance().getFilePath())) {
			robotMap = RobotMap.getInstance().getMapper().readTree(file).get(getName());
		} catch (Exception e) {
			System.out.println("[!] SUBSYSTEM '" + getName().toUpperCase() + "' JSON INIT ERROR: " + e.getMessage());
			System.exit(1);
		}

		initPID();
		outputPIDValues(getName(), p, i, d);
	}

	/**
	 * Method to initialize and set {@link PIDSuperSubsystem}'s PID values and
	 * configuration.
	 */
	private void initPID() {
		try {
			// Get JsonNode out of the 'pid' key.
			JsonNode pid = robotMap.get("pid");

			// Update current PID values.
			p = pid.get("p").asDouble();
			i = pid.get("i").asDouble();
			d = pid.get("d").asDouble();

			// Update PIDSubsystem PID values and configuration.
			getPIDController().setPID(p, i, d);
			setInputRange(pid.get("inputMin").asDouble(), pid.get("inputMax").asDouble());
			setOutputRange(pid.get("outputMin").asDouble(), pid.get("outputMax").asDouble());
			setAbsoluteTolerance(pid.get("absoluteTolerance").asDouble());
			getPIDController().setContinuous(pid.get("continuous").asBoolean());
		} catch (Exception e) {
			System.out.println("[!] SUBSYSTEM '" + getName().toUpperCase() + "' PID INIT ERROR: " + e.getMessage());
			System.exit(1);
		}
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
	private void outputPIDValues(String subsystemName, double p, double i, double d) {
		SmartDashboard.putString(subsystemName + "P", p + "");
		SmartDashboard.putString(subsystemName + "I", i + "");
		SmartDashboard.putString(subsystemName + "D", d + "");
	}

	/**
	 * Method to update {@link #p}, {@link #i}, and {@link #d} values as float from
	 * the {@link SmartDashboard}.
	 */
	public void updatePIDValues() {
		p = Double.parseDouble(SmartDashboard.getString(getName() + "P", p + ""));
		i = Double.parseDouble(SmartDashboard.getString(getName() + "I", i + ""));
		d = Double.parseDouble(SmartDashboard.getString(getName() + "D", d + ""));

		getPIDController().setPID(p, i, d);
	}

	/**
	 * Method to return {@link #pidOutput}, must be updated in a PID loop in order
	 * to be useful.
	 * 
	 * @return pidOutput
	 */
	public double getPIDOutput() {
		return pidOutput;
	}
}