package org.usfirst.lib6647.json;

import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.SimEnabler;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * This class enables JSON functionality, specifically for the
 * {@link JController} and {@link SuperSubsystem} classes.
 */
public class JSONRobot extends TimedRobot {
	/**
	 * Constructor for {@link JSONRobot} with a default period time of 0.02s.
	 */
	protected JSONRobot() {
		this(0.02);
	}

	/**
	 * Constructor for {@link JSONRobot} with a specific period time.
	 * 
	 * @param period The robot's period time, in seconds
	 */
	protected JSONRobot(double period) {
		super(period);

		// Make sure ~/lvuser/deploy/Profiles.json and ~/lvuser/deploy/RobotMap.json
		// both exist.
		JSONReader.createInstance("Profiles", "RobotMap");

		if (!isReal())
			SmartDashboard.putData(new SimEnabler());
	}
}