package org.usfirst.lib6647.subsystem.hypercomponents;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;

/**
 * Simple Wrapper for a {@link CANSparkMax}, only implements the
 * {@link Sendable} interface for now. Also includes its own
 * {@link CANPIDController} and {@link CANEncoder CANEncoders}, more can be
 * added in the future if needed.
 */
public class HyperSparkMax extends CANSparkMax implements Sendable {
	/** The {@link HyperSparkMax}'s {@link CANPIDController} instance. */
	private CANPIDController controller;
	/** The {@link HyperSparkMax}'s {@link CANEncoder} instances. */
	private CANEncoder encoder, alternateEncoder;

	/**
	 * HyperComponent Wrapper for {@link WPI_TalonSRX}.
	 * 
	 * @param name     The {@link HyperSparkMax}'s name
	 * @param deviceID The {@link HyperSparkMax}'s port (set via REV's SparkMax
	 *                 Client)
	 * @param type     The {@link MotorType type of motor} (either Brushed or
	 *                 Brushless)
	 */
	public HyperSparkMax(String name, int deviceID, MotorType type) {
		super(deviceID, type);

		SendableRegistry.setName(this, name);

		controller = super.getPIDController();
		encoder = super.getEncoder();
		alternateEncoder = super.getAlternateEncoder();
	}

	@Override
	public CANPIDController getPIDController() {
		return controller;
	}

	@Override
	public CANEncoder getEncoder() {
		return encoder;
	}

	@Override
	public CANEncoder getAlternateEncoder() {
		return alternateEncoder;
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		// TODO: Add simulation support to HyperSparkMax

		builder.setSmartDashboardType("Speed Controller");
		builder.setSafeState(this::stopMotor);
		builder.addDoubleProperty("Value", this::get, this::set);
	}
}