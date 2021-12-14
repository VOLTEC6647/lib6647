package org.usfirst.lib6647.math;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import net.jafama.FastMath;

public class FastMathConfig {
	public void initialize() {
		try (var file = new FileInputStream(Filesystem.getDeployDirectory() + "FastMath.properties")) {
			new Properties().load(file);
			FastMath.initTables();
		} catch (FileNotFoundException e) {
			var error = "[!] FASTMATH PROPERTIES FILE (FastMath.properties) NOT FOUND IN DEPLOY DIRECTORY!\n\t"
					+ e.getLocalizedMessage() + "\nUSING DEFAULT VALUES...";

			Logger.getGlobal().warning(() -> error);
			DriverStation.reportWarning(error, false);
		} catch (IOException e) {
			var error = "[!] COULD NOT ACCESS FASTMATH PROPERTIES FILE (FastMath.properties) IN DEPLOY DIRECTORY!\n\t"
					+ e.getLocalizedMessage() + "\nUSING DEFAULT VALUES...";

			Logger.getGlobal().warning(() -> error);
			DriverStation.reportWarning(error, false);
		}
	}
}