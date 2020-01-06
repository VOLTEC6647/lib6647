package org.usfirst.lib6647.loops;

import java.util.HashMap;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.usfirst.lib6647.oi.JController;
import org.usfirst.lib6647.subsystem.RobotMap;
import org.usfirst.lib6647.subsystem.SuperSubsystem;
import org.usfirst.lib6647.util.JSONReader;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

/**
 * Implementation of Team 254's {@link Looper Loopers} within a
 * {@link TimedRobot}. It features three {@link Looper} instances which run
 * whenever the robot is either enabled or disabled, or periodically.
 */
public abstract class LooperRobot extends TimedRobot {
	/** The {@link LooperRobot}'s main {@link Looper Loopers}. */
	private final Looper enabledLooper = new Looper(), disabledLooper = new Looper(), periodicLooper = new Looper();
	/** Final instance of {@link RobotMap}. */
	private final RobotMap robotMap = new RobotMap();
	/** HashMap holding initialized {@link JController joysticks}. */
	protected HashMap<String, JController> joysticks = new HashMap<>();

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

		for (Supplier<T> s : subsystems)
			robotMap.registerSubsystem(s.get());
	}

	@Override
	public void robotInit() {
		// Registers each Loop in every declared subsystem.
		robotMap.registerEnabledLoops(enabledLooper);
		robotMap.registerDisabledLoops(disabledLooper);
		robotMap.registerPeriodicLoops(periodicLooper);

		periodicLooper.start();

		System.out.println("Default LooperRobot robotInit() method... Override me!");
	}

	@Override
	public void robotPeriodic() {
		CommandScheduler.getInstance().run();
	}

	@Override
	public void disabledInit() {
		enabledLooper.stop();
		disabledLooper.start();

		System.out.println("Default LooperRobot disabledInit() method... Override me!");
	}

	@Override
	public void autonomousInit() {
		disabledLooper.stop();
		enabledLooper.start();

		System.out.println("Default LooperRobot autonomousInit() method... Override me!");
	}

	@Override
	public void teleopInit() {
		disabledLooper.stop();
		enabledLooper.start();

		System.out.println("Default LooperRobot teleopInit() method... Override me!");
	}

	@Override
	public void testInit() {
		disabledLooper.stop();
		enabledLooper.stop();

		System.out.println("Default LooperRobot testInit() method... Override me!");
	}

	public SuperSubsystem getSubsystem(String name) {
		return robotMap.getSubsystem(name);
	}

	public Stream<SuperSubsystem> getSubsystems() {
		return robotMap.getSubsystems();
	}

	public JController getJoystick(String name) {
		return joysticks.get(name);
	}
}