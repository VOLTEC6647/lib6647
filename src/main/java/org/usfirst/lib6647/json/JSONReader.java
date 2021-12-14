package org.usfirst.lib6647.json;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wpi.first.wpilibj.Filesystem;

/**
 * Class for handling the reading of specified JSON files.
 */
public class JSONReader {
	/** Jackson's {@link ObjectMapper}, only one instance required. */
	private static final ObjectMapper mapper = new ObjectMapper();
	/**
	 * Map holding each of the JSON files to be read, with its name as its key.
	 * 
	 * <p>
	 * E.g. a file path such as '/home/lvuser/deploy/Profiles.json' has a key of
	 * 'Profiles'.
	 */
	private static final Map<String, String> filePaths = new HashMap<>();

	/** Static instance for the {@link JSONReader} */
	private static JSONReader instance = null;

	/**
	 * Creates static {@link JSONReader} instance.
	 * 
	 * @param fileNames Every JSON file's names, without extension
	 */
	public static void createInstance(final String... fileNames) {
		instance = new JSONReader(fileNames);
	}

	/**
	 * Gets static {@link JSONReader} instance.
	 * 
	 * @return The static {@link JSONReader} instance
	 */
	public static synchronized JSONReader getInstance() {
		return instance;
	}

	/**
	 * Must be instantiated with each name of every JSON file that goes into the
	 * roboRIO's /home/lvuser/deploy directory.
	 * 
	 * @param fileNames Every JSON file's names, without extension
	 */
	private JSONReader(final String... fileNames) {
		for (final String fileName : fileNames)
			putFile(fileName);
	}

	/**
	 * Get a {@link JsonNode} from one of the JSON files in {@link #filePaths}.
	 * 
	 * @param fileName A JSON file's name, without extension
	 * @param nodeName The node to be read
	 * @return The {@link JsonNode} object
	 * @throws JSONInitException When a JSON file does not exist, or is not
	 *                           readable/writeable.
	 */
	public synchronized JsonNode getNode(String fileName, String nodeName) throws JSONInitException {
		try (Reader file = new FileReader(filePaths.get(fileName))) {
			JsonNode node = mapper.readTree(file).get(nodeName);
			return node;
		} catch (FileNotFoundException e) {
			String message = String.format(
					"%n[!] FILE '%s' NOT FOUND, PLEASE MAKE SURE IT EXISTS AND IS NAMED ACCORDINGLY.", fileName);
			throw new JSONInitException(message);
		} catch (IOException e) {
			String message = String.format("%n[!] FILE '%s' CAN NOT BE READ/MODIFIED.", fileName);
			throw new JSONInitException(message);
		}
	}

	/**
	 * Puts a file entry into {@link #filePaths}, with the fileName as its key.
	 * 
	 * @param fileName A JSON file's name, without extension
	 */
	private void putFile(final String fileName) {
		filePaths.putIfAbsent(fileName, String.format("%1$s/%2$s.json", Filesystem.getDeployDirectory(), fileName));
	}
}