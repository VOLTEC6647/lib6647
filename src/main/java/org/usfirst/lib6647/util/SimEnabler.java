package org.usfirst.lib6647.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;

/**
 * Class to enable characterization of simulations.
 * 
 * <p>
 * Copied over from:
 * https://github.com/wpilibsuite/frc-characterization/blob/master/frc_characterization/robot/project/src/main/java/dc/SimEnabler.java.
 */
public class SimEnabler implements Sendable {
	public SimEnabler() {
		SendableRegistry.setName(this, "SimEnabler");
		DriverStationSim.setAutonomous(true);
	}

	public void setEnabled(boolean enabled) {
		DriverStationSim.setEnabled(enabled);
		DriverStationSim.notifyNewData();

		DriverStation.getInstance().isNewControlData();
		while (DriverStation.getInstance().isEnabled() != enabled) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO: Properly format this error.
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.addBooleanProperty("Enabled", DriverStation.getInstance()::isEnabled, this::setEnabled);
	}
}