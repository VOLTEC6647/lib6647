package org.usfirst.lib6647.subsystem;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Class holding instances of objects required to read values from a JSON file,
 * for {@link SuperSubsystem} usage.
 */
public class RobotMap {

	private static RobotMap m_instance = null;

	/**
	 * Creates static {@link RobotMap} instance.
	 */
	public static void createInstance(String filePath) {
		m_instance = new RobotMap(filePath);
	}

	/**
	 * Gets static {@link RobotMap} instance.
	 * 
	 * @return static {@link RobotMap} instance
	 */
	public static RobotMap getInstance() {
		return m_instance;
	}

	/** JSON {@link ObjectMapper} for {@link SuperSubsystem SuperSubsystems}. */
	private final ObjectMapper mapper;
	/** Path to JSON file. */
	private final String filePath;

	/**
	 * Constructor for {@link RobotMap}. Initializes {@link #filePath} and
	 * {@link #mapper}.
	 * 
	 * @param filePath
	 */
	private RobotMap(final String filePath) {
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