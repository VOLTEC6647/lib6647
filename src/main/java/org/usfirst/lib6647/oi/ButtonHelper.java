package org.usfirst.lib6647.oi;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.wpi.first.wpilibj.buttons.Button;

/**
 * Helper class for registering {@link Button} input.
 */
public class ButtonHelper {

	/**
	 * HashMap where declared {@link JController joysticks} are stored.
	 */
	public HashMap<String, JController> joysticks;

	/**
	 * Location of the JSON file for {@link Button} nicknames.
	 */
	private String fileName;

	/**
	 * Helper class for registering {@link Button} input.
	 * 
	 * @param fileName
	 */
	public ButtonHelper(String fileName) {
		this.fileName = fileName;

		joysticks = new HashMap<String, JController>();
	}

	/**
	 * Method for getting a {@link Button} with a friendly name (declared in the
	 * JSON configuration) from a given {@link #joysticks joystick}. Returns null if
	 * no {@link Button} is found at that key.
	 * 
	 * @param joystickName
	 * @param buttonName
	 * @return {@link Button}
	 */
	public Button oiButton(String joystickName, String buttonName) {
		try {
			// Create a new JSONParser and JSONObject with the given key.
			JSONParser parser = new JSONParser();
			Reader file = new FileReader(fileName);
			JSONObject jsonJoystick = (JSONObject) ((JSONObject) parser.parse(file))
					.get(joysticks.get(joystickName).getName());

			// Create Button object and initialize it with values from the JSONObject.
			Button button = joysticks.get(joystickName).buttons.get(jsonJoystick.get(buttonName).toString());

			// Clear JSONObject and JSONParser after use, and close Reader. Not sure if it
			// does anything, but it might free some unused memory.
			jsonJoystick.clear();
			file.close();
			parser.reset();

			// Finally, return Button object from the friendly name.
			return button;
		} catch (IOException e) {
			System.out.println("[!] OIBUTTON " + buttonName + " IO ERROR: " + e.getMessage());
		} catch (ParseException e) {
			System.out.println("[!] OIBUTTON " + buttonName + " PARSE ERROR: " + e.getMessage());
		} catch (Exception e) {
			System.out.println("[!] OIBUTTON " + buttonName + " ERROR: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Method for getting a {@link Button} from a given {@link JController#joysticks
	 * joystick}.
	 * 
	 * @param joystickName
	 * @param button
	 * @return button from the given {@link JController#joysticks joystick}
	 */
	public Button oiButton(String joystickName, int button) {
		return joysticks.get(joystickName).buttons.get("Button" + button);
	}

	/**
	 * Method for getting a {@link Button} from an axisButton or dPadButton, from a
	 * given {@link JController#joysticks joystick}.
	 * 
	 * @param joystickName
	 * @param type
	 * @param axis
	 * @return {@link Button} from the given {@link JController#joysticks joystick},
	 *         for the given axis or dPad
	 */
	public Button oiButton(String joystickName, String type, int axis) {
		return joysticks.get(joystickName).buttons.get(type + axis);
	}

	/**
	 * Method for getting a {@link Button} from an axisButton or dPadButton, from a
	 * given {@link JController#joysticks joystick}, at a specific angle.
	 * 
	 * @param joystickName
	 * @param type
	 * @param axis
	 * @param angle
	 * @return {@link Button} from the given {@link JController#joysticks joystick},
	 *         for the given axis or dPad, for the given angle or value
	 */
	public Button oiButton(String joystickName, String type, int axis, int angle) {
		return joysticks.get(joystickName).buttons.get(type + axis + "_" + angle);
	}
}