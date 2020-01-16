package org.usfirst.lib6647.wpilib;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.usfirst.lib6647.loops.Looper;
import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.RobotMap;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.JSONReader;

import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * Implementation of Team 254's {@link Looper Loopers} within a
 * {@link TimedRobot}. It features three {@link Looper} instances which run
 * whenever the robot is either enabled or disabled, or periodically.
 */
public abstract class LooperRobot extends TimedRobot {
	/** The {@link LooperRobot}'s main {@link Looper Loopers}. */
	private final Looper enabledLooper = new Looper("enabled"), teleopLooper = new Looper("teleop"),
			autoLooper = new Looper("auto"), disabledLooper = new Looper("disabled");
	/** Instance of {@link RobotMap}. */
	private final RobotMap robotMap = new RobotMap();
	/** HashMap holding initialized {@link JController joysticks}. */
	protected final HashMap<String, JController> joysticks = new HashMap<>();

	/**
	 * Constructor for {@link LooperRobot} with default period. Every subsystem
	 * provided via lambda syntax (Chassis::new, for instance) will be registered in
	 * {@link RobotMap}.
	 * 
	 * @param <T>
	 * @param period
	 * @param subsystems
	 */
	@SafeVarargs
	protected <T extends SuperSubsystem> LooperRobot(Supplier<T>... subsystems) {
		this(0.02, subsystems);
	}

	/**
	 * Constructor for {@link LooperRobot} with specified period. Every subsystem
	 * provided via lambda syntax (Chassis::new, for instance) will be registered in
	 * {@link RobotMap}.
	 * 
	 * @param <T>
	 * @param period
	 * @param subsystems
	 */
	@SafeVarargs
	protected <T extends SuperSubsystem> LooperRobot(double period, Supplier<T>... subsystems) {
		super(period);

		// Make sure ~/lvuser/deploy/Profiles.json and ~/lvuser/deploy/RobotMap.json
		// both exist.
		JSONReader.createInstance("Profiles", "RobotMap");

		// Run JController initialization.
		initJoysticks();

		// Register each given subsystem.
		for (Supplier<T> s : subsystems)
			robotMap.registerSubsystem(s.get());
	}

	@Override
	public void robotInit() {
		// Registers each Loop in every declared subsystem.
		robotMap.registerLoops(enabledLooper, teleopLooper, autoLooper, disabledLooper);

		System.out.println("Default LooperRobot robotInit() method... Override me!");
	}

	// Can be overwritten if it is not necessary to call the CommandScheduler
	// periodically.
	@Override
	public void robotPeriodic() {
		CommandScheduler.getInstance().run();
	}

	@Override
	public void disabledInit() {
		// Start disabled loops, stop enabled, teleop, and auto.
		enabledLooper.stop();
		teleopLooper.stop();
		autoLooper.stop();
		disabledLooper.start();

		System.out.println("Default LooperRobot disabledInit() method... Override me!");
	}

	@Override
	public void autonomousInit() {
		// Start enabled & auto loops, stop teleop & disabled.
		enabledLooper.start();
		teleopLooper.stop();
		autoLooper.start();
		disabledLooper.stop();

		System.out.println("Default LooperRobot autonomousInit() method... Override me!");
	}

	@Override
	public void teleopInit() {
		// Start enabled & teleop loops, stop auto & disabled.
		enabledLooper.start();
		teleopLooper.start();
		autoLooper.stop();
		disabledLooper.stop();

		System.out.println("Default LooperRobot teleopInit() method... Override me!");
	}

	@Override
	public void testInit() {
		// Stop every loop.
		enabledLooper.stop();
		teleopLooper.stop();
		autoLooper.stop();
		disabledLooper.stop();

		System.out.println("Default LooperRobot testInit() method... Override me!");
	}

	/**
	 * Get {@link SuperSubsystem Subsystem} from {@link LooperRobot#robotMap}.
	 * 
	 * @param name
	 * @return subsystem
	 */
	public SuperSubsystem getSubsystem(String name) {
		return robotMap.getSubsystem(name);
	}

	public Stream<SuperSubsystem> getSubsystems() {
		return robotMap.getSubsystems();
	}

	/**
	 * Get {@link JController Joystick} from
	 * 
	 * @param name
	 * @return
	 */
	public JController getJoystick(String name) {
		return joysticks.get(name);
	}

	/**
	 * Method to run {@link JController JControllers} intialization code before
	 * {@link SuperSubsystem Subsystems}. Make sure to add each declared
	 * {@link JController} to the {@link LooperRobot#joysticks joysticks} List.
	 */
	public abstract void initJoysticks();
}