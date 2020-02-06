package org.usfirst.lib6647.oi;

/**
 * Enum listing possible axis types.
 */
public enum JAxisType {
	STICK("Stick"), DPAD("dPad"), POV("dPad");

	/** The {@link JAxisType axis'} name. */
	private final String name;

	/**
	 * Get the {@link JAxisType axis'} name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Constructor for {@link JAxisType}.
	 * 
	 * @param name
	 */
	JAxisType(String name) {
		this.name = name;
	}
}