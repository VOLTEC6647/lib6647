package org.usfirst.lib6647.oi;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.usfirst.lib6647.subsystem.RobotMap;

/**
 * Class holding instances of objects required to read values from a JSON file,
 * for {@link JController} usage.
 */
public class ControllerProfiles {

	private static ControllerProfiles m_instance = null;

	/**
	 * Creates static {@link ControllerProfiles} instance.
	 */
	public static void createInstance(String filePath) {
		m_instance = new ControllerProfiles(filePath);
	}

	/**
	 * Gets static {@link ControllerProfiles} instance.
	 * 
	 * @return static {@link ControllerProfiles} instance
	 */
	public static ControllerProfiles getInstance() {
		return m_instance;
	}

	/** JSON {@link ObjectMapper} for {@link JController JControllers}. */
	private final ObjectMapper mapper;
	/** Path to JSON file. */
	private final String filePath;

	/**
	 * Constructor for {@link RobotMap}. Initializes {@link #filePath} and
	 * {@link #mapper}.
	 * 
	 * @param filePath
	 */
	public ControllerProfiles(String filePath) {
		this.filePath = filePath;
		mapper = new ObjectMapper();
	}

	/**
	 * Get {@link ObjectMapper} instance.
	 * 
	 * @return mapper
	 */
	public ObjectMapper getMapper() {
		return mapper;
	}

	/**
	 * Get {@link #filePath} for JSON file.
	 * 
	 * @return filePath
	 */
	public String getFilePath() {
		return filePath;
	}
}