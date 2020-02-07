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
	/** This {@link JController}'s axes' values. */
	private int leftX = 0, leftY = 1, rightX = 4, rightY = 5;

	/**
	 * {@link JsonNode} holding the {@link JController}'s profile (friendly
	 * {@link Button} names).
	 */
	private JsonNode profile;

	/**
	 * Constructor for a {@link JController}.
	 * 
	 * <p>
	 * Initializes each and every {@link Button} from the {@link Joystick} found at
	 * the given port, and initializes {@link Button Buttons} for each of the
	 * {@link JController}'s axes and POVs with the default {@link #axisTolerance
	 * tolerance} of 0.15.
	 * 
	 * <p>
	 * Also initializes its {@link #profile} if possible.
	 * 
	 * @param port The port to be read
	 */
	public JController(int port) {
		this(port, 0.15);
	}

	/**
	 * Constructor for a {@link JController}.
	 * 
	 * <p>
	 * Initializes each and every {@link Button} from the {@link Joystick} found at
	 * the given port, and initializes {@link Button Buttons} for each of the
	 * {@link JController}'s axes and POVs with the given {@link #axisTolerance
	 * tolerance} value.
	 * 
	 * <p>
	 * Also initializes its {@link #profile} if possible.
	 * 
	 * @param axisTolerance The amount of {@link #axisTolerance tolerance} to apply
	 *                      to this {@link JController}'s axes
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
	public String getName() {
		return DriverStation.getInstance().getJoystickName(getPort()).trim();
	}

	@Override
	public double getRawAxis(int axis) {
		return getRawAxis(axis, false);
	}

	/**
	 * Get the value of the axis, with or without {@link #axisTolerance tolerance}.
	 *
	 * @param axis         The axis to read, starting at 0
	 * @param useTolerance Whether to apply {@link #axisTolerance tolerance} or not
	 * @return The value of the axis
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
	 * @param buttonName The {@link Button}'s 'friendly' name
	 * @return A {@link Button} that matches the 'friendly' name in the
	 *         {@link JController}'s profile
	 */
	public Button get(String buttonName) {
		return buttons.get(profile.get(buttonName).asText());
	}

	/**
	 * Method for getting a {@link Button} from the {@link JController}.
	 * 
	 * @param button The {@link Button}'s specific number (starting at 1)
	 * @return A {@link Button} from the {@link JController}'s {@link #buttons}
	 *         HashMap
	 */
	public Button get(int button) {
		return buttons.get("Button" + button);
	}

	/**
	 * Method for getting a {@link Button} from either an axis or POV from the
	 * {@link JController}.
	 * 
	 * @param type The {@link JAxisType type of axis} to get
	 * @param axis The value of the axis to be read
	 * @return A {@link Button} from the {@link JController}'s {@link #buttons}
	 *         HashMap, for the specified axis or POV, for any direction or angle
	 */
	public Button get(JAxisType type, int axis) {
		return buttons.get(type.getName() + axis);
	}

	/**
	 * Method for getting a {@link Button} from either an axis or POV from the
	 * {@link JController}, at a specific angle or value.
	 * 
	 * @param type  The {@link JAxisType type of axis} to get
	 * @param axis  The value of the axis to be read
	 * @param angle The angle or direction needed for this {@link Button} to be
	 *              triggered
	 * @return A {@link Button} from the {@link JController}'s {@link #buttons}
	 *         HashMap, for the specified axis or POV, for the specified direction
	 *         or angle
	 */
	public Button get(JAxisType type, int axis, int angle) {
		return buttons.get(type.getName() + axis + "_" + angle);
	}

	/**
	 * Method for getting a POV {@link Button} input for any angle.
	 * 
	 * @param pov The value of the POV
	 * @return A POV {@link Button} that triggers at any angle
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
	 * Method for getting a POV {@link Button} input for a specific angle.
	 * 
	 * @param pov   The value of the POV
	 * @param angle The angle to trigger this Button
	 * @return A POV {@link Button} that triggers ONLY at the specified angle
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
	 * Method for getting an axis {@link Button} for any absolute value.
	 * 
	 * @param axis The value of the axis
	 * @return An axis {@link Button} for any direction (both positive and negative)
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
	 * Method for getting an axis {@link Button} for either a positive or negative
	 * value.
	 * 
	 * @param axis     The value of the axis
	 * @param negative Whether the {@link Button} should trigger on either positive
	 *                 or negative values
	 * @return An axis {@link Button} for the specified direction (either positive
	 *         or negative)
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
	 * Sets both X and Y axes for a specific {@link Hand hand}.
	 * 
	 * @param hand  The hand to be used
	 * @param axisX The value to be read when calling {@link #getX(Hand)}, starting
	 *              at 0
	 * @param axisY The value to be read when calling {@link #getY(Hand)}, starting
	 *              at 0
	 */
	public void setXY(Hand hand, int axisX, int axisY) {
		setX(hand, axisX);
		setY(hand, axisY);
	}

	/**
	 * Set X axis for a specific {@link Hand}.
	 * 
	 * @param hand The hand to be used
	 * @param axis The value to be read when calling {@link #getX(Hand)}, starting
	 *             at 0
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
	 * Get X raw axis, for a specific hand, with or without {@link #axisTolerance
	 * tolerance}.
	 * 
	 * @param hand         The hand to be used
	 * @param useTolerance Whether to apply {@link #axisTolerance tolerance} or not
	 * @return The x position of the given {@link Hand hand}'s axes, with or without
	 *         {@link #axisTolerance tolerance}
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
	 * @param hand The hand to be used
	 * @param axis The value to be read when calling {@link #getY(Hand)}, starting
	 *             at 0
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
	 * Get Y raw axis, for a specific hand, with or without {@link #axisTolerance
	 * tolerance}.
	 * 
	 * @param hand         The hand to be used
	 * @param useTolerance Whether to apply {@link #axisTolerance tolerance} or not
	 * @return The y position of the given {@link Hand hand}'s axes, with or without
	 *         {@link #axisTolerance tolerance}
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

	/**
	 * Get the direction of the vector formed by the joystick and its origin in
	 * radians.
	 *
	 * @param hand The {@link Hand hand} to be used
	 * @return The direction of the vector in radians
	 */
	public double getAngleRadians(Hand hand) {
		return getAngleRadians(hand, false);
	}

	/**
	 * Get the direction of the vector formed by the joystick and its origin in
	 * radians.
	 *
	 * @param hand         The {@link Hand hand} to be used
	 * @param useTolerance Whether to apply {@link #axisTolerance tolerance} or not
	 * @return The direction of the vector in radians
	 */
	public double getAngleRadians(Hand hand, boolean useTolerance) {
		return Math.atan2(getX(hand, useTolerance), -getY(hand, useTolerance));
	}

	/**
	 * Get the direction of the vector formed by the joystick and its origin in
	 * degrees.
	 *
	 * @param hand The {@link Hand hand} to be used
	 * @return The direction of the vector in degrees
	 */
	public double getAngleDegrees(Hand hand) {
		return getAngleDegrees(hand, false);
	}

	/**
	 * Get the direction of the vector formed by the joystick and its origin in
	 * degrees.
	 *
	 * @param hand         The {@link Hand hand} to be used
	 * @param useTolerance Whether to apply {@link #axisTolerance tolerance} or not
	 * @return The direction of the vector in degrees
	 */
	public double getAngleDegrees(Hand hand, boolean useTolerance) {
		return Math.toDegrees(getAngleRadians(hand, useTolerance));
	}
}