package org.usfirst.lib6647.oi;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

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
	 * {@link JSONNode} for usage of friendly button names with JSON.
	 */
	private JsonNode profile;

	/**
	 * Constructor for {@link JController}.
	 * 
	 * Initializes each and every {@link Button} from the {@link Joystick} found at
	 * the given port. Also initializes {@link Button Buttons} for each of the axes
	 * and POVs. Also initializes {@link #profile} {@link JsonNode} if possible.
	 * 
	 * @param port
	 */
	public JController(int port) {
		super(port);

		try (Reader file = new FileReader(ControllerProfiles.getInstance().getFilePath())) {
			profile = ControllerProfiles.getInstance().getMapper().readTree(file).get(getName());
		} catch (Exception e) {
			System.out.println(
					"[!] COULD NOT INITIALIZE CONTROLLER PROFILE FOR CONTROLLER '" + getName().toUpperCase() + "'.");
		}

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
	 * Method for getting a {@link Button} with a friendly name (declared in the
	 * JSON configuration) from this {@link JController}.
	 * 
	 * @param buttonName
	 * @return {@link Button}
	 */
	public Button get(String buttonName) {
		return buttons.get(profile.get(buttonName).asText());
	}

	/**
	 * Method for getting a {@link Button} from the {@link JController}.
	 * 
	 * @param button
	 * @return button from the given {@link JController#joysticks joystick}
	 */
	public Button get(int button) {
		return buttons.get("Button" + button);
	}

	/**
	 * Method for getting a {@link Button} from either a Stick or dPad from the
	 * {@link JController}.
	 * 
	 * @param type
	 * @param axis
	 * @return {@link Button} from the {@link JController}, for the given Stick or
	 *         dPad
	 */
	public Button get(String type, int axis) {
		return buttons.get(type + axis);
	}

	/**
	 * Method for getting a {@link Button} from either a Stick or dPad from the
	 * {@link JController}, at a specific angle or value.
	 * 
	 * @param type
	 * @param axis
	 * @param angle
	 * @return {@link Button} from the {@link JController}, for the given Stick or
	 *         dPad, for the given angle or value
	 */
	public Button get(String type, int axis, int angle) {
		return buttons.get(type + axis + "_" + angle);
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
	 * Method for getting an axisButton input for any value, with 0.30 as tolerance
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
				return Math.abs(controller.getRawAxis(axis)) < 0.30;
			}
		};
	}

	/**
	 * Method for getting a negative axisButton input, with -0.30 as tolerance (to
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
				return controller.getRawAxis(axis) < -0.30;
			}
		};
	}

	/**
	 * Method for getting a positive axisButton input, with 0.30 as tolerance (to
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
				return controller.getRawAxis(axis) > 0.30;
			}
		};
	}
}