package org.usfirst.lib6647.loops;

/**
 * Interface for {@link Looper}.
 * 
 * <p>
 * Copied over from:
 * https://github.com/Team254/FRC-2019-Public/blob/master/src/main/java/com/team254/frc2019/loops/ILooper.java.
 */
public interface ILooper {
	void register(Loop... loops);
}