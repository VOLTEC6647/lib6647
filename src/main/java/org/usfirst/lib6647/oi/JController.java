package org.usfirst.lib6647.oi;

import java.util.HashMap;

import com.fasterxml.jackson.databind.JsonNode;

import org.usfirst.lib6647.util.JSONInitException;
import org.usfirst.lib6647.util.JSONReader;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.Button;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * Wrapper for the {@link GenericHID} class, for easy {@link Button}
 * initialization and increased controller compatibility.
 */
public class JController extends GenericHID {
	/** HashMap storing the {@link JController}'s {@link Button Buttons}. */
	private final HashMap<String, Button> buttons = new HashMap<>();
	/** Tolerance for this {@link JController}'s axes. */
	private double axisTolerance = 0.15;
	/** This {@JController}'s axes' values. */
	private int leftX = 0, leftY = 1, rightX = 4, rightY = 5;

	/**
	 * {@link JsonNode} holding the {@link JController}'s profile (friendly
	 * {@link Button} names).
	 */
	private JsonNode profile;

	/**
	 * Constructor for a {@link JController}.
	 * 
	 * Initializes each and every {@link Button} from the {@link Joystick} found at
	 * the given port, and initializes {@link Button Buttons} for each of the
	 * {@link JController}'s axes and POVs with the default tolerance of 0.15. Also
	 * initializes its {@link #profile} if possible.
	 * 
	 * @param port
	 */
	public JController(int port) {
		this(port, 0.15);
	}

	/**
	 * Constructor for a {@link JController}.
	 * 
	 * Initializes each and every {@link Button} from the {@link Joystick} found at
	 * the given port, and initializes {@link Button Buttons} for each of the
	 * {@link JController}'s axes and POVs with the specified tolerance. Also
	 * initializes its {@link #profile} if possible.
	 * 
	 * @param port
	 * @param axisTolerance
	 */
	public JController(int port, double axisTolerance) {
		super(port);

		// Set tolerance of JController to specified amount.
		this.axisTolerance = axisTolerance;

		try {
			profile = JSONReader.getInstance().getNode("Profiles", getName());
		} catch (JSONInitException e) {
			String error = String.format(
					"[!] COULD NOT INITIALIZE CONTROLLER PROFILE FOR CONTROLLER '%1$s', USER-FRIENDLY NAMES WON'T WORK!\n\t%2$s",
					getName().toUpperCase(), e.getLocalizedMessage());

			System.out.println(error);
			DriverStation.reportWarning(error, false);
		}

		// Button initialization. Starting at 1.
		for (int i = 1; i <= this.getButtonCount(); i++)
			buttons.put("Button" + i, new JoystickButton(this, i));

		// dPadButton initialization. Starting at 0.
		for (int i = 0; i < this.getPOVCount(); i++) {
			buttons.put("dPad" + i, buttonFromPOV(i));
			for (int j = 0; j <= 315; j += 45)
				buttons.put("dPad" + i + "_" + j, buttonFromPOV(i, j));
		}

		// axisButton (Stick) initialization. Starting at 0.
		for (int i = 0; i < this.getAxisCount(); i++) {
			buttons.put("Stick" + i, buttonFromAxis(i));
			buttons.put("Stick" + i + "_1", buttonFromAxis(i, false));
			buttons.put("Stick" + i + "_-1", buttonFromAxis(i, true));
		}
	}

	@Override
	public double getRawAxis(int axis) {
		return getRawAxis(axis, false);
	}

	/**
	 * Get the value of the axis, with or without tolerance.
	 *
	 * @param axis         The axis to read, starting at 0.
	 * @param useTolerance Whether to apply tolerance or not.
	 * @return The value of the axis.
	 */
	public double getRawAxis(int axis, boolean useTolerance) {
		var ds = DriverStation.getInstance();
		return (!useTolerance || (Math.abs(ds.getStickAxis(getPort(), axis)) > axisTolerance))
				? ds.getStickAxis(getPort(), axis)
				: 0.0;
	}

	/**
	 * Method for getting a {@link Button} with a friendly name (declared in the
	 * JSON configuration) from this {@link JController}.
	 * 
	 * @param buttonName
	 * @return {@link Button}.
	 */
	public Button get(String buttonName) {
		return buttons.get(profile.get(buttonName).asText());
	}

	/**
	 * Method for getting a {@link Button} from the {@link JController}.
	 * 
	 * @param button
	 * @return {@link Button} from the {@link JController}'s {@link #buttons}
	 *         HashMap.
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
	 * @return {@link Button} from the {@link JController}'s {@link #buttons}
	 *         HashMap, for the specified Stick or dPad.
	 */
	public Button get(JAxisType type, int axis) {
		return buttons.get(type.getName() + axis);
	}

	/**
	 * Method for getting a {@link Button} from either a Stick or dPad from the
	 * {@link JController}, at a specific angle or value.
	 * 
	 * @param type
	 * @param axis
	 * @param angle
	 * @return {@link Button} from the {@link JController}'s {@link #buttons}
	 *         HashMap, for the specified Stick or dPad, for the specified angle or
	 *         value
	 */
	public Button get(JAxisType type, int axis, int angle) {
		return buttons.get(type.getName() + axis + "_" + angle);
	}

	/**
	 * Method for getting a dPadButton input for any angle.
	 * 
	 * @param pov
	 * @return dPadButton
	 */
	private Button buttonFromPOV(int pov) {
		return new Button() {
			@Override
			public boolean get() {
				return getPOV(pov) > -1;
			}
		};
	}

	/**
	 * Method for getting a dPadButton input for a specific angle.
	 * 
	 * @param pov
	 * @param angle
	 * @return povButton
	 */
	private Button buttonFromPOV(int pov, int angle) {
		return new Button() {
			@Override
			public boolean get() {
				return getPOV(pov) == angle;
			}
		};
	}

	/**
	 * Method for getting an {@link Button axisButton} for any absolute value, with
	 * given value as tolerance (so as to avoid accidental input due to improper
	 * {@link JController} calibration).
	 * 
	 * @param axis
	 * @param negative
	 * @return axisButton
	 */
	private Button buttonFromAxis(int axis) {
		return new Button() {
			@Override
			public boolean get() {
				return Math.abs(getRawAxis(axis)) < axisTolerance;
			}
		};
	}

	/**
	 * Method for getting an {@link Button axisButton} for either a positive or
	 * negative value, with given value as tolerance (so as to avoid accidental
	 * input due to improper {@link JController} calibration).
	 * 
	 * @param axis
	 * @param negative
	 * @return axisButton
	 */
	private Button buttonFromAxis(int axis, boolean negative) {
		return new Button() {
			@Override
			public boolean get() {
				return getRawAxis(axis) < (axisTolerance * (negative ? -1 : 1));
			}
		};
	}

	/**
	 * Sets both X and Y axes for a specific {@link Hand}.
	 * 
	 * @param hand
	 * @param axisX
	 * @param axisY
	 */
	public void setXY(Hand hand, int axisX, int axisY) {
		setX(hand, axisX);
		setY(hand, axisY);
	}

	/**
	 * Set X axis for a specific {@link Hand}.
	 * 
	 * @param hand
	 * @param axis
	 */
	public void setX(Hand hand, int axis) {
		switch (hand) {
		case kLeft:
			leftX = axis;
			break;
		case kRight:
			rightX = axis;
		default:
		}
	}

	@Override
	public double getX(Hand hand) {
		return getX(hand, true);
	}

	/**
	 * Get X raw axis, for a specific hand, with or without tolerance.
	 * 
	 * @param hand
	 * @param useTolerance
	 * @return x position, with or without tolerance
	 */
	public double getX(Hand hand, boolean useTolerance) {
		switch (hand) {
		case kLeft:
			return getRawAxis(leftX, useTolerance);
		case kRight:
			return getRawAxis(rightX, useTolerance);
		default:
			return Double.NaN;
		}
	}

	/**
	 * Set Y axis for a specific {@link Hand}.
	 * 
	 * @param hand
	 * @param axis
	 */
	public void setY(Hand hand, int axis) {
		switch (hand) {
		case kLeft:
			leftY = axis;
			break;
		case kRight:
			rightY = axis;
		default:
		}
	}

	@Override
	public double getY(Hand hand) {
		return getY(hand, true);
	}

	/**
	 * Get Y raw axis, for a specific hand, with or without tolerance.
	 * 
	 * @param hand
	 * @param useTolerance
	 * @return y position, with or without tolerance
	 */
	public double getY(Hand hand, boolean useTolerance) {
		switch (hand) {
		case kLeft:
			return getRawAxis(leftY, useTolerance);
		case kRight:
			return getRawAxis(rightY, useTolerance);
		default:
			return Double.NaN;
		}
	}
}