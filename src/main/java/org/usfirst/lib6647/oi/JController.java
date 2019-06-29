package org.usfirst.lib6647.oi;

import java.util.HashMap;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * Wrapper for the {@link Joystick} class for easy {@link Button}
 * initialization, and more.
 */
public class JController extends Joystick {

	/**
	 * HashMap storing the {@link JController}'s {@link Button Buttons}.
	 */
	public HashMap<String, Button> buttons = new HashMap<String, Button>();

	/**
	 * Left or right axis of the {@link JController} (assuming it's a gamepad).
	 */
	private int leftAxis = 1, rightAxis = 5;

	/**
	 * Constructor for {@link JController}.
	 * 
	 * Initializes each and every {@link Button} from the {@link Joystick} found at
	 * the given port. Also initializes {@link Button Buttons} for each of the axes
	 * and POVs.
	 * 
	 * @param port
	 */
	public JController(int port) {
		super(port);

		// Button initialization. Starting at 1.
		for (int i = 1; i <= this.getButtonCount(); i++) {
			buttons.put("Button" + i, new JoystickButton(this, i));
		}

		// dPadButton initialization. Starting at 0.
		for (int i = 0; i < this.getPOVCount(); i++) {
			buttons.put("dPad" + i, buttonFromPOV(this, i));
			for (int j = 0; j <= 315; j += 45) {
				buttons.put("dPad" + i + "_" + j, buttonFromPOV(this, i, j));
			}
		}

		// axisButton initialization. Starting at 0.
		for (int i = 0; i < this.getAxisCount(); i++) {
			buttons.put("Stick" + i, buttonFromAxis(this, i));
			buttons.put("Stick" + i + "_1", buttonFromAxisPositive(this, i));
			buttons.put("Stick" + i + "_-1", buttonFromAxisNegative(this, i));
		}
	}

	/**
	 * Method to set {@link #leftAxis} and {@link #rightAxis}.
	 * 
	 * @param {@link #leftAxis}
	 * @param {@link #rightAxis}
	 */
	public void setLeftRightAxis(int leftAxis, int rightAxis) {
		this.leftAxis = leftAxis;
		this.rightAxis = rightAxis;
	}

	/**
	 * Method to get {@link #leftAxis} raw value.
	 * 
	 * @return {@link #leftAxis}
	 */
	public double getLeftAxis() {
		return getRawAxis(leftAxis);
	}

	/**
	 * Method to get {@link #rightAxis} raw value.
	 * 
	 * @return {@link #rightAxis}
	 */
	public double getRightAxis() {
		return getRawAxis(rightAxis);
	}

	/**
	 * Method for getting a dPadButton input for any angle.
	 * 
	 * @param controller
	 * @param pov
	 * @return dPadButton
	 */
	private Button buttonFromPOV(GenericHID controller, int pov) {
		return new Button() {
			@Override
			public boolean get() {
				return controller.getPOV(pov) > -1;
			}
		};
	}

	/**
	 * Method for getting a dPadButton input for a specific angle.
	 * 
	 * @param controller
	 * @param pov
	 * @param angle
	 * @return povButton
	 */
	private Button buttonFromPOV(GenericHID controller, int pov, int angle) {
		return new Button() {
			@Override
			public boolean get() {
				return controller.getPOV(pov) == angle;
			}
		};
	}

	/**
	 * Method for getting an axisButton input for any value, with 0.15 as tolerance
	 * (so as to avoid accidental input due to improper {@link JController}
	 * calibration).
	 * 
	 * @param controller
	 * @param axis
	 * @return axisButton
	 */
	private Button buttonFromAxis(GenericHID controller, int axis) {
		return new Button() {
			@Override
			public boolean get() {
				return Math.abs(controller.getRawAxis(axis)) < 0.15;
			}
		};
	}

	/**
	 * Method for getting a negative axisButton input, with 0.15 as tolerance (to
	 * avoid accidental input due to improper {@link JController} calibration).
	 * 
	 * @param controller
	 * @param axis
	 * @return axisButton
	 */
	private Button buttonFromAxisNegative(GenericHID controller, int axis) {
		return new Button() {
			@Override
			public boolean get() {
				return controller.getRawAxis(axis) < -0.15;
			}
		};
	}

	/**
	 * Method for getting a positive axisButton input, with 0.15 as tolerance (to
	 * avoid accidental input due to improper {@link JController} calibration).
	 * 
	 * @param controller
	 * @param axis
	 * @return axisButton
	 */
	private Button buttonFromAxisPositive(GenericHID controller, int axis) {
		return new Button() {
			@Override
			public boolean get() {
				return controller.getRawAxis(axis) > 0.15;
			}
		};
	}
}