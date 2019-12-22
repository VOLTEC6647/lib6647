package org.usfirst.lib6647.util;

import java.io.Reader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wpi.first.wpilibj.Filesystem;

/**
 * Class for handling the reading of specified JSON files.
 */
public class JSONReader {

	/** Static instance for the {@link JSONReader} */
	private static JSONReader instance = null;

	/**
	 * Creates static {@link JSONReader} instance.
	 */
	public static void createInstance(final String... fileNames) {
		instance = new JSONReader(fileNames);
	}

	/**
	 * Gets static {@link JSONReader} instance.
	 * 
	 * @return static {@link JSONReader} instance
	 */
	public static JSONReader getInstance() {
		return instance;
	}

	/** Jackson's {@link ObjectMapper}, only one instance required. */
	private static final ObjectMapper mapper = new ObjectMapper();
	/**
	 * Map holding each of the JSON files to be read, with its name as its key. e.g.
	 * a file path such as '/home/lvuser/deploy/Profiles.json' has a key of
	 * 'Profiles'.
	 */
	private static final Map<String, String> filePaths = new HashMap<String, String>();

	/**
	 * Must be instantiated with each name of every JSON file that goes into the
	 * roboRIO's /home/lvuser/deploy directory.
	 * 
	 * @param fileNames
	 */
	private JSONReader(final String... fileNames) {
		for (final String fileName : fileNames)
			putFile(fileName);
	}

	/**
	 * Get a {@link JsonNode} from one of the JSON files in {@link #filePaths}.
	 * 
	 * @param fileName
	 * @param nodeName
	 * @return JsonNode
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public JsonNode getNode(String fileName, String nodeName) throws FileNotFoundException, IOException {
		try (Reader file = new FileReader(filePaths.get(fileName))) {
			return mapper.readTree(file).get(nodeName);
		} catch (FileNotFoundException e) {
			System.out.printf("\n[!] FILE '%s' NOT FOUND.");
			throw e;
		} catch (IOException e) {
			System.out.printf("\n[!] FILE '%s' CAN NOT BE READ/MODIFIED.");
			throw e;
		}
	}

	/**
	 * Puts a file entry into {@link #filePaths}, with the fileName as its key.
	 * 
	 * @param fileName
	 */
	public void putFile(final String fileName) {
		filePaths.putIfAbsent(fileName, String.format("%s/%s.json", Filesystem.getDeployDirectory(), fileName));
	}
}