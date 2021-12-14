package org.usfirst.lib6647.util;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import net.jafama.FastMath;

public class ControlUtil {
	// TODO: Comment this.

	private ControlUtil() {
	}

	public static double dotProduct(Translation2d vector1, Translation2d vector2) {
		return (vector1.getX() * vector2.getX()) + (vector1.getY() * vector2.getY());
	}

	public static double getMagnitude(Translation2d vector) {
		return FastMath.sqrt((vector.getX() * vector.getX()) + (vector.getY() * vector.getY()));
	}

	public static Translation2d normalize(Translation2d vector) {
		var magnitude = getMagnitude(vector);
		return new Translation2d(vector.getX() / magnitude, vector.getY() / magnitude);
	}
}