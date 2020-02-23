/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.lib6647.wpilib;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Watchdog;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * IterativeRobotBase implements a specific type of robot program framework,
 * extending the RobotBase class.
 *
 * <p>
 * The IterativeRobotBase class does not implement startCompetition(), so it
 * should not be used by teams directly.
 *
 * <p>
 * This class provides the following functions which are called by the main
 * loop, startCompetition(), at the appropriate times:
 *
 * <p>
 * robotInit() -- provide for initialization at robot power-on
 *
 * <p>
 * init() functions -- each of the following functions is called once when the
 * appropriate mode is entered: - disabledInit() -- called each and every time
 * disabled is entered from another mode - autonomousInit() -- called each and
 * every time autonomous is entered from another mode - teleopInit() -- called
 * each and every time teleop is entered from another mode - testInit() --
 * called each and every time test is entered from another mode
 *
 * <p>
 * periodic() functions -- each of these functions is called on an interval: -
 * robotPeriodic() - disabledPeriodic() - autonomousPeriodic() -
 * teleopPeriodic() - testPeriodic()
 */
@SuppressWarnings("PMD.TooManyMethods")
public abstract class IterativeRobotBase extends RobotBase {
	protected double period;

	private enum Mode {
		kNone, kDisabled, kAutonomous, kTeleop, kTest
	}

	private Mode lastMode = Mode.kNone;
	private final Watchdog watchdog;

	/**
	 * Constructor for IterativeRobotBase.
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
		System.out.println("Default robotInit() method... Override me!");
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
		System.out.println("Default simulationInit() method... Override me!");
	}

	/**
	 * Initialization code for disabled mode should go here.
	 *
	 * <p>
	 * Users should override this method for initialization code which will be
	 * called each time the robot enters disabled mode.
	 */
	public void disabledInit() {
		System.out.println("Default disabledInit() method... Override me!");
	}

	/**
	 * Initialization code for autonomous mode should go here.
	 *
	 * <p>
	 * Users should override this method for initialization code which will be
	 * called each time the robot enters autonomous mode.
	 */
	public void autonomousInit() {
		System.out.println("Default autonomousInit() method... Override me!");
	}

	/**
	 * Initialization code for teleop mode should go here.
	 *
	 * <p>
	 * Users should override this method for initialization code which will be
	 * called each time the robot enters teleop mode.
	 */
	public void teleopInit() {
		System.out.println("Default teleopInit() method... Override me!");
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
		System.out.println("Default testInit() method... Override me!");
	}

	/* ----------- Overridable periodic code ----------------- */

	private boolean m_rpFirstRun = true;

	/**
	 * Periodic code for all robot modes should go here.
	 */
	public void robotPeriodic() {
		if (m_rpFirstRun) {
			System.out.println("Default robotPeriodic() method... Override me!");
			m_rpFirstRun = false;
		}
	}

	private boolean m_spFirstRun = true;

	/**
	 * Periodic simulation code should go here.
	 *
	 * <p>
	 * This function is called in a simulated robot after user code executes.
	 */
	public void simulationPeriodic() {
		if (m_spFirstRun) {
			System.out.println("Default simulationPeriodic() method... Override me!");
			m_spFirstRun = false;
		}
	}

	private boolean m_dpFirstRun = true;

	/**
	 * Periodic code for disabled mode should go here.
	 */
	public void disabledPeriodic() {
		if (m_dpFirstRun) {
			System.out.println("Default disabledPeriodic() method... Override me!");
			m_dpFirstRun = false;
		}
	}

	private boolean m_apFirstRun = true;

	/**
	 * Periodic code for autonomous mode should go here.
	 */
	public void autonomousPeriodic() {
		if (m_apFirstRun) {
			System.out.println("Default autonomousPeriodic() method... Override me!");
			m_apFirstRun = false;
		}
	}

	private boolean m_tpFirstRun = true;

	/**
	 * Periodic code for teleop mode should go here.
	 */
	public void teleopPeriodic() {
		if (m_tpFirstRun) {
			System.out.println("Default teleopPeriodic() method... Override me!");
			m_tpFirstRun = false;
		}
	}

	private boolean m_tmpFirstRun = true;

	/**
	 * Periodic code for test mode should go here.
	 */
	@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
	public void testPeriodic() {
		if (m_tmpFirstRun) {
			System.out.println("Default testPeriodic() method... Override me!");
			m_tmpFirstRun = false;
		}
	}

	protected void loopFunc() {
		watchdog.reset();

		// Call the appropriate function depending upon the current robot mode
		if (isDisabled()) {
			// Call DisabledInit() if we are now just entering disabled mode from either a
			// different mode
			// or from power-on.
			if (lastMode != Mode.kDisabled) {
				LiveWindow.setEnabled(false);
				Shuffleboard.disableActuatorWidgets();
				disabledInit();
				watchdog.addEpoch("disabledInit()");
				lastMode = Mode.kDisabled;
			}

			HAL.observeUserProgramDisabled();
			disabledPeriodic();
			watchdog.addEpoch("disablePeriodic()");
		} else if (isAutonomous()) {
			// Call AutonomousInit() if we are now just entering autonomous mode from either
			// a different
			// mode or from power-on.
			if (lastMode != Mode.kAutonomous) {
				LiveWindow.setEnabled(false);
				Shuffleboard.disableActuatorWidgets();
				autonomousInit();
				watchdog.addEpoch("autonomousInit()");
				lastMode = Mode.kAutonomous;
			}

			HAL.observeUserProgramAutonomous();
			autonomousPeriodic();
			watchdog.addEpoch("autonomousPeriodic()");
		} else if (isOperatorControl()) {
			// Call TeleopInit() if we are now just entering teleop mode from either a
			// different mode or
			// from power-on.
			if (lastMode != Mode.kTeleop) {
				LiveWindow.setEnabled(false);
				Shuffleboard.disableActuatorWidgets();
				teleopInit();
				watchdog.addEpoch("teleopInit()");
				lastMode = Mode.kTeleop;
			}

			HAL.observeUserProgramTeleop();
			teleopPeriodic();
			watchdog.addEpoch("teleopPeriodic()");
		} else {
			// Call TestInit() if we are now just entering test mode from either a different
			// mode or from
			// power-on.
			if (lastMode != Mode.kTest) {
				LiveWindow.setEnabled(true);
				Shuffleboard.enableActuatorWidgets();
				testInit();
				watchdog.addEpoch("testInit()");
				lastMode = Mode.kTest;
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
	}

	private void printLoopOverrunMessage() {
	}
}
