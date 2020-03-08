package org.usfirst.lib6647.subsystem.hypercomponents;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.AlternateEncoderType;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.EncoderType;

import edu.wpi.first.hal.SimBoolean;
import edu.wpi.first.hal.SimDevice;
import edu.wpi.first.hal.SimDouble;
import edu.wpi.first.wpilibj.RobotController;
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

	private SimDevice simDevice;
	private SimDouble simSpeed;
	private SimBoolean simInvert;

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
		SendableRegistry.addLW(this, "Spark Max ", deviceID);

		controller = super.getPIDController();
		alternateEncoder = super.getAlternateEncoder();

		simDevice = SimDevice.create("Spark Max", deviceID);
		if (simDevice != null) {
			simSpeed = simDevice.createDouble("Motor Output", false, 0.0);
			simInvert = simDevice.createBoolean("Inverted?", false, false);
		}
	}

	// TODO: Comment this.

	@Override
	public void setInverted(boolean inverted) {
		super.setInverted(inverted);
		if (simInvert != null)
			simInvert.set(inverted);
	}

	@Override
	public double get() {
		return simSpeed != null ? simSpeed.get() : super.get();
	}

	@Override
	public void set(double speed) {
		super.set(speed);
		simSet(speed);
	}

	@Override
	public void setVoltage(double outputVolts) {
		super.setVoltage(outputVolts);
		simSet(outputVolts / RobotController.getBatteryVoltage());
	}

	private void simSet(double speed) {
		if (simSpeed != null && simInvert != null)
			simSpeed.set(speed * (simInvert.get() ? -1 : 1));
	}

	@Override
	public CANPIDController getPIDController() {
		return controller;
	}

	public void setEncoder(CANEncoder encoder) {
		this.encoder = encoder;
	}

	public void setEncoder(EncoderType type, int countsPerRev) {
		encoder = super.getEncoder(type, countsPerRev);
	}

	public void setEncoder() {
		encoder = super.getEncoder();
	}

	@Override
	public CANEncoder getEncoder() {
		return encoder;
	}

	public void setAlternateEncoder(CANEncoder alternateEncoder) {
		this.alternateEncoder = alternateEncoder;
	}

	public void setAlternateEncoder(AlternateEncoderType type, int countsPerRev) {
		alternateEncoder = super.getAlternateEncoder(type, countsPerRev);
	}

	public void setAlternateEncoder() {
		alternateEncoder = super.getAlternateEncoder();
	}

	@Override
	public CANEncoder getAlternateEncoder() {
		return alternateEncoder;
	}

	@Override
	public void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("Speed Controller");
		builder.setSafeState(this::stopMotor);
		builder.addDoubleProperty("Value", this::get, this::set);
	}
}