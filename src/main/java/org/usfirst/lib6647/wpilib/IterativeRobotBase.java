/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.lib6647.wpilib;

import java.util.logging.Logger;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * {@link IterativeRobotBase} implements a specific type of robot program
 * framework, extending the RobotBase class.
 *
 * <p>
 * The {@link IterativeRobotBase} class does not implement
 * {@link #startCompetition()}, so it should not be used by teams directly.
 * 
 * <p>
 * This class provides the following functions which are called by the main
 * loop, {@link #startCompetition()}, at the appropriate times:
 *
 * <p>
 * {@link #robotInit()} -- provide for initialization at robot power-on
 *
 * <p>
 * init() functions -- each of the following functions is called once when the
 * appropriate mode is entered:
 * <p>
 * - {@link #disabledInit()} -- called each and
 * every time disabled is entered from another mode
 * <p>
 * - {@link #autonomousInit()}
 * -- called each and every time autonomous is entered from another mode
 * <p>
 * - {@link #teleopInit()} -- called each and every time teleop is entered from
 * another mode
 * <p>
 * - {@link #testInit()} -- called each and every time test is
 * entered from another mode
 *
 * <p>
 * periodic() functions -- each of these functions is called on an interval:
 * <p>
 * - {@link #robotPeriodic()}
 * <p>
 * - {@link #disabledPeriodic()}
 * <p>
 * - {@link #autonomousPeriodic()}
 * <p>
 * - {@link #teleopPeriodic()}
 * <p>
 * - {@link #testPeriodic()}
 */
public abstract class IterativeRobotBase extends RobotBase {
	protected double period;

	private enum Mode {
		NONE, DISABLED, AUTONOMOUS, TELEOP, TEST
	}

	private Mode lastMode = Mode.NONE;
	private final Watchdog watchdog;
	private boolean ntFlushEnabled;

	/**
	 * Constructor for {@link IterativeRobotBase}.
	 *
	 * @param period Period in seconds.
	 */
	protected IterativeRobotBase(double period) {
		this.period = period;
		watchdog = new Watchdog(period, this::printLoopOverrunMessage);

		watchdog.disable();
		watchdog.suppressTimeoutMessage(true);

		Thread.currentThread().setPriority(9);
		Thread.currentThread().setName("RobotMainThread");
	}

	/**
	 * Provide an alternate "main loop" via startCompetition().
	 */
	@Override
	public abstract void startCompetition();

	/* ----------- Overridable initialization code ----------------- */

	/**
	 * Robot-wide initialization code should go here.
	 *
	 * <p>
	 * Users should override this method for default Robot-wide initialization which
	 * will be called when the robot is first powered on. It will be called exactly
	 * one time.
	 *
	 * <p>
	 * Warning: the Driver Station "Robot Code" light and FMS "Robot Ready"
	 * indicators will be off until RobotInit() exits. Code in RobotInit() that
	 * waits for enable will cause the robot to never indicate that the code is
	 * ready, causing the robot to be bypassed in a match.
	 */
	public void robotInit() {
		Logger.getGlobal().info("Default robotInit() method... Override me!");
	}

	/**
	 * Robot-wide simulation initialization code should go here.
	 *
	 * <p>
	 * Users should override this method for default Robot-wide simulation related
	 * initialization which will be called when the robot is first started. It will
	 * be called exactly one time after RobotInit is called only when the robot is
	 * in simulation.
	 */
	public void simulationInit() {
		Logger.getGlobal().info("Default simulationInit() method... Override me!");
	}

	/**
	 * Initialization code for disabled mode should go here.
	 *
	 * <p>
	 * Users should override this method for initialization code which will be
	 * called each time the robot enters disabled mode.
	 */
	public void disabledInit() {
		Logger.getGlobal().info("Default disabledInit() method... Override me!");
	}

	/**
	 * Initialization code for autonomous mode should go here.
	 *
	 * <p>
	 * Users should override this method for initialization code which will be
	 * called each time the robot enters autonomous mode.
	 */
	public void autonomousInit() {
		Logger.getGlobal().info("Default autonomousInit() method... Override me!");
	}

	/**
	 * Initialization code for teleop mode should go here.
	 *
	 * <p>
	 * Users should override this method for initialization code which will be
	 * called each time the robot enters teleop mode.
	 */
	public void teleopInit() {
		Logger.getGlobal().info("Default teleopInit() method... Override me!");
	}

	/**
	 * Initialization code for test mode should go here.
	 *
	 * <p>
	 * Users should override this method for initialization code which will be
	 * called each time the robot enters test mode.
	 */
	@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
	public void testInit() {
		Logger.getGlobal().info("Default testInit() method... Override me!");
	}

	/* ----------- Overridable periodic code ----------------- */

	private boolean rpFirstRun = true;

	/**
	 * Periodic code for all robot modes should go here.
	 */
	public void robotPeriodic() {
		if (rpFirstRun) {
			Logger.getGlobal().info("Default robotPeriodic() method... Override me!");
			rpFirstRun = false;
		}
	}

	private boolean spFirstRun = true;

	/**
	 * Periodic simulation code should go here.
	 *
	 * <p>
	 * This function is called in a simulated robot after user code executes.
	 */
	public void simulationPeriodic() {
		if (spFirstRun) {
			Logger.getGlobal().info("Default simulationPeriodic() method... Override me!");
			spFirstRun = false;
		}
	}

	private boolean dpFirstRun = true;

	/**
	 * Periodic code for disabled mode should go here.
	 */
	public void disabledPeriodic() {
		if (dpFirstRun) {
			Logger.getGlobal().info("Default disabledPeriodic() method... Override me!");
			dpFirstRun = false;
		}
	}

	private boolean apFirstRun = true;

	/**
	 * Periodic code for autonomous mode should go here.
	 */
	public void autonomousPeriodic() {
		if (apFirstRun) {
			Logger.getGlobal().info("Default autonomousPeriodic() method... Override me!");
			apFirstRun = false;
		}
	}

	private boolean tpFirstRun = true;

	/**
	 * Periodic code for teleop mode should go here.
	 */
	public void teleopPeriodic() {
		if (tpFirstRun) {
			Logger.getGlobal().info("Default teleopPeriodic() method... Override me!");
			tpFirstRun = false;
		}
	}

	private boolean tmpFirstRun = true;

	/**
	 * Periodic code for test mode should go here.
	 */
	@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
	public void testPeriodic() {
		if (tmpFirstRun) {
			Logger.getGlobal().info("Default testPeriodic() method... Override me!");
			tmpFirstRun = false;
		}
	}

	/**
	 * Enables or disables flushing NetworkTables every loop iteration. By default,
	 * this is disabled.
	 *
	 * @param enabled True to enable, false to disable
	 */
	public void setNetworkTablesFlushEnabled(boolean enabled) {
		ntFlushEnabled = enabled;
	}

	protected void loopFunc() {
		watchdog.reset();

		// Call the appropriate function depending upon the current robot mode
		if (isDisabled()) {
			// Call DisabledInit() if we are now just entering disabled mode from either a
			// different mode
			// or from power-on.
			if (lastMode != Mode.DISABLED) {
				LiveWindow.setEnabled(false);
				Shuffleboard.disableActuatorWidgets();
				disabledInit();
				watchdog.addEpoch("disabledInit()");
				lastMode = Mode.DISABLED;
			}

			HAL.observeUserProgramDisabled();
			disabledPeriodic();
			watchdog.addEpoch("disablePeriodic()");
		} else if (isAutonomous()) {
			// Call AutonomousInit() if we are now just entering autonomous mode from either
			// a different
			// mode or from power-on.
			if (lastMode != Mode.AUTONOMOUS) {
				LiveWindow.setEnabled(false);
				Shuffleboard.disableActuatorWidgets();
				autonomousInit();
				watchdog.addEpoch("autonomousInit()");
				lastMode = Mode.AUTONOMOUS;
			}

			HAL.observeUserProgramAutonomous();
			autonomousPeriodic();
			watchdog.addEpoch("autonomousPeriodic()");
		} else if (isOperatorControl()) {
			// Call TeleopInit() if we are now just entering teleop mode from either a
			// different mode or
			// from power-on.
			if (lastMode != Mode.TELEOP) {
				LiveWindow.setEnabled(false);
				Shuffleboard.disableActuatorWidgets();
				teleopInit();
				watchdog.addEpoch("teleopInit()");
				lastMode = Mode.TELEOP;
			}

			HAL.observeUserProgramTeleop();
			teleopPeriodic();
			watchdog.addEpoch("teleopPeriodic()");
		} else {
			// Call TestInit() if we are now just entering test mode from either a different
			// mode or from
			// power-on.
			if (lastMode != Mode.TEST) {
				LiveWindow.setEnabled(true);
				Shuffleboard.enableActuatorWidgets();
				testInit();
				watchdog.addEpoch("testInit()");
				lastMode = Mode.TEST;
			}

			HAL.observeUserProgramTest();
			testPeriodic();
			watchdog.addEpoch("testPeriodic()");
		}

		robotPeriodic();
		watchdog.addEpoch("robotPeriodic()");

		SmartDashboard.updateValues();
		watchdog.addEpoch("SmartDashboard.updateValues()");
		LiveWindow.updateValues();
		watchdog.addEpoch("LiveWindow.updateValues()");
		Shuffleboard.update();
		watchdog.addEpoch("Shuffleboard.update()");

		if (isSimulation()) {
			simulationPeriodic();
			watchdog.addEpoch("simulationPeriodic()");
		}

		watchdog.disable();

		// Flush NetworkTables
		if (ntFlushEnabled) {
			NetworkTableInstance.getDefault().flush();
		}

		// Warn on loop time overruns
		if (watchdog.isExpired()) {
			// watchdog.printEpochs();
		}
	}

	private void printLoopOverrunMessage() {
		// DriverStation.reportWarning("Loop time of " + period + "s overrun\n", false);
	}
}
